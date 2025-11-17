-- Sample data for local testing

-- Prizes
INSERT INTO thetiptop.prizes (name, description, image, created_at, updated_at)
VALUES
    ('Goodies Pack', 'Goodies exclusifs pour la communauté TipTop', 'goodies.png', NOW(), NOW()),
    ('VIP Pass', 'Accès VIP à la soirée finale', 'vip-pass.png', NOW(), NOW()),
    ('Coupon Remise', 'Coupon de réduction -20%', 'coupon.png', NOW(), NOW());

-- Users
INSERT INTO thetiptop.users (first_name, last_name, email, password, role, created_at, updated_at)
VALUES
    ('Alice', 'Durand', 'alice@example.com', '$2a$10$MDMr8TWLkFIW5LBMJ6BDIexiFji49rvtRHyOWFfLntRcfV5.4UN/S', 'ADMIN', NOW(), NOW()),
    ('Bruno', 'Martin', 'bruno@example.com', '$2a$10$4xPcZPF7.2Sq7eF1NPCAsu387X9MkfbRivKpJjC9v7hLTdST8t2uW', 'USER', NOW(), NOW()),
    ('Chloe', 'Bernard', 'chloe@example.com', '$2a$10$Cra0uZuhaUTuHqj./hrzne8wYMFH7Q3HEjcnKp/EIX6zJNgiQ4Vbe', 'USER', NOW(), NOW());

-- Concours
INSERT INTO thetiptop.concours (start_date, end_date, winner_id, created_at, updated_at)
VALUES (
    NOW() - INTERVAL '30 days',
    NOW() - INTERVAL '2 days',
    (SELECT id FROM thetiptop.users WHERE email = 'alice@example.com'),
    NOW(),
    NOW()
);

-- Codes
INSERT INTO thetiptop.codes (code, prize_id, status, expiration_date, issue_date, created_at, updated_at)
VALUES (
    'ABC123DEF4',
    (SELECT id FROM thetiptop.prizes WHERE name = 'Goodies Pack'),
    'NEW',
    NOW() + INTERVAL '30 days',
    NOW() - INTERVAL '1 days',
    NOW(),
    NOW()
);

INSERT INTO thetiptop.codes (code, prize_id, status, expiration_date, issue_date, use_date, validated_at, validated_by, created_at, updated_at)
VALUES (
    'XYZ789LMN0',
    (SELECT id FROM thetiptop.prizes WHERE name = 'VIP Pass'),
    'USED',
    NOW() + INTERVAL '15 days',
    NOW() - INTERVAL '5 days',
    NOW() - INTERVAL '1 days',
    NOW() - INTERVAL '1 days',
    (SELECT id FROM thetiptop.users WHERE email = 'bruno@example.com'),
    NOW(),
    NOW()
);

INSERT INTO thetiptop.codes (code, prize_id, status, expiration_date, issue_date, use_date, claim_date, validated_at, validated_by, created_at, updated_at)
VALUES (
    'WIN1234567',
    (SELECT id FROM thetiptop.prizes WHERE name = 'Coupon Remise'),
    'CLAIMED',
    NOW() + INTERVAL '5 days',
    NOW() - INTERVAL '10 days',
    NOW() - INTERVAL '3 days',
    NOW() - INTERVAL '2 days',
    NOW() - INTERVAL '2 days',
    (SELECT id FROM thetiptop.users WHERE email = 'bruno@example.com'),
    NOW(),
    NOW()
);

INSERT INTO thetiptop.codes (code, prize_id, status, expiration_date, issue_date, created_at, updated_at)
VALUES (
    'GIFT987654',
    (SELECT id FROM thetiptop.prizes WHERE name = 'Goodies Pack'),
    'NEW',
    NOW() + INTERVAL '45 days',
    NOW() - INTERVAL '2 days',
    NOW(),
    NOW()
);

-- Participations
INSERT INTO thetiptop.participations (user_id, code_id, created_at, updated_at)
SELECT u.id, c.id, NOW() - INTERVAL '1 days', NOW() - INTERVAL '1 days'
FROM thetiptop.users u, thetiptop.codes c
WHERE u.email = 'alice@example.com'
  AND c.code = 'XYZ789LMN0';

INSERT INTO thetiptop.participations (user_id, code_id, created_at, updated_at)
SELECT u.id, c.id, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'
FROM thetiptop.users u, thetiptop.codes c
WHERE u.email = 'chloe@example.com'
  AND c.code = 'WIN1234567';

-- Newsletters
INSERT INTO thetiptop.newsletters (email, created_at, updated_at)
VALUES
    ('alice@example.com', NOW(), NOW()),
    ('vip@thetiptop.local', NOW(), NOW()),
    ('promo@thetiptop.local', NOW(), NOW());
