ALTER TABLE thetiptop.users
    ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'USER';

UPDATE thetiptop.users
SET role = COALESCE(role, 'USER');
