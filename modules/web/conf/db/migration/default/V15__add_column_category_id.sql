ALTER TABLE article ADD category_id bigint NULL AFTER description;
ALTER TABLE article ADD FOREIGN KEY (category_id) REFERENCES categories (id);