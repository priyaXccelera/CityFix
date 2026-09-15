INSERT INTO announcements (id, text, posted_by_user_id, posted_by_name, created_at, updated_at)
VALUES (1, 'Welcome to CityFix! Report civic issues in your area and track their resolution.', 1, 'Alice Admin', NOW(), NOW())
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO announcements (id, text, posted_by_user_id, posted_by_name, created_at, updated_at)
VALUES (2, 'Sanitation crews will be doing extra rounds in Uptown this week.', 1, 'Alice Admin', NOW(), NOW())
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO announcements (id, text, posted_by_user_id, posted_by_name, created_at, updated_at)
VALUES (3, 'Road resurfacing scheduled for Downtown Main St next month.', 1, 'Alice Admin', NOW(), NOW())
ON DUPLICATE KEY UPDATE id = id;
