-- Очистить таблицы перед вставкой
DELETE FROM comments;
DELETE FROM post_tags;
DELETE FROM tags;
DELETE FROM posts;

-- Сбросить счетчики автоинкремента
ALTER TABLE posts ALTER COLUMN id RESTART WITH 1;
ALTER TABLE tags ALTER COLUMN id RESTART WITH 1;
ALTER TABLE comments ALTER COLUMN id RESTART WITH 1;

-- Вставляем тестовые данные БЕЗ указания ID (пусть H2 генерирует сама)
INSERT INTO posts (title, text, likes_count) 
VALUES ('First Post', 'Content of first post', 5);

INSERT INTO posts (title, text, likes_count) 
VALUES ('Second Post', 'Content of second post', 3);

INSERT INTO tags (name) VALUES ('java');
INSERT INTO tags (name) VALUES ('spring');

-- Теперь нужно получить ID для связей
-- В тестах мы будем искать посты по title, а не по ID
INSERT INTO post_tags (post_id, tag_id) 
VALUES (
    (SELECT id FROM posts WHERE title = 'First Post'),
    (SELECT id FROM tags WHERE name = 'java')
);

INSERT INTO post_tags (post_id, tag_id) 
VALUES (
    (SELECT id FROM posts WHERE title = 'First Post'),
    (SELECT id FROM tags WHERE name = 'spring')
);

INSERT INTO comments (text, post_id) 
VALUES ('Great post!', (SELECT id FROM posts WHERE title = 'First Post'));

INSERT INTO comments (text, post_id) 
VALUES ('Thanks for sharing', (SELECT id FROM posts WHERE title = 'First Post'));

