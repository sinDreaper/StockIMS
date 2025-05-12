<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.stockx.util.StringUtils" %>
<%@ page import="com.stockx.model.CustomerModel" %>
<%@ page import="com.stockx.model.AdminModel" %>

<c:set var="loggedInRole" value="${sessionScope[StringUtils.SESSION_USER_ROLE]}" />

<c:choose>
    <c:when test="${loggedInRole == StringUtils.ROLE_ADMIN && not empty user}">
        <c:set var="userName" value="${user.adminName}" />
        <c:set var="userEmail" value="${user.adminEmail}" />
        <c:set var="userContact" value="${user.adminContact}" />
    </c:when>
    <c:when test="${loggedInRole == StringUtils.ROLE_CUSTOMER && not empty user}">
        <c:set var="userName" value="${user.customerName}" />
        <c:set var="userEmail" value="${user.customerEmail}" />
        <c:set var="userContact" value="${user.customerContact}" />
    </c:when>
    <c:otherwise>
        <c:set var="userName" value="User" />
        <c:set var="userEmail" value="" />
        <c:set var="userContact" value="" />
    </c:otherwise>
</c:choose>

<c:set var="initials" value="?" />
<c:if test="${not empty userName}">
    <c:set var="nameParts" value="${fn:split(userName, ' ')}" />
    <c:choose>
        <c:when test="${fn:length(nameParts) >= 2}">
            <c:set var="initials" value="${fn:substring(nameParts[0], 0, 1)}${fn:substring(nameParts[fn:length(nameParts)-1], 0, 1)}" />
        </c:when>
        <c:when test="${fn:length(nameParts) == 1 && fn:length(nameParts[0]) > 0}">
            <c:set var="initials" value="${fn:substring(nameParts[0], 0, 1)}" />
        </c:when>
    </c:choose>
    <c:set var="initials" value="${fn:toUpperCase(initials)}" />
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Profile - ChainX</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/user_profile.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/header.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/footer.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
    <div class="profile-page-container">
        <jsp:include page="header.jsp" />

        <main class="profile-main-content">
            <c:if test="${not empty sessionScope.successMessage}">
                <p class="message success-message">${sessionScope.successMessage}</p>
                <c:remove var="successMessage" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.errorMessage}">
                <p class="message error-message">${sessionScope.errorMessage}</p>
                <c:remove var="errorMessage" scope="session" />
            </c:if>
            <c:if test="${not empty requestScope.errorMessage}">
                <p class="message error-message">${requestScope.errorMessage}</p>
            </c:if>

            <c:if test="${not empty user}">
                <div class="profile-header">
                    <div class="profile-avatar">${initials}</div>
                    <div class="profile-info">
                        <h1><c:out value="${userName}" /></h1>
                        <p><c:out value="${userEmail}" /></p>
                    </div>
                </div>

                <section class="profile-form-section">
                    <h2>Personal Information</h2>
                    <form action="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_USER_PROFILE}" method="post">
                        <div class="form-grid">
                            <div>
                                <label for="fullName">Full Name</label>
                                <input type="text" id="fullName" name="fullName" value="<c:out value='${userName}' />" required>
                            </div>
                            <div>
                                <label for="emailAddress">Email Address</label>
                                <input type="email" id="emailAddress" name="emailAddress" value="<c:out value='${userEmail}' />" disabled>
                            </div>
                            <div>
                                <label for="phone">Phone Number (Optional)</label>
                                <input type="tel" id="phone" name="${StringUtils.CONTACT_NUMBER}" value="<c:out value='${userContact}' />">
                            </div>
                        </div>

                        <div class="form-full-width">
                            <label for="aboutMe">About Me (Optional)</label>
                            <textarea id="aboutMe" name="aboutMe" rows="4"></textarea>
                        </div>

                        <div class="form-actions">
                            <button type="button" class="cancel-button" onclick="window.history.back();">Cancel</button>
                            <button type="submit" class="save-button">Save Changes</button>
                        </div>
                    </form>
                </section>
            </c:if>

            <c:if test="${empty user}">
                <p class="error-message">Could not load user profile data.</p>
            </c:if>
        </main>

        <jsp:include page="footer.jsp" />
    </div>
</body>
</html>
