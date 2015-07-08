ALTER TABLE article ADD published_at DATETIME NOT NULL DEFAULT AFTER url;
ALTER TABLE article CHANGE created_at created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE article ADD updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;
