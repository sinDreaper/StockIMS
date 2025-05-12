<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<head>
     <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/footer.css" />
      <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<footer class="app-footer">
    <p>© <%= java.time.Year.now().getValue() %> StockX. All rights reserved.</p>
    <div class="footer-links">
        <a href="${pageContext.request.contextPath}/aboutus">About Us</a> |
        <a href="${pageContext.request.contextPath}/contactus">Contact Us</a> |
        <a href="#">Privacy Policy</a> |
        <a href="#">Terms of Service</a>
    </div>
     <div class="social-media">
        <a href="#" aria-label="Facebook"><i class="fab fa-facebook-f"></i></a>
        <a href="#" aria-label="Twitter"><i class="fab fa-twitter"></i></a>
        <a href="#" aria-label="LinkedIn"><i class="fab fa-linkedin-in"></i></a>
    </div>
</footer>