#!Ups

INSERT INTO SITE
VALUES (1, 'site_name', 'http://example.com', 'http://example.com/index.rdf', NULL, '', 0, '2014-01-01 00:00:00');
INSERT INTO SITE SELECT
                   ID + 1,
                   NAME,
                   URL,
                   RSS_URL,
                   THUMBNAIL,
                   SCRAPING_CSS_SELECTOR,
                   CLICK_COUNT,
                   CRAWLED_AT
                 FROM SITE;
INSERT INTO SITE SELECT
                   ID + 2,
                   NAME,
                   URL,
                   RSS_URL,
                   THUMBNAIL,
                   SCRAPING_CSS_SELECTOR,
                   CLICK_COUNT,
                   CRAWLED_AT
                 FROM SITE;
INSERT INTO SITE SELECT
                   ID + 4,
                   NAME,
                   URL,
                   RSS_URL,
                   THUMBNAIL,
                   SCRAPING_CSS_SELECTOR,
                   CLICK_COUNT,
                   CRAWLED_AT
                 FROM SITE;
INSERT INTO SITE SELECT
                   ID + 8,
                   NAME,
                   URL,
                   RSS_URL,
                   THUMBNAIL,
                   SCRAPING_CSS_SELECTOR,
                   CLICK_COUNT,
                   CRAWLED_AT
                 FROM SITE;
INSERT INTO SITE SELECT
                   ID + 16,
                   NAME,
                   URL,
                   RSS_URL,
                   THUMBNAIL,
                   SCRAPING_CSS_SELECTOR,
                   CLICK_COUNT,
                   CRAWLED_AT
                 FROM SITE;
