<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> <%-- Added for string functions like endsWith --%>
<%@ page import="com.stockx.util.StringUtils" %>

<%--
    This header is included in various pages.
    It uses sessionScope attributes set during login (or cleared on logout)
    to determine what to display.
    It relies on 'header.css' for styling.
--%>

<header class="app-header">
    
    <div class="header-left">
        <a href="${pageContext.request.contextPath}/" class="logo">StockX</a>
        
        
        <c:if test="${marketOpen}"> <span class="market-status open">Market Open</span> </c:if>
        <c:if test="${!marketOpen}"> <span class="market-status closed">Market Closed</span> </c:if>
       
    </div>

    <%-- Center Section: Main Navigation --%>
    <div class="header-center">
        <nav class="main-nav">
            <ul>
                <%-- Dashboard link role based--%>
                <c:if test="${not empty sessionScope[StringUtils.SESSION_USER_EMAIL]}">
                    <c:set var="dashboardUrl" value="" />
                    <c:set var="dashboardActive" value="${false}" />

                    <c:choose>
                        <c:when test="${sessionScope[StringUtils.SESSION_USER_ROLE] == StringUtils.ROLE_ADMIN}">
                            <c:set var="dashboardUrl" value="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_ADMIN_DASHBOARD}" />
                            <c:if test="${fn:endsWith(pageContext.request.requestURI, StringUtils.SERVLET_URL_ADMIN_DASHBOARD) || fn:endsWith(pageContext.request.requestURI, StringUtils.PAGE_URL_ADMIN_DASHBOARD)}">
                                <c:set var="dashboardActive" value="${true}" />
                            </c:if>
                        </c:when>
                        <c:when test="${sessionScope[StringUtils.SESSION_USER_ROLE] == StringUtils.ROLE_CUSTOMER}">
                            <c:set var="dashboardUrl" value="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_CUSTOMER_DASHBOARD}" />
                             <c:if test="${fn:endsWith(pageContext.request.requestURI, StringUtils.SERVLET_URL_CUSTOMER_DASHBOARD) || fn:endsWith(pageContext.request.requestURI, StringUtils.PAGE_URL_CUSTOMER_DASHBOARD)}">
                                <c:set var="dashboardActive" value="${true}" />
                            </c:if>
                        </c:when>
                    </c:choose>

                    <li><a href="${dashboardUrl}" class="${dashboardActive ? 'active' : ''}">Dashboard</a></li>
                </c:if>

                <%-- Always Visible Links --%>
                <li>
                    <a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_ABOUT_US}"
                       class="${fn:endsWith(pageContext.request.requestURI, StringUtils.SERVLET_URL_ABOUT_US) ? 'active' : ''}">
                       About
                    </a>
                </li>
                <li>
                     <a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_CONTACT_US}"
                        class="${fn:endsWith(pageContext.request.requestURI, StringUtils.SERVLET_URL_CONTACT_US) ? 'active' : ''}">
                        Contact
                    </a>
                </li>

                
            </ul>
        </nav>
    </div>

    <%-- Right Section: Search, Actions, User Menu/Login --%>
    <div class="header-right">
        
        <div class="header-search">
             <i class="fas fa-search"></i>
            <input type="text" placeholder="Search stocks...">
        </div>
        

        <c:choose>
            <%-- User logged in --%>
            <c:when test="${not empty sessionScope[StringUtils.SESSION_USER_EMAIL]}">
                

                 <div class="user-menu">
                   
                    <a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_USER_PROFILE}" class="user-profile-link" title="View Profile">
                       <i class="fas fa-user-circle"></i>
                       <span><c:out value="${not empty sessionScope[StringUtils.SESSION_USER_NAME] ? sessionScope[StringUtils.SESSION_USER_NAME] : 'User'}" /></span>
                    </a>
                  
                   <form action="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_LOGOUT}" method="post" style="display: inline; margin: 0; padding: 0;">
                        <button type="submit" class="logout-button" title="Logout">
                            <i class="fas fa-sign-out-alt"></i>
                           
                        </button>
                   </form>
                 </div>
            </c:when>

            <%-- User not logged in --%>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_LOGIN}" class="login-button">${StringUtils.LOGIN}</a>
                <a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_REGISTER}" class="signup-button">${StringUtils.SIGNUP}</a>
            </c:otherwise>
        </c:choose>
    </div>
</header>