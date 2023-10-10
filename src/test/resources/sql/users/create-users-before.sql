ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;

insert into users (name, login, email, birthday) values
('Robert De Niro','RagingBull','robert@deniro.com','1943-08-17'),
('Leonardo DiCaprio','Inception','leo-dc@yahoo.com','1974-11-11'),
('Al Pacino','Serpico','al-pacino777@gmail.com','1940-04-25'),
('Sylvester Stallone','Rocky','rocky@stallone.com','1946-07-06');

insert into likes (user_id, film_id) values
(1, 9), (1, 8), (1, 1), (1, 4),
(2, 8), (2, 4),
(3, 8), (3, 9), (3, 7), (3, 6), (3, 5), (3, 1),
(4, 2), (4, 1), (4, 9);