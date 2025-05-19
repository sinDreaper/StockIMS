<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.stockx.util.StringUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>About Us - ChainX</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/about_us.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/header.css" />

</head>
<body>
     <div class="page-container">
         <%@ include file="header.jsp" %>

         <main class="main-content about-us-content">
            <h1>About Us</h1>

            <section>
                <h2>Our Story</h2>
                <p>Welcome to ChainX. We are dedicated to providing the most comprehensive and user-friendly platform for managing financial assets. Founded in [Year], our mission has always been to empower administrators with the tools they need to efficiently oversee and control stock data, user accounts, and system settings.</p>
                <p>Our journey began with a simple idea: to create an administrative interface that is not only powerful but also intuitive and aesthetically pleasing. We believe that managing complex data shouldn't be a complex task.</p>
            </section>

            <section>
                <h2>Our Mission</h2>
                <p>To deliver a seamless and secure administrative experience, enabling our clients to manage their financial platforms with confidence and precision. We strive for continuous innovation, ensuring our tools are always at the forefront of technology and user needs.</p>
            </section>

             <section>
                <h2>Our Team</h2>
                <p>ChainX Admin is powered by a diverse team of passionate developers, designers, and financial technology experts. We are united by our commitment to excellence and our dedication to supporting our users. While we work behind the scenes, our focus is always on making your administrative tasks easier and more effective.</p>
                <ul>
                    <li>Jane Doe - Lead Architect</li>
                    <li>John Smith - Head of Product</li>
                    <li>Alex Chen - Senior Developer</li>
                    <li>Maria Garcia - UX/UI Lead</li>
                 </ul>
            </section>

            <section>
                <h2>Our Values</h2>
                 <p>Integrity, innovation, and user-centricity are the core pillars of our philosophy. We operate with transparency and are committed to building trust with every interaction.</p>
            </section>

        </main>

         <%@ include file="footer.jsp" %>
    </div>
</body>
</html>