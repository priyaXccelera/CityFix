INSERT INTO departments (id, name, description, category_handled, created_at)
VALUES (1, 'Public Works', 'Handles roads, potholes and streetlights', 'Roads & Lighting', NOW())
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO departments (id, name, description, category_handled, created_at)
VALUES (2, 'Sanitation', 'Handles garbage collection and waste management', 'Waste Management', NOW())
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO issue_categories (id, name, department_id, default_priority)
VALUES (1, 'Pothole', 1, 'HIGH')
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO issue_categories (id, name, department_id, default_priority)
VALUES (2, 'Garbage Collection', 2, 'MEDIUM')
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO issue_categories (id, name, department_id, default_priority)
VALUES (3, 'Streetlight Outage', 1, 'LOW')
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO issues (id, title, description, category_id, area, latitude, longitude, photo_reference, status, priority, reported_by_user_id, reported_by_name, assigned_department_id, created_at, updated_at, resolved_at)
VALUES (1, 'Pothole on Main St', 'Large pothole causing traffic issues', 1, 'Downtown', 12.9716, 77.5946, NULL, 'REPORTED', 'HIGH', 2, 'Bob Reporter', NULL, NOW(), NOW(), NULL)
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO issues (id, title, description, category_id, area, latitude, longitude, photo_reference, status, priority, reported_by_user_id, reported_by_name, assigned_department_id, created_at, updated_at, resolved_at)
VALUES (2, 'Overflowing bin', 'Garbage bin overflowing for a week', 2, 'Uptown', 12.9800, 77.6000, NULL, 'IN_PROGRESS', 'MEDIUM', 3, 'Carla Citizen', 2, NOW(), NOW(), NULL)
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO issues (id, title, description, category_id, area, latitude, longitude, photo_reference, status, priority, reported_by_user_id, reported_by_name, assigned_department_id, created_at, updated_at, resolved_at)
VALUES (3, 'Streetlight broken', 'Streetlight has been out for days', 3, 'Westside', 12.9500, 77.5800, NULL, 'RESOLVED', 'LOW', 4, 'Dave Resident', 1, NOW(), NOW(), NOW())
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO issues (id, title, description, category_id, area, latitude, longitude, photo_reference, status, priority, reported_by_user_id, reported_by_name, assigned_department_id, created_at, updated_at, resolved_at)
VALUES (4, 'Large pothole near school', 'Dangerous pothole near school zone', 1, 'Downtown', 12.9720, 77.5950, NULL, 'ASSIGNED', 'HIGH', 3, 'Carla Citizen', 1, NOW(), NOW(), NULL)
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO issues (id, title, description, category_id, area, latitude, longitude, photo_reference, status, priority, reported_by_user_id, reported_by_name, assigned_department_id, created_at, updated_at, resolved_at)
VALUES (5, 'Garbage not collected', 'Weekly garbage pickup missed', 2, 'Downtown', 12.9730, 77.5960, NULL, 'REPORTED', 'MEDIUM', 2, 'Bob Reporter', NULL, NOW(), NOW(), NULL)
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO upvotes (id, issue_id, user_id, created_at)
VALUES (1, 1, 3, NOW())
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO upvotes (id, issue_id, user_id, created_at)
VALUES (2, 1, 4, NOW())
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO upvotes (id, issue_id, user_id, created_at)
VALUES (3, 4, 2, NOW())
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO comments (id, issue_id, text, posted_by_user_id, posted_by_name, posted_by_role, created_at)
VALUES (1, 1, 'This has been an issue for months, please fix soon.', 3, 'Carla Citizen', 'USER', NOW())
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO comments (id, issue_id, text, posted_by_user_id, posted_by_name, posted_by_role, created_at)
VALUES (2, 2, 'We have dispatched a sanitation crew.', 1, 'Alice Admin', 'ADMIN', NOW())
ON DUPLICATE KEY UPDATE id = id;
