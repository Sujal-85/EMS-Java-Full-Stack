-- Run this script in MySQL to create the database

CREATE DATABASE IF NOT EXISTS employee_management_system
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE employee_management_system;

-- Verify database creation
SELECT 'Database created successfully!' AS Status;
