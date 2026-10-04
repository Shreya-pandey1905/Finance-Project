CREATE DATABASE IF NOT EXISTS `forgehub`;
USE `forgehub`;
show tables;
select * from users;

desc users;
desc users;

INSERT INTO users
    (email, is_first_time_login, full_name, password_hash, role, secret_key)
VALUES
    ('admin1@gmail.com', 1, 'Admin One',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     'ADMIN', NULL),

    ('admin2@gmail.com', 1, 'Admin Two',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     'ADMIN', NULL),

    ('vendor1@gmail.com', 1, 'Vendor One',
     '$2a$10$7EqJtq98hPqEX7fNZaFWoO9Y6xYx1ZK8qJ7YfJXJ7qZ5QJYxV8J9K',
     'VENDOR', NULL),

    ('vendor2@gmail.com', 1, 'Vendor Two',
     '$2a$10$7EqJtq98hPqEX7fNZaFWoO9Y6xYx1ZK8qJ7YfJXJ7qZ5QJYxV8J9K',
     'VENDOR', NULL);
     
     UPDATE users
SET password_hash = '$2a$10$prPOvv55P9xvpZGcBAgQseLUDCiyaMJCWPNGSnU3D2qm4g/j39H7O'
WHERE email = 'admin2@gmail.com';

SET SQL_SAFE_UPDATES = 0;

ALTER TABLE Users
Modify COLUMN refresh_token VARCHAR(500) NULL;

UPDATE Users
SET Secret_Key = NULL
WHERE Email = 'spprac82@gmail.com';