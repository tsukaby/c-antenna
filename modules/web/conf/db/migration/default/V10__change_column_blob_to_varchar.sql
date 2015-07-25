ALTER TABLE site ADD thumbnail_url VARCHAR(255) AFTER thumbnail;
ALTER TABLE site DROP thumbnail;
