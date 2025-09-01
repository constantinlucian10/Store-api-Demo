INSERT INTO product (name, price) VALUES
('Laptop', 3500.00),
('Mouse', 50.00),
('Keyboard', 120.00),
('Monitor', 900.00),
('Headphones', 200.00);

INSERT INTO orders (product_id, product_name, quantity, final_price, created_at) VALUES
(1, 'Laptop', 1, 3500.00, NOW()),
(2, 'Mouse', 2, 100.00, NOW()),
(3, 'Keyboard', 1, 120.00, NOW()),
(4, 'Monitor', 2, 1800.00, NOW()),
(5, 'Headphones', 3, 600.00, NOW());