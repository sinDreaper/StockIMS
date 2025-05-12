<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.stockx.util.StringUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Contact Us - ChainX</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/contact_us.css" />
     <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
     <div class="page-container">
         <%@ include file="header.jsp" %>

         <main class="main-content contact-us-content">
            <h1>Contact Us</h1>
            <p class="intro-text">We're here to help and answer any question you might have. We look forward to hearing from you!</p>

             <%-- Display Messages --%>
            <c:if test="${not empty requestScope[StringUtils.MESSAGE_SUCCESS]}">
                <p class="message success-message">${requestScope[StringUtils.MESSAGE_SUCCESS]}</p>
            </c:if>
            <c:if test="${not empty requestScope[StringUtils.MESSAGE_ERROR]}">
                <p class="message error-message">${requestScope[StringUtils.MESSAGE_ERROR]}</p>
            </c:if>

            <div class="contact-info-grid">
                <div class="info-block">
                    <i class="fas fa-map-marker-alt"></i>
                    <h3>Our Office</h3>
                    <p>123 Finance Street<br>Innovation City, ST 54321<br>United States</p>
                </div>
                 <div class="info-block">
                     <i class="fas fa-envelope"></i>
                    <h3>Email Us</h3>
                    <p>Support: <a href="mailto:support@chainxadmin.com">support@chainxadmin.com</a></p>
                    <p>Sales: <a href="mailto:sales@chainxadmin.com">sales@chainxadmin.com</a></p>
                </div>
                 <div class="info-block">
                    <i class="fas fa-phone-alt"></i>
                    <h3>Call Us</h3>
                    <p>Main Line: +1 (555) 123-4567</p>
                    <p>Support Line: +1 (555) 987-6543</p>
                </div>
            </div>

            <section class="contact-form-section">
                <h2>Send Us a Message</h2>
                 <form action="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_CONTACT_US}" method="post">
                     <div class="form-group">
                        <label for="fullName">Full Name</label>
                        <input type="text" id="fullName" name="fullName" required>
                     </div>
                      <div class="form-group">
                        <label for="emailAddress">Email Address</label>
                        <input type="email" id="emailAddress" name="emailAddress" required>
                     </div>
                      <div class="form-group">
                        <label for="subject">Subject</label>
                        <input type="text" id="subject" name="subject" required>
                     </div>
                      <div class="form-group">
                        <label for="yourMessage">Your Message</label>
                        <textarea id="yourMessage" name="yourMessage" rows="6" required></textarea>
                     </div>
                     <button type="submit" class="submit-button">Send Message</button>
                </form>
            </section>

        </main>

         <%@ include file="footer.jsp" %>
    </div>
</body>
</html>