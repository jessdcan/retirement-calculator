-- Insert mock data for 'simple' lifestyle
INSERT INTO staging.lifestyle_deposits 
(lifestyle_type, monthly_deposit, description)
VALUES 
('simple', 2000.00,'Basic lifestyle with moderate expenses');

-- Insert mock data for 'fancy' lifestyle
INSERT INTO staging.lifestyle_deposits 
(lifestyle_type, monthly_deposit, description)
VALUES 
('fancy', 5000.00,'Luxury lifestyle with premium expenses');