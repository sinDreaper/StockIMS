<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.stockx.util.StringUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - ChainX</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/login.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
    <div class="login-container">
        <div class="login-form-container">
            <h2>Sign in</h2>
            <div class="social-login">
                <a href="#" class="social-icon"><i class="fab fa-facebook-f"></i></a>
                <a href="#" class="social-icon"><i class="fab fa-google"></i></a>
                <a href="#" class="social-icon"><i class="fab fa-linkedin-in"></i></a>
            </div>
            <span>or use your email account</span>

            <c:if test="${not empty requestScope[StringUtils.MESSAGE_ERROR]}">
                <p class="error-message">${requestScope[StringUtils.MESSAGE_ERROR]}</p>
            </c:if>
            <c:if test="${param.success == StringUtils.TRUE}">
                <p class="success-message">${StringUtils.MESSAGE_SUCCESS_REGISTER}</p>
            </c:if>

            <form action="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_LOGIN}" method="post">
                <div class="input-group">
                    <label for="email"><i class="fas fa-envelope"></i> Email</label>
                    <input type="email" id="email" name="${StringUtils.EMAIL}" value="${requestScope[StringUtils.EMAIL]}" placeholder="Enter your email" required>
                </div>
                <div class="input-group">
                    <label for="password"><i class="fas fa-lock"></i> Password</label>
                    <input type="password" id="password" name="${StringUtils.PASSWORD}" placeholder="Enter your password" required>
                </div>
                <a href="#" class="forgot-password">Forgot your password?</a>
                <button type="submit" class="login-button">Sign In</button>
            </form>
        </div>

        <div class="login-panel-container">
            <h2>Hello, Friend!</h2>
            <p>Enter your personal details and start your journey with us</p>
            <a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_REGISTER}" class="panel-button">Sign Up</a>
        </div>
    </div>
</body>
</html>
