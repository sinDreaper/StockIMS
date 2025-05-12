<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.stockx.util.StringUtils" %>
<%@ page import="com.stockx.model.StockModel" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - StockX</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/header.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin_dashboard.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/footer.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
    <div class="dashboard-container">
        <%@ include file="header.jsp" %>

        <div class="dashboard-body">
            <aside class="sidebar">
                <h3>StockX Admin</h3>
                <nav>
                    <ul>
                        <li><a href="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_ADMIN_DASHBOARD}" class="active"><i class="fas fa-tachometer-alt"></i> Dashboard & Stock Mgt</a></li>
                        <li><a href="#"><i class="fas fa-users"></i> User Management</a></li>
                        <li><a href="#"><i class="fas fa-cog"></i> Settings</a></li>
                    </ul>
                </nav>
                <div class="sidebar-bottom">
                    <div class="dark-mode-toggle">
                        <i class="fas fa-moon"></i> Dark Mode 
                        <label class="switch"><input type="checkbox" checked><span class="slider round"></span></label>
                    </div>
                    <form action="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_LOGOUT}" method="post">
                        <button type="submit" class="logout-button-sidebar"><i class="fas fa-sign-out-alt"></i> Logout</button>
                    </form>
                </div>
            </aside>

            <main class="main-content">
                <div class="content-header">
                    <h1>Stock Management</h1>
                    <button class="add-new-button" onclick="openAddModal()"><i class="fas fa-plus"></i> Add New Stock</button>
                </div>

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

                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>Symbol</th>
                                <th>Stock Name</th>
                                <th>Current Price</th>
                                <th>Exchange</th>
                                <th>Category</th>
                                <th>Date Added</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty requestScope.stocks}">
                                     <tr><td colspan="8">No stocks found.</td></tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="stock" items="${requestScope.stocks}">
                                        <tr>
                                            <td>${stock.symbol}</td>
                                            <td>${stock.companyName}</td>
                                            <td>
                                                <fmt:setLocale value="en_US"/>
                                                <fmt:formatNumber value="${stock.currentPrice}" type="currency" currencySymbol="$"/>
                                                <c:if test="${empty stock.currentPrice}">N/A</c:if>
                                            </td>
                                            <td>${stock.exchange}</td>
                                            <td>${stock.category}</td>
                                            <td>${stock.dateAddedFormatted}</td>
                                            <td><span class="status ${stock.status == 'Active' ? 'status-active' : 'status-inactive'}">${stock.status}</span></td>
                                            <td>
                                                <button class="action-button edit-button" onclick="openEditModal(
                                                    '${stock.stockId}',
                                                    '${fn:escapeXml(stock.symbol)}',
                                                    '${fn:escapeXml(stock.companyName)}',
                                                    '${fn:escapeXml(stock.exchange)}',
                                                    '${fn:escapeXml(stock.category)}',
                                                    '${stock.status}',
                                                    '${stock.listingDate}',
                                                    '${stock.openingPrice}',
                                                    '${stock.closingPrice}',
                                                    '${stock.totalShares}',
                                                    '${fn:escapeXml(stock.instrumentId)}'
                                                )">
                                                    <i class="fas fa-pencil-alt"></i>
                                                </button>
                                                <form action="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_STOCK_ACTION}" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete ${fn:escapeXml(stock.symbol)}?');">
                                                    <input type="hidden" name="${StringUtils.ACTION}" value="${StringUtils.ACTION_DELETE}">
                                                    <input type="hidden" name="${StringUtils.STOCK_ID}" value="${stock.stockId}">
                                                    <button type="submit" class="action-button delete-button">
                                                        <i class="fas fa-trash-alt"></i>
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <div class="pagination">
                    <button disabled>&lt; Prev</button>
                    <span>Page 1 of X</span>
                    <button>Next &gt;</button>
                </div>
            </main>
        </div>

        <div id="addStockModal" class="modal">
            <div class="modal-content">
                <span class="close-button" onclick="closeModal('addStockModal')">×</span>
                <h2>Add New Stock</h2>
                <form action="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_STOCK_ACTION}" method="post">
                    <input type="hidden" name="${StringUtils.ACTION}" value="${StringUtils.ACTION_ADD}">
                    <div class="form-grid">
                        <div><label for="addSymbol">Symbol:</label><input type="text" id="addSymbol" name="${StringUtils.STOCK_SYMBOL}" required></div>
                        <div><label for="addCompanyName">Company Name:</label><input type="text" id="addCompanyName" name="${StringUtils.STOCK_COMPANY_NAME}" required></div>
                        <div><label for="addExchange">Exchange:</label><input type="text" id="addExchange" name="${StringUtils.STOCK_EXCHANGE}"></div>
                        <div><label for="addCategory">Category:</label><input type="text" id="addCategory" name="${StringUtils.STOCK_CATEGORY}"></div>
                        <div><label for="addListingDate">Listing Date:</label><input type="date" id="addListingDate" name="${StringUtils.STOCK_LISTING_DATE}"></div>
                        <div><label for="addStatus">Status:</label>
                            <select id="addStatus" name="${StringUtils.STOCK_STATUS}" required>
                                <option value="Active">Active</option>
                                <option value="Inactive">Inactive</option>
                            </select>
                        </div>
                        <div><label for="addOpeningPrice">Opening Price:</label><input type="number" step="0.01" id="addOpeningPrice" name="${StringUtils.STOCK_OPENING_PRICE}"></div>
                        <div><label for="addClosingPrice">Closing Price:</label><input type="number" step="0.01" id="addClosingPrice" name="${StringUtils.STOCK_CLOSING_PRICE}"></div>
                        <div><label for="addTotalShares">Total Shares:</label><input type="number" id="addTotalShares" name="${StringUtils.STOCK_TOTAL_SHARES}"></div>
                        <div><label for="addInstrumentId">Instrument ID:</label><input type="text" id="addInstrumentId" name="${StringUtils.STOCK_INSTRUMENT_ID}"></div>
                    </div>
                    <button type="submit">Add Stock</button>
                </form>
            </div>
        </div>

        <div id="editStockModal" class="modal">
            <div class="modal-content">
                <span class="close-button" onclick="closeModal('editStockModal')">×</span>
                <h2>Edit Stock</h2>
                <form action="${pageContext.request.contextPath}${StringUtils.SERVLET_URL_STOCK_ACTION}" method="post">
                    <input type="hidden" name="${StringUtils.ACTION}" value="${StringUtils.ACTION_UPDATE}">
                    <input type="hidden" id="editStockId" name="${StringUtils.STOCK_ID}">
                    <div class="form-grid">
                        <div><label for="editSymbol">Symbol:</label><input type="text" id="editSymbol" name="${StringUtils.STOCK_SYMBOL}" required></div>
                        <div><label for="editCompanyName">Company Name:</label><input type="text" id="editCompanyName" name="${StringUtils.STOCK_COMPANY_NAME}" required></div>
                        <div><label for="editExchange">Exchange:</label><input type="text" id="editExchange" name="${StringUtils.STOCK_EXCHANGE}"></div>
                        <div><label for="editCategory">Category:</label><input type="text" id="editCategory" name="${StringUtils.STOCK_CATEGORY}"></div>
                        <div><label for="editListingDate">Listing Date:</label><input type="date" id="editListingDate" name="${StringUtils.STOCK_LISTING_DATE}"></div>
                        <div><label for="editStatus">Status:</label>
                            <select id="editStatus" name="${StringUtils.STOCK_STATUS}" required>
                                <option value="Active">Active</option>
                                <option value="Inactive">Inactive</option>
                            </select>
                        </div>
                        <div><label for="editOpeningPrice">Opening Price:</label><input type="number" step="0.01" id="editOpeningPrice" name="${StringUtils.STOCK_OPENING_PRICE}"></div>
                        <div><label for="editClosingPrice">Closing Price:</label><input type="number" step="0.01" id="editClosingPrice" name="${StringUtils.STOCK_CLOSING_PRICE}"></div>
                        <div><label for="editTotalShares">Total Shares:</label><input type="number" id="editTotalShares" name="${StringUtils.STOCK_TOTAL_SHARES}"></div>
                        <div><label for="editInstrumentId">Instrument ID:</label><input type="text" id="editInstrumentId" name="${StringUtils.STOCK_INSTRUMENT_ID}"></div>
                    </div>
                    <button type="submit">Update Stock</button>
                </form>
            </div>
        </div>

        <%@ include file="footer.jsp" %>
    </div>

    <script>
        function openModal(modalId) {
            document.getElementById(modalId).style.display = 'block';
        }
        function closeModal(modalId) {
            document.getElementById(modalId).style.display = 'none';
        }
        function openAddModal() {
            document.querySelector('#addStockModal form').reset();
            openModal('addStockModal');
        }
        function openEditModal(id, symbol, name, exchange, category, status, listingDate, openingPrice, closingPrice, totalShares, instrumentId) {
            document.getElementById('editStockId').value = id || '';
            document.getElementById('editSymbol').value = symbol || '';
            document.getElementById('editCompanyName').value = name || '';
            document.getElementById('editExchange').value = exchange || '';
            document.getElementById('editCategory').value = category || '';
            document.getElementById('editStatus').value = status || 'Active';
            document.getElementById('editListingDate').value = listingDate && listingDate !== 'null' ? listingDate.substring(0, 10) : '';
            document.getElementById('editOpeningPrice').value = openingPrice && openingPrice !== 'null' ? openingPrice : '';
            document.getElementById('editClosingPrice').value = closingPrice && closingPrice !== 'null' ? closingPrice : '';
            document.getElementById('editTotalShares').value = totalShares && totalShares !== 'null' ? totalShares : '';
            document.getElementById('editInstrumentId').value = instrumentId || '';
            openModal('editStockModal');
        }
        window.onclick = function(event) {
            let modals = document.getElementsByClassName('modal');
            for (let i = 0; i < modals.length; i++) {
                if (event.target == modals[i]) {
                    modals[i].style.display = "none";
                }
            }
        }
    </script>
</body>
</html>
