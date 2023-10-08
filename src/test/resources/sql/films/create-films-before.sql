ALTER TABLE mpa ALTER COLUMN mpa_id RESTART WITH 1;

INSERT INTO mpa (name) VALUES
('G'),
('PG'),
('PG-13'),
('R'),
('NC-17');


ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;

insert into films (name, description, release_date, mpa_id, duration) values
--1
('Lolita','How did they ever make a movie of Lolita?',
 '1962-06-13', 4, 153),
--2
('Dr. Strangelove or: How I Learned to Stop Worrying and Love the Bomb', 'the hot-line suspense comedy',
'1963-01-29', 4, 95),
--3
('2001: A Space Odyssey','the time is now',
 '1968-04-02', 1, 149),
--4
('A Clockwork Orange','Being the adventures of a young man ... who could not resist pretty girls ... or a bit of the old ultra-violence ...  ... or was he ?',
 '1971-12-19', 4, 137),
--5
('Barry Lyndon','At long last Redmond Barry became a gentleman -- and that was his tragedy',
 '1975-12-11', 2, 185),
--6
('The Shining', 'The Horror is driving him crazy',
 '1980-05-23', 4, 144),
--7
('Full Metal Jacket','Born to Kill',
 '1987-06-17', 4, 116),
--8
('Eyes Wide Shut','Cruise. Kidman. Kubrick',
 '1999-07-13', 4, 159),
--9
 ('Raging Bull', 'Джейк ЛаМотта, получивший прозвище Бронксский Бык, - боксер.',
 '1980-11-13', 4, 129);

insert into films_director (director_id, film_id) values
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
(3, 9);
