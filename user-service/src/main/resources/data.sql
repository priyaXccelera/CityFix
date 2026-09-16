INSERT INTO users (id, name, email, password, role, address, phone, active, created_at)
VALUES (1, 'Alice Super Admin', 'superadmin@cityfix.com', '$2b$10$0SVYBQFG/jj1dsGSanVQ9OI1QfUqCPI07qgFFvvxoOVYgbxARht1e', 'SUPER_ADMIN', 'Downtown', '555-0100', true, NOW())
ON DUPLICATE KEY UPDATE
  name = VALUES(name), email = VALUES(email), password = VALUES(password), role = 'SUPER_ADMIN',
  address = VALUES(address), phone = VALUES(phone), active = true;

INSERT INTO users (id, name, email, password, role, address, phone, active, created_at)
VALUES (2, 'Bob Reporter', 'bob@cityfix.com', '$2b$10$.X158LDBfxqbk23MI62HjeyQSelKD/Kapo1U8UqM.Bc9NL5YbNLga', 'USER', 'Downtown', '555-0101', true, NOW())
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO users (id, name, email, password, role, address, phone, active, created_at)
VALUES (3, 'Carla Citizen', 'carla@cityfix.com', '$2b$10$.X158LDBfxqbk23MI62HjeyQSelKD/Kapo1U8UqM.Bc9NL5YbNLga', 'USER', 'Uptown', '555-0102', true, NOW())
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO users (id, name, email, password, role, address, phone, active, created_at)
VALUES (4, 'Dave Resident', 'dave@cityfix.com', '$2b$10$.X158LDBfxqbk23MI62HjeyQSelKD/Kapo1U8UqM.Bc9NL5YbNLga', 'USER', 'Westside', '555-0103', true, NOW())
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO users (id, name, email, password, role, address, phone, active, created_at)
VALUES (5, 'Eve Inactive', 'eve@cityfix.com', '$2b$10$.X158LDBfxqbk23MI62HjeyQSelKD/Kapo1U8UqM.Bc9NL5YbNLga', 'USER', 'Downtown', '555-0104', false, NOW())
ON DUPLICATE KEY UPDATE id = id;
