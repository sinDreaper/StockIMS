<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.stockx.util.StringUtils" %>
<%@ page import="com.stockx.model.StockModel" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard - StockX</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/customer_dashboard.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/header.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/footer.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        /* Basic styling for sort indicators and filter form */
        .table-filters { margin-bottom: 15px; display: flex; align-items: center; gap: 10px; }
        .table-filters label { margin-right: 5px; color: #bbb; }
        .table-filters select, .table-filters input[type="text"] { /* Added text input styling for consistency */
            padding: 8px 12px; /* Increased padding slightly */
            border-radius: 5px; /* Slightly more rounded */
            border: 1px solid #444; /* Darker border to blend better */
            background-color: #2a2a3e; /* Darker input background */
            color: #e0e0e0; /* Lighter text */
            margin-right: 15px; /* More space between elements */
            font-size: 0.9em;
        }
        .table-filters select:focus, .table-filters input[type="text"]:focus {
            outline: none;
            border-color: #6200ea; /* Accent color on focus */
        }

        .stock-overview th a { color: #a0a0c0; text-decoration: none; display: inline-block; padding: 2px 0;} /* Adjusted link color */
        .stock-overview th a:hover { color: #fff; }
        .stock-overview th .sort-indicator { margin-left: 5px; font-size: 0.8em; }

        .view-all-link {
            color: #7e3ff2; /* Accent color for link */
            text-decoration: none;
            font-weight: 500;
        }
        .view-all-link:hover {
            text-decoration: underline;
            color: #a06eff;
        }
    </style>
</head>
<body>
    <%-- DEFINE currentView HERE, at a higher scope --%>
    <c:set var="currentView" value="${empty param.view ? 'dashboard' : param.view}" />

    <div class="dashboard-container">
        <jsp:include page="header.jsp" />

        <div class="dashboard-body">
            <aside class="sidebar">
                <h3>StockX</h3>
                 <nav>
                    <ul>
                        <%-- These _jsp variables are page-scoped by default, fine here if used below in this page scope --%>
                        <c:set var="currentSortBy_jsp" value="${empty param.sort_by ? '' : param.sort_by}" />
                        <c:set var="currentSortOrder_jsp" value="${empty param.sort_order ? '' : param.sort_order}" />
                        <c:set var="currentFilterCategory_jsp" value="${empty param.filter_category ? '' : param.filter_category}" />
                        
                        <li><a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_CUSTOMER_DASHBOARD}" class="${currentView == 'dashboard' ? 'active' : ''}"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>                       
                        <li><a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_CUSTOMER_DASHBOARD}?view=stocks" class="${currentView == 'stocks' ? 'active' : ''}"><i class="fas fa-chart-line"></i> Stocks</a></li>                       
                        <li><a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_USER_PROFILE}"><i class="fas fa-cog"></i> Settings</a></li>
                        <li><a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_CONTACT_US}"><i class="fas fa-question-circle"></i> Help & Support</a></li>
                    </ul>
                </nav>
                 <div class="sidebar-bottom">
                    <form action="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_LOGOUT}" method="post">
                         <button type="submit" class="logout-button-sidebar"><i class="fas fa-sign-out-alt"></i> Logout</button>
                    </form>
                </div>
            </aside>

            <main class="main-content">
                <c:choose>
                    <c:when test="${currentView == 'stocks'}">
                        <div class="content-header">
                            <h2>Stock Market Data</h2>
                            <%-- No specific actions for customer here --%>
                        </div>

                        <div class="table-filters">
                            <form method="GET" action="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_CUSTOMER_DASHBOARD}" id="stockFilterForm">
                                <input type="hidden" name="view" value="stocks">
                                
                                <label for="filterCategory">Category:</label>
                                <select name="filter_category" id="filterCategory" onchange="document.getElementById('stockFilterForm').submit()">
                                    <option value="" ${empty param.filter_category ? 'selected' : ''}>All Categories</option>
                                   
                                    <option value="Technology" ${param.filter_category == 'Technology' ? 'selected' : ''}>Technology</option>
                                    <option value="Finance" ${param.filter_category == 'Finance' ? 'selected' : ''}>Finance</option>
                                    <option value="Healthcare" ${param.filter_category == 'Healthcare' ? 'selected' : ''}>Healthcare</option>
                                    <option value="Energy" ${param.filter_category == 'Energy' ? 'selected' : ''}>Energy</option>
                                    <option value="Consumer Discretionary" ${param.filter_category == 'Consumer Discretionary' ? 'selected' : ''}>Consumer Discretionary</option>
                                </select>
                                
                                
                            </form>
                        </div>

                        <c:if test="${not empty requestScope[StringUtils.MESSAGE_ERROR]}">
                            <p class="message error-message">${requestScope[StringUtils.MESSAGE_ERROR]}</p>
                        </c:if>

                        <section class="stock-overview full-page-table">
                             <div class="table-container">
                                <table>
                                    <%-- Variables for sorting links, specific to this 'stocks' view --%>
                                    <c:set var="currentTableSortBy" value="${empty param.sort_by ? 'CompanyName' : param.sort_by}" />
                                    <c:set var="currentTableSortOrder" value="${empty param.sort_order ? 'ASC' : param.sort_order}" />
                                    <c:set var="filterCategoryQueryParam" value="${empty param.filter_category ? '' : '&filter_category='.concat(param.filter_category)}" />

                                    <thead>
                                        <tr>
                                            <th><a href="?view=stocks&sort_by=Symbol&sort_order=${currentTableSortBy == 'Symbol' && currentTableSortOrder == 'ASC' ? 'DESC' : 'ASC'}${filterCategoryQueryParam}">Symbol <span class="sort-indicator">${currentTableSortBy == 'Symbol' ? (currentTableSortOrder == 'ASC' ? '▲' : '▼') : ''}</span></a></th>
                                            <th>Company Name</th>
                                            <th><a href="?view=stocks&sort_by=CurrentPrice&sort_order=${currentTableSortBy == 'CurrentPrice' && currentTableSortOrder == 'ASC' ? 'DESC' : 'ASC'}${filterCategoryQueryParam}">Current Price <span class="sort-indicator">${currentTableSortBy == 'CurrentPrice' ? (currentTableSortOrder == 'ASC' ? '▲' : '▼') : ''}</span></a></th>
                                            <th><a href="?view=stocks&sort_by=Exchange&sort_order=${currentTableSortBy == 'Exchange' && currentTableSortOrder == 'ASC' ? 'DESC' : 'ASC'}${filterCategoryQueryParam}">Exchange <span class="sort-indicator">${currentTableSortBy == 'Exchange' ? (currentTableSortOrder == 'ASC' ? '▲' : '▼') : ''}</span></a></th>
                                            <th><a href="?view=stocks&sort_by=Category&sort_order=${currentTableSortBy == 'Category' && currentTableSortOrder == 'ASC' ? 'DESC' : 'ASC'}${filterCategoryQueryParam}">Category <span class="sort-indicator">${currentTableSortBy == 'Category' ? (currentTableSortOrder == 'ASC' ? '▲' : '▼') : ''}</span></a></th>
                                            <th><a href="?view=stocks&sort_by=ListingDate&sort_order=${currentTableSortBy == 'ListingDate' && currentTableSortOrder == 'ASC' ? 'DESC' : 'ASC'}${filterCategoryQueryParam}">Listing Date <span class="sort-indicator">${currentTableSortBy == 'ListingDate' ? (currentTableSortOrder == 'ASC' ? '▲' : '▼') : ''}</span></a></th>
                                            <th>Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:choose>
                                            <c:when test="${empty requestScope[StringUtils.REQ_ATTRIBUTE_STOCKS] || fn:length(requestScope[StringUtils.REQ_ATTRIBUTE_STOCKS]) == 0}">
                                                <tr><td colspan="7">No stocks match your criteria.</td></tr>
                                            </c:when>
                                            <c:otherwise>
                                                <c:forEach var="stock" items="${requestScope[StringUtils.REQ_ATTRIBUTE_STOCKS]}">
                                                    <tr>
                                                        <td>${stock.symbol}</td>
                                                        <td>${stock.companyName}</td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${not empty stock.currentPrice}">
                                                                    <fmt:setLocale value="en_US"/>
                                                                    <fmt:formatNumber value="${stock.currentPrice}" type="currency" currencySymbol="$"/>
                                                                </c:when>
                                                                <c:otherwise>N/A</c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>${stock.exchange}</td>
                                                        <td>${stock.category}</td>
                                                        <td>${stock.listingDate}</td>
                                                        <td><span class="status ${stock.status == 'Active' ? 'status-active' : 'status-inactive'}">${stock.status}</span></td>
                                                    </tr>
                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>
                                    </tbody>
                                </table>
                            </div>
                         </section>
                    </c:when>
                    <c:otherwise>
                        <%-- Default Dashboard View (Stats, Summary, etc.) --%>
                        <div class="content-header">
                            <h2>Dashboard</h2>
                            <div class="header-actions">
                                 <button class="action-button"><i class="fas fa-plus"></i> Add Widget</button>
                                 <select class="action-select"> <option>All Time</option> </select>
                                 <button class="action-button primary">Buy Asset</button>
                            </div>
                        </div>

                        <c:if test="${not empty sessionScope[StringUtils.MESSAGE_SUCCESS]}">
                            <p class="message success-message">${sessionScope[StringUtils.MESSAGE_SUCCESS]}</p>
                            <c:remove var="successMessage" scope="session" />
                        </c:if>
                        <c:if test="${not empty requestScope[StringUtils.MESSAGE_ERROR]}">
                            <p class="message error-message">${requestScope[StringUtils.MESSAGE_ERROR]}</p>
                        </c:if>

                        <div class="stats-grid">
                            <div class="stat-card">
                                <div class="card-header"><h4>Total Assets</h4><i class="fas fa-info-circle"></i></div>
                                <div class="amount">$325,980.65</div>
                                <div class="change"><span class="percentage positive">+12%</span> <span class="period">vs last month</span></div>
                            </div>
                            <div class="stat-card">
                                <div class="card-header"><h4>Total Investments</h4><i class="fas fa-info-circle"></i></div>
                                <div class="amount">$270,560.20</div>
                                <div class="change"><span class="percentage positive">+20%</span> <span class="period">this year</span></div>
                            </div>
                        </div>

                        <section class="stock-overview">
                             <h3>Featured Stocks</h3>
                             <div class="table-container">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>Symbol</th>
                                            <th>Company Name</th>
                                            <th>Current Price</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:choose>
                                            <c:when test="${empty requestScope[StringUtils.REQ_ATTRIBUTE_STOCKS] || fn:length(requestScope[StringUtils.REQ_ATTRIBUTE_STOCKS]) == 0}">
                                                <tr><td colspan="3">No featured stocks available.</td></tr>
                                            </c:when>
                                            <c:otherwise>
                                                <c:forEach var="stock" items="${requestScope[StringUtils.REQ_ATTRIBUTE_STOCKS]}" varStatus="loop" begin="0" end="4">
                                                    <tr>
                                                        <td>${stock.symbol}</td>
                                                        <td>${stock.companyName}</td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${not empty stock.currentPrice}">
                                                                    <fmt:setLocale value="en_US"/>
                                                                    <fmt:formatNumber value="${stock.currentPrice}" type="currency" currencySymbol="$"/>
                                                                </c:when>
                                                                <c:otherwise>N/A</c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>
                                    </tbody>
                                </table>
                                <c:if test="${fn:length(requestScope[StringUtils.REQ_ATTRIBUTE_STOCKS]) > 5 && currentView != 'stocks'}">
                                     <div style="text-align: right; margin-top: 10px;">
                                         <a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_CUSTOMER_DASHBOARD}?view=stocks" class="view-all-link">View All Stocks →</a>
                                     </div>
                                 </c:if>
                            </div>
                         </section>

                        <section class="chart-section">
                             <h3>Investment Distribution</h3>
                             <div class="chart-placeholder">Chart Would Go Here</div>
                        </section>

                         <section class="ai-robo-advisor">
                             <h3>Invest Smarter With Our AI-Robo Advisor!</h3>
                             <p>Get automated management, real-time insights, and personalized advice.</p>
                             <button>Try Now</button>
                         </section>
                    </c:otherwise>
                </c:choose>
            </main>
        </div>
        <jsp:include page="footer.jsp" />
    </div>
</body>
</html>