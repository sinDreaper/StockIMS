<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.stockx.model.CustomerModel" %> 
<%@ page import="com.stockx.model.AdminModel" %>   
<%@ page import="com.stockx.util.StringUtils" %>
<%@ page import="com.stockx.model.StockModel" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title> Dashboard - StockX</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/customer_dashboard.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/header.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/footer.css" />
</head>
<body>
    <div class="dashboard-container">
        <jsp:include page="header.jsp" />

        <div class="dashboard-body">
            <aside class="sidebar">
                
                 <nav>
                    <ul>
                        <li><a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_CUSTOMER_DASHBOARD}" class="active"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
                        <li><a href="#"><i class="fas fa-briefcase"></i> Portfolio</a></li>
                        <li><a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_CUSTOMER_DASHBOARD}"><i class="fas fa-chart-line"></i> Stocks</a></li>
                        <li><a href="#"><i class="fas fa-coins"></i> Bonds</a></li>
                        <li><a href="#"><i class="fas fa-chart-pie"></i> Mutual Funds</a></li>
                        <li><a href="#"><i class="fas fa-search-dollar"></i> Analytics</a></li>
                        <li><a href="#"><i class="fas fa-wallet"></i> Wallet</a></li>
                        <li><a href="#"><i class="fas fa-users"></i> Community</a></li>
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
				    <c:remove var="successMessage" scope="session" /> <%-- CORRECTED LINE --%>
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
                     <h3>Stock Market Overview</h3>
                     <div class="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Symbol</th>
                                    <th>Company Name</th>
                                    <th>Current Price</th>
                                    <th>Exchange</th>
                                    <th>Category</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty requestScope[StringUtils.REQ_ATTRIBUTE_STOCKS] || requestScope[StringUtils.REQ_ATTRIBUTE_STOCKS].size() == 0}">
                                        <tr><td colspan="5">No active stocks available at the moment.</td></tr>
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
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                 </section>

                <%-- Placeholder for a chart section needs to be done before final submission--%>
                <section class="chart-section">
                     <h3>Investment Distribution</h3>
                     <div class="chart-placeholder">
                         Chart Would Go Here (e.g., Stocks, Bonds, Mutual Funds Pie Chart)
                     </div>
                </section>

                 <section class="ai-robo-advisor">
                     <h3>Invest Smarter With Our AI-Robo Advisor!</h3>
                     <p>Get automated management, real-time insights, and personalized advice.</p>
                     <button>Try Now</button>
                 </section>

            </main>
        </div>
        <jsp:include page="footer.jsp" />
    </div>
</body>
</html>