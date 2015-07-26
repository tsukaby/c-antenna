ALTER TABLE site ADD tweet_count BIGINT NOT NULL DEFAULT 0 AFTER hatebu_count;
ALTER TABLE article ADD tweet_count BIGINT NOT NULL DEFAULT 0 AFTER hatebu_count;
