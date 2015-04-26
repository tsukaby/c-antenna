CREATE TABLE article (
  id          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  site_id     BIGINT(20)   NOT NULL,
  url         VARCHAR(700) NOT NULL,
  title       VARCHAR(255) NOT NULL,
  tag         VARCHAR(1024)         DEFAULT NULL,
  click_count BIGINT(20)   NOT NULL DEFAULT 0,
  created_at  DATETIME     NOT NULL,
  PRIMARY KEY (id),
  INDEX article_url_index(url),
  INDEX article_site_id_index(site_id)
)
  ROW_FORMAT = DYNAMIC;

CREATE TABLE site (
  id                    BIGINT(20)   NOT NULL AUTO_INCREMENT,
  name                  VARCHAR(255) NOT NULL,
  url                   VARCHAR(255) NOT NULL,
  rss_url               VARCHAR(255) NOT NULL,
  thumbnail             MEDIUMBLOB,
  scraping_css_selector VARCHAR(255) NOT NULL,
  click_count           BIGINT(20)   NOT NULL DEFAULT 0,
  hatebu_count          BIGINT(20)   NOT NULL DEFAULT 0,
  crawled_at            DATETIME     NOT NULL,
  PRIMARY KEY (id)
);

CREATE VIEW site_summary AS
  SELECT
    S.id,
    S.name,
    S.url,
    S.thumbnail,
    COUNT(1) AS article_count,
    S.click_count,
    S.hatebu_count
  FROM
    article A, site S
  WHERE
    S.id = A.site_id
  GROUP BY
    A.site_id
