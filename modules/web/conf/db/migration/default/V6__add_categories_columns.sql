CREATE TABLE categories(
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE site_categories(
  site_id BIGINT(20) NOT NULL AUTO_INCREMENT,
  category_id BIGINT(20) NOT NULL,
  PRIMARY KEY (site_id, category_id),
  FOREIGN KEY (site_id) REFERENCES site(id),
  FOREIGN KEY (category_id) REFERENCES categories(id)
);
