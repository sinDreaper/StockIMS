package com.stockx.model;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

// NOTE: Ensure you have the necessary crypto providers installed if running into issues.
// Standard Java distributions usually include them.
public class PasswordEncryptionWithAes {
    private static final Logger LOGGER = Logger.getLogger(PasswordEncryptionWithAes.class.getName());
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128; // AES-GCM tag length
    private static final int IV_LENGTH_BYTE = 12; // Recommended IV length for GCM
    private static final int SALT_LENGTH_BYTE = 16; // Salt length for PBKDF2
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    // PBKDF2 parameters
    private static final String PBKDF2_ALGO = "PBKDF2WithHmacSHA256";
    private static final int PBKDF2_ITERATIONS = 65536; // Standard iteration count
    private static final int PBKDF2_KEY_LENGTH = 256; // AES-256

    /**
     * Generates a random nonce (IV or Salt).
     * @param numBytes The length of the nonce in bytes.
     * @return The generated nonce.
     */
    public static byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        try {
            SecureRandom.getInstanceStrong().nextBytes(nonce);
        } catch (NoSuchAlgorithmException e) {
            // Fallback to default SecureRandom if Strong not available
            LOGGER.log(Level.WARNING, "SecureRandom.getInstanceStrong() failed, falling back to default.", e);
            new SecureRandom().nextBytes(nonce);
        }
        return nonce;
    }

    /**
     * Generates an AES secret key from a password and salt using PBKDF2.
     * @param password The user's password.
     * @param salt A random salt.
     * @return The derived SecretKey, or null on error.
     */
    public static SecretKey getAESKeyFromPassword(char[] password, byte[] salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGO);
            KeySpec spec = new PBEKeySpec(password, salt, PBKDF2_ITERATIONS, PBKDF2_KEY_LENGTH);
            SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            return secret;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.log(Level.SEVERE, "Error generating AES key from password", e);
            return null;
        }
    }

    /**
     * Encrypts plain text using AES-GCM with a password-derived key.
     * The IV and Salt are prepended to the ciphertext.
     * @param password The plain text password to encrypt.
     * @param keyDerivationContext Typically the user's email or username, used to derive the key.
     * @return Base64 encoded string: IV + Salt + Ciphertext, or null on error.
     */
    public static String encrypt(String password, String keyDerivationContext) {
        if (password == null || keyDerivationContext == null) {
             LOGGER.log(Level.SEVERE, "Password or key derivation context cannot be null for encryption.");
            return null;
        }
        try {
            byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);
            byte[] iv = getRandomNonce(IV_LENGTH_BYTE);

            SecretKey aesKey = getAESKeyFromPassword(keyDerivationContext.toCharArray(), salt);
            if (aesKey == null) return null; // Error logged in getAESKeyFromPassword

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            byte[] cipherText = cipher.doFinal(password.getBytes(UTF_8));

            // Prepend IV and Salt
            byte[] cipherTextWithIvSalt = ByteBuffer.allocate(iv.length + salt.length + cipherText.length)
                    .put(iv)
                    .put(salt)
                    .put(cipherText)
                    .array();

            return Base64.getEncoder().encodeToString(cipherTextWithIvSalt);
        } catch (Exception e) { // Catch broader exceptions from crypto operations
            LOGGER.log(Level.SEVERE, "Encryption failed", e);
            return null;
        }
    }

    /**
     * Decrypts a Base64 encoded ciphertext (IV + Salt + Ciphertext) using AES-GCM.
     * @param base64CipherText The encrypted text (Base64 encoded).
     * @param keyDerivationContext The same context (email/username) used during encryption.
     * @return The original plain text password, or null on error (e.g., bad key, tampered data).
     */
    public static String decrypt(String base64CipherText, String keyDerivationContext) {
         if (base64CipherText == null || keyDerivationContext == null) {
             LOGGER.log(Level.SEVERE, "Ciphertext or key derivation context cannot be null for decryption.");
            return null;
        }
        try {
            byte[] decodedCipher = Base64.getDecoder().decode(base64CipherText.getBytes(UTF_8));

            ByteBuffer bb = ByteBuffer.wrap(decodedCipher);

            // Extract IV
            byte[] iv = new byte[IV_LENGTH_BYTE];
            bb.get(iv);

            // Extract Salt
            byte[] salt = new byte[SALT_LENGTH_BYTE];
            bb.get(salt);

            // Extract Ciphertext
            byte[] cipherText = new byte[bb.remaining()];
            bb.get(cipherText);

            SecretKey aesKey = getAESKeyFromPassword(keyDerivationContext.toCharArray(), salt);
             if (aesKey == null) return null; // Error logged in getAESKeyFromPassword


            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            // Use GCMParameterSpec for decryption as well
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            byte[] plainTextBytes = cipher.doFinal(cipherText);

            return new String(plainTextBytes, UTF_8);
        } catch (Exception e) {
            // Includes AEADBadTagException if integrity check fails
            LOGGER.log(Level.SEVERE, "Decryption failed. Potential reasons: incorrect key, tampered data, or wrong format.", e);
            return null;
        }
    }

     // --- Optional: Main method for testing/generating initial hash ---
    
    public static void main(String[] args) {
        String email = "admin@gmail.com";
        String plainPassword = "admin@12"; // Replace with desired password

        String encryptedPassword = encrypt(plainPassword, email);

        if (encryptedPassword != null) {
            System.out.println("Email: " + email);
            System.out.println("Plain Password: " + plainPassword);
            System.out.println("Encrypted (Hash): " + encryptedPassword);
            System.out.println("\n--- Verification ---");
            String decryptedPassword = decrypt(encryptedPassword, email);
            System.out.println("Decrypted: " + decryptedPassword);
            System.out.println("Match: " + plainPassword.equals(decryptedPassword));
        } else {
             System.out.println("Encryption failed.");
        }
    }
    
}