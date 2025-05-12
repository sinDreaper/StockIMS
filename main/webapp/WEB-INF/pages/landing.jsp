<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.stockx.util.StringUtils" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Welcome to StockX - Stock Information Management</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/landing.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/header.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/footer.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css">
</head>
<body class="landing-body">
    <div class="landing-page-container">

        <jsp:include page="header.jsp" />

        <main class="landing-main">
            <section class="hero-section">
                <h1>Welcome to StockX</h1>
                <p>Your platform for seamless stock information management.</p>
                <div class="cta-buttons">
                    <a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_LOGIN}" class="cta-button primary">Login</a>
                    <a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_REGISTER}" class="cta-button secondary">Register</a>
                </div>
            </section>

            <section class="features-section">
                <h2>Features</h2>
                <div class="feature-grid">
                    <div class="feature-item">
                        <i class="fas fa-chart-line"></i>
                        <h3>Real-time Data</h3>
                        <p>Access up-to-date stock information.</p>
                    </div>
                    <div class="feature-item">
                        <i class="fas fa-user-shield"></i>
                        <h3>Secure Access</h3>
                        <p>Role-based permissions for admins and users.</p>
                    </div>
                    <div class="feature-item">
                        <i class="fas fa-tasks"></i>
                        <h3>Easy Management</h3>
                        <p>Intuitive interface for stock administration.</p>
                    </div>
                </div>
            </section>
        </main>

        <jsp:include page="footer.jsp" />
    </div>
</body>
</html>
