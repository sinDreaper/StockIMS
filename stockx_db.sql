-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: May 19, 2025 at 05:45 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `stockx_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `Admin`
--

CREATE TABLE `Admin` (
  `Admin_Id` int(11) NOT NULL,
  `Admin_Name` varchar(100) NOT NULL,
  `Admin_Email` varchar(255) NOT NULL,
  `Admin_DOB` date DEFAULT NULL,
  `Admin_Contact` varchar(20) DEFAULT NULL,
  `Password_Id` int(11) NOT NULL,
  `Created_At` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Admin`
--

INSERT INTO `Admin` (`Admin_Id`, `Admin_Name`, `Admin_Email`, `Admin_DOB`, `Admin_Contact`, `Password_Id`, `Created_At`) VALUES
(3, 'System Admin', 'admin@gmail.com', '1990-01-01', '1234567890', 7, '2025-05-18 23:23:29');

-- --------------------------------------------------------

--
-- Table structure for table `Customer`
--

CREATE TABLE `Customer` (
  `Customer_Id` int(11) NOT NULL,
  `Customer_Name` varchar(100) NOT NULL,
  `Customer_Email` varchar(255) NOT NULL,
  `Customer_DOB` date DEFAULT NULL,
  `Customer_Contact` varchar(20) DEFAULT NULL,
  `Password_Id` int(11) NOT NULL,
  `Created_At` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Customer`
--

INSERT INTO `Customer` (`Customer_Id`, `Customer_Name`, `Customer_Email`, `Customer_DOB`, `Customer_Contact`, `Password_Id`, `Created_At`) VALUES
(1, 'ram', 'ram@gmail.com', '2005-02-01', '9767656182', 2, '2025-05-12 14:04:50'),
(3, 'Test User', 'testuser@gmail.com', '2000-01-01', '123456789', 6, '2025-05-18 22:57:54');

-- --------------------------------------------------------

--
-- Table structure for table `Password`
--

CREATE TABLE `Password` (
  `Password_Id` int(11) NOT NULL,
  `Email` varchar(255) NOT NULL,
  `Password_Hash` varchar(255) NOT NULL,
  `User_Role` enum('Admin','Customer') NOT NULL,
  `Created_At` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Password`
--

INSERT INTO `Password` (`Password_Id`, `Email`, `Password_Hash`, `User_Role`, `Created_At`) VALUES
(2, 'ram@gmail.com', '95QGYdt+HGOP29DtFjc9iRRxUNAsgBdHR4RAx9R9S/uqdt4AnlJSe9fbiv7OrxyPb5DrT+c=', 'Customer', '2025-05-12 14:04:50'),
(6, 'testuser@gmail.com', 'Yc2hbJtsuXudnBtqbjYCwtcAdRCmfUwvcTIwc351uURqGdlI6eQT9XUpOKRbSkMUK3VKGTQ16KM=', 'Customer', '2025-05-18 22:57:54'),
(7, 'admin@gmail.com', 'MCsoZOx4Joy+C3H8JIc+PVvYjkPXKO9nw1BdpTgEWHglFUotJk0++1Z7wGT/BQElmnPyFQ==', 'Admin', '2025-05-18 23:16:29');

-- --------------------------------------------------------

--
-- Table structure for table `Stock`
--

CREATE TABLE `Stock` (
  `Stock_Id` int(11) NOT NULL,
  `Symbol` varchar(10) NOT NULL,
  `Company_Name` varchar(255) NOT NULL,
  `Instrument_Id` varchar(50) DEFAULT NULL,
  `Total_Shares` bigint(20) DEFAULT NULL,
  `Listing_Date` date DEFAULT NULL,
  `Opening_Price` decimal(10,2) DEFAULT NULL,
  `Closing_Price` decimal(10,2) DEFAULT NULL,
  `Category` varchar(100) DEFAULT NULL,
  `Exchange` varchar(50) DEFAULT NULL,
  `Status` enum('Active','Inactive') DEFAULT 'Active',
  `Date_Added` timestamp NOT NULL DEFAULT current_timestamp(),
  `Last_Updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Stock`
--

INSERT INTO `Stock` (`Stock_Id`, `Symbol`, `Company_Name`, `Instrument_Id`, `Total_Shares`, `Listing_Date`, `Opening_Price`, `Closing_Price`, `Category`, `Exchange`, `Status`, `Date_Added`, `Last_Updated`) VALUES
(61, 'AAPL', 'Apple Inc.', 'EQ-AAPL', 17000000000, '1980-12-12', 22.12, 22.35, 'Technology', 'NASDAQ', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(62, 'MSFT', 'Microsoft Corp.', 'EQ-MSFT', 7600000000, '1986-03-13', 21.00, 21.15, 'Technology', 'NASDAQ', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(63, 'GOOGL', 'Alphabet Inc.', 'EQ-GOOGL', 3000000000, '2004-08-19', 85.00, 86.50, 'Technology', 'NASDAQ', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(64, 'AMZN', 'Amazon.com Inc.', 'EQ-AMZN', 500000000, '1997-05-15', 18.50, 18.75, 'Technology', 'NASDAQ', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(65, 'NVDA', 'NVIDIA Corp.', 'EQ-NVDA', 600000000, '1999-01-22', 12.50, 12.80, 'Technology', 'NASDAQ', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(66, 'JPM', 'JPMorgan Chase & Co.', 'EQ-JPM', 3000000000, '1969-05-19', 5.12, 5.15, 'Finance', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(67, 'BAC', 'Bank of America Corp.', 'EQ-BAC', 7000000000, '1973-01-02', 4.50, 4.55, 'Finance', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(68, 'WFC', 'Wells Fargo & Co.', 'EQ-WFC', 4300000000, '1978-03-09', 3.80, 3.85, 'Finance', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(69, 'C', 'Citigroup Inc.', 'EQ-C', 2100000000, '1998-01-15', 2.50, 2.55, 'Finance', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(70, 'GS', 'Goldman Sachs Group', 'EQ-GS', 270000000, '1999-05-04', 50.00, 50.75, 'Finance', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(71, 'JNJ', 'Johnson & Johnson', 'EQ-JNJ', 2600000000, '1944-09-24', 3.75, 3.80, 'Healthcare', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(72, 'PFE', 'Pfizer Inc.', 'EQ-PFE', 5000000000, '1942-06-22', 0.35, 0.36, 'Healthcare', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(73, 'MRK', 'Merck & Co. Inc.', 'EQ-MRK', 2400000000, '1946-05-02', 1.50, 1.52, 'Healthcare', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(74, 'ABT', 'Abbott Laboratories', 'EQ-ABT', 2200000000, '1957-03-12', 1.20, 1.22, 'Healthcare', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(75, 'LLY', 'Eli Lilly & Co.', 'EQ-LLY', 1100000000, '1953-11-21', 0.45, 0.46, 'Healthcare', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(76, 'XOM', 'Exxon Mobil Corp.', 'EQ-XOM', 4200000000, '1978-10-28', 1.25, 1.27, 'Energy', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(77, 'CVX', 'Chevron Corp.', 'EQ-CVX', 1900000000, '1928-09-08', 2.10, 2.12, 'Energy', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(78, 'BP', 'BP plc', 'EQ-BP', 1500000000, '1983-01-20', 0.85, 0.86, 'Energy', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(79, 'TOT', 'TotalEnergies SE', 'EQ-TOT', 1250000000, '1995-07-06', 1.10, 1.12, 'Energy', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(80, 'COP', 'ConocoPhillips', 'EQ-COP', 800000000, '2002-06-12', 0.95, 0.96, 'Energy', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(81, 'TSLA', 'Tesla Inc.', 'EQ-TSLA', 1000000000, '2010-06-29', 17.00, 17.50, 'Consumer Discretionary', 'NASDAQ', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(82, 'HD', 'Home Depot Inc.', 'EQ-HD', 600000000, '1981-09-22', 12.00, 12.20, 'Consumer Discretionary', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(83, 'MCD', 'McDonald’s Corp.', 'EQ-MCD', 300000000, '1965-04-21', 0.50, 0.51, 'Consumer Discretionary', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(84, 'NKE', 'NIKE Inc.', 'EQ-NKE', 220000000, '1980-12-02', 1.40, 1.42, 'Consumer Discretionary', 'NYSE', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30'),
(85, 'SBUX', 'Starbucks Corp.', 'EQ-SBUX', 160000000, '1992-06-26', 0.95, 0.97, 'Consumer Discretionary', 'NASDAQ', 'Active', '2025-05-19 01:04:30', '2025-05-19 01:04:30');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Admin`
--
ALTER TABLE `Admin`
  ADD PRIMARY KEY (`Admin_Id`),
  ADD UNIQUE KEY `Admin_Email` (`Admin_Email`),
  ADD KEY `Password_Id` (`Password_Id`);

--
-- Indexes for table `Customer`
--
ALTER TABLE `Customer`
  ADD PRIMARY KEY (`Customer_Id`),
  ADD UNIQUE KEY `Customer_Email` (`Customer_Email`),
  ADD KEY `Password_Id` (`Password_Id`);

--
-- Indexes for table `Password`
--
ALTER TABLE `Password`
  ADD PRIMARY KEY (`Password_Id`),
  ADD UNIQUE KEY `Email` (`Email`);

--
-- Indexes for table `Stock`
--
ALTER TABLE `Stock`
  ADD PRIMARY KEY (`Stock_Id`),
  ADD UNIQUE KEY `Symbol` (`Symbol`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `Admin`
--
ALTER TABLE `Admin`
  MODIFY `Admin_Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `Customer`
--
ALTER TABLE `Customer`
  MODIFY `Customer_Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `Password`
--
ALTER TABLE `Password`
  MODIFY `Password_Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `Stock`
--
ALTER TABLE `Stock`
  MODIFY `Stock_Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=86;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Admin`
--
ALTER TABLE `Admin`
  ADD CONSTRAINT `admin_ibfk_1` FOREIGN KEY (`Password_Id`) REFERENCES `Password` (`Password_Id`) ON DELETE CASCADE;

--
-- Constraints for table `Customer`
--
ALTER TABLE `Customer`
  ADD CONSTRAINT `customer_ibfk_1` FOREIGN KEY (`Password_Id`) REFERENCES `Password` (`Password_Id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
