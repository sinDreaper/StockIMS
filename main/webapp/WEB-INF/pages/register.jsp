<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.stockx.util.StringUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register - ChainX</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/register.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
    <div class="register-container">
        <div class="register-panel-container">
            <h2>Welcome Back!</h2>
            <p>To keep connected with us please login with your personal info</p>
            <a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_LOGIN}" class="panel-button">Sign In</a>
        </div>
        <div class="register-form-container">
            <h2>Create Account</h2>
            <div class="social-login">
                <a href="#" class="social-icon"><i class="fab fa-facebook-f"></i></a>
                <a href="#" class="social-icon"><i class="fab fa-google"></i></a>
                <a href="#" class="social-icon"><i class="fab fa-linkedin-in"></i></a>
            </div>
            <span>or use your email for registration</span>

            <c:if test="${not empty requestScope[StringUtils.MESSAGE_ERROR]}">
                <p class="error-message">${requestScope[StringUtils.MESSAGE_ERROR]}</p>
            </c:if>

            <form action="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_REGISTER}" method="post">
                <div class="input-group">
                    <label for="name"><i class="fas fa-user"></i> Name</label>
                    <input type="text" id="name" name="${StringUtils.USER_NAME}" value="${requestScope[StringUtils.USER_NAME]}" placeholder="Enter your full name" required>
                </div>
                <div class="input-group">
                    <label for="email"><i class="fas fa-envelope"></i> Email</label>
                    <input type="email" id="email" name="${StringUtils.EMAIL}" value="${requestScope[StringUtils.EMAIL]}" placeholder="Enter your email" required>
                </div>
                <div class="input-group">
                    <label for="password"><i class="fas fa-lock"></i> Password</label>
                    <input type="password" id="password" name="${StringUtils.PASSWORD}" placeholder="Create a password (min 6 chars)" required>
                </div>
                <div class="input-group">
                    <label for="retypePassword"><i class="fas fa-lock"></i> Confirm Password</label>
                    <input type="password" id="retypePassword" name="${StringUtils.RETYPE_PASSWORD}" placeholder="Retype your password" required>
                </div>
                <div class="input-group optional">
                    <label for="dob"><i class="fas fa-calendar-alt"></i> Date of Birth (Optional)</label>
                    <input type="date" id="dob" name="${StringUtils.DOB}" value="${requestScope[StringUtils.DOB]}">
                </div>
                <div class="input-group optional">
                    <label for="contactNumber"><i class="fas fa-phone"></i> Contact Number (Optional)</label>
                    <input type="tel" id="contactNumber" name="${StringUtils.CONTACT_NUMBER}" value="${requestScope[StringUtils.CONTACT_NUMBER]}" placeholder="Enter contact number">
                </div>

                <button type="submit" class="register-button">Sign Up</button>
            </form>
        </div>
    </div>
</body>
</html>
