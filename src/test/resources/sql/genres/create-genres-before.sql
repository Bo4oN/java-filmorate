ALTER TABLE genre_types ALTER COLUMN genre_types_id RESTART WITH 1;

INSERT INTO genre_types (name) VALUES
--1
('Комедия'),
--2
('Драма'),
--3
('Мультфильм'),
--4
('Триллер'),
--5
('Документальный'),
--6
('Боевик');
