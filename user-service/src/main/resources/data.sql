-- User accounts are intentionally not seeded. SUPER_ADMIN can only be registered through the
-- public registration flow when no SUPER_ADMIN account exists.
-- Preserve existing accounts when the status column is added by treating them as active.
UPDATE users SET status = 'ACTIVE' WHERE status IS NULL;
