-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.4.32-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Dumping database structure for ecommerce
CREATE DATABASE IF NOT EXISTS `ecommerce` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `ecommerce`;

-- Dumping data for table ecommerce.product_category: ~4 rows (approximately)
DELETE FROM `product_category`;
INSERT INTO `product_category` (`id`, `category_name`) VALUES
	(1, 'Coffee Beans'),
	(2, 'Coffee Equipment'),
	(3, 'Coffee Accessories'),
	(4, 'Coffee Cups');

-- Dumping data for table ecommerce.product: ~100 rows (approximately)
DELETE FROM `product`;
INSERT INTO `product` (`id`, `sku`, `name`, `description`, `unit_price`, `image_url`, `active`, `units_in_stock`, `date_created`, `last_updated`, `category_id`, `units_in_stocks`) VALUES
	(1, 'COFFEE-1000', 'Arabica Coffee Beans', 'Premium Arabica coffee beans from Ethiopia. Rich flavor with notes of chocolate and berries.', 14.99, 'assets/images/products/coffee/arabica-beans.png', b'1', 100, '2025-04-02', '2025-04-02', 1, NULL),
	(2, 'COFFEE-1001', 'Robusta Coffee Beans', 'Strong Robusta coffee beans from Vietnam. Bold flavor with earthy notes.', 12.99, 'assets/images/products/coffee/robusta-beans.png', b'1', 100, '2025-04-02', '2025-04-02', 1, NULL),
	(3, 'COFFEE-1002', 'Colombian Coffee Beans', 'Smooth Colombian coffee beans. Balanced flavor with caramel notes.', 15.99, 'assets/images/products/coffee/colombian-beans.png', b'1', 100, '2025-04-02', '2025-04-02', 1, NULL),
	(4, 'COFFEE-1003', 'French Press', 'Stainless steel French press for brewing rich, full-bodied coffee.', 29.99, 'assets/images/products/equipment/french-press.png', b'1', 100, '2025-04-02', '2025-04-02', 2, NULL),
	(5, 'COFFEE-1004', 'Pour Over Set', 'Ceramic pour over coffee maker with reusable filter.', 24.99, 'assets/images/products/equipment/pour-over.png', b'1', 100, '2025-04-02', '2025-04-02', 2, NULL),
	(6, 'COFFEE-1005', 'Coffee Grinder', 'Electric burr grinder for consistent coffee grounds.', 89.99, 'assets/images/products/equipment/grinder.png', b'1', 100, '2025-04-02', '2025-04-02', 2, NULL),
	(7, 'COFFEE-1006', 'Coffee Scale', 'Digital scale for precise coffee measurements.', 19.99, 'assets/images/products/accessories/scale.png', b'1', 100, '2025-04-02', '2025-04-02', 3, NULL),
	(8, 'COFFEE-1007', 'Coffee Scoop', 'Stainless steel coffee scoop for perfect measurements.', 9.99, 'assets/images/products/accessories/scoop.png', b'1', 100, '2025-04-02', '2025-04-02', 3, NULL),
	(9, 'COFFEE-1008', 'Ceramic Coffee Cup', 'Handcrafted ceramic coffee cup.', 12.99, 'assets/images/products/cups/ceramic-cup.png', b'1', 100, '2025-04-02', '2025-04-02', 4, NULL),
	(10, 'COFFEE-1009', 'Travel Coffee Mug', 'Insulated travel mug for hot coffee on the go.', 16.99, 'assets/images/products/cups/travel-mug.png', b'1', 100, '2025-04-02', '2025-04-02', 4, NULL),
	(11, 'COFFEE-1010', 'Espresso Machine', 'Professional espresso machine for home use.', 299.99, 'assets/images/products/equipment/espresso-machine.png', b'1', 100, '2025-04-02', '2025-04-02', 2, NULL),
	(12, 'COFFEE-1011', 'Coffee Filter Paper', 'Premium coffee filter papers for pour over.', 4.99, 'assets/images/products/accessories/filter-paper.png', b'1', 100, '2025-04-02', '2025-04-02', 3, NULL),
	(13, 'COFFEE-1012', 'Coffee Storage Container', 'Airtight container for coffee bean storage.', 19.99, 'assets/images/products/accessories/storage-container.png', b'1', 100, '2025-04-02', '2025-04-02', 3, NULL),
	(14, 'COFFEE-1013', 'Coffee Tumbler', 'Double-walled insulated coffee tumbler.', 24.99, 'assets/images/products/cups/tumbler.png', b'1', 100, '2025-04-02', '2025-04-02', 4, NULL),
	(15, 'COFFEE-1014', 'Coffee Mug Set', 'Set of 4 ceramic coffee mugs.', 39.99, 'assets/images/products/cups/mug-set.png', b'1', 100, '2025-04-02', '2025-04-02', 4, NULL); 