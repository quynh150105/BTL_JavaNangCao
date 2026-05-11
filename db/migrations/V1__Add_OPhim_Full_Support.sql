-- ============================================
-- DATABASE MIGRATION: Add OPhim Full Support
-- Date: 2026-05-11
-- ============================================

-- 1. Add new columns to movies table
ALTER TABLE movies ADD COLUMN view BIGINT DEFAULT NULL;
ALTER TABLE movies ADD COLUMN cinema_screen BOOLEAN DEFAULT FALSE;
ALTER TABLE movies ADD COLUMN exclusive_subtitle BOOLEAN DEFAULT FALSE;
ALTER TABLE movies ADD COLUMN notify VARCHAR(500) DEFAULT NULL;

-- 2. Create table for movie actors
CREATE TABLE IF NOT EXISTS movie_actors (
    movie_id BIGINT NOT NULL,
    actor VARCHAR(255) NOT NULL,
    CONSTRAINT fk_movie_actors FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE INDEX idx_movie_actors_id ON movie_actors(movie_id);

-- 3. Create table for movie directors
CREATE TABLE IF NOT EXISTS movie_directors (
    movie_id BIGINT NOT NULL,
    director VARCHAR(255) NOT NULL,
    CONSTRAINT fk_movie_directors FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE INDEX idx_movie_directors_id ON movie_directors(movie_id);

-- 4. Update episode table if needed (add fetch strategy optimization)
-- Note: This is informational, Hibernate handles the mapping

-- 5. Add indexes for better query performance
ALTER TABLE movies ADD INDEX idx_movie_view (view);
ALTER TABLE movies ADD INDEX idx_movie_cinema_screen (cinema_screen);

-- ============================================
-- VERIFICATION QUERIES
-- ============================================

-- Check if columns were added successfully
SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'movies'
AND COLUMN_NAME IN ('view', 'cinema_screen', 'exclusive_subtitle', 'notify');

-- Check if tables were created
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_NAME IN ('movie_actors', 'movie_directors');

-- ============================================
-- ROLLBACK SCRIPT (If needed)
-- ============================================

/*
ALTER TABLE movies DROP COLUMN view;
ALTER TABLE movies DROP COLUMN cinema_screen;
ALTER TABLE movies DROP COLUMN exclusive_subtitle;
ALTER TABLE movies DROP COLUMN notify;

DROP TABLE IF EXISTS movie_actors;
DROP TABLE IF EXISTS movie_directors;
*/

-- ============================================
-- NOTES
-- ============================================
/*
- Sử dụng Quarkus Flyway hoặc manual execution
- Nên test trên dev database trước production
- Backup database trước khi chạy migration
- ORM sẽ tự động manage foreign keys
*/

