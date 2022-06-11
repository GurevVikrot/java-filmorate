# java-filmorate
Template repository for Filmorate project.

## *[Диаграмма](https://dbdiagram.io/d/629e6c3f54ce2635276e8216) базы данных filmorate*
![db_diagram](/filmorate_db_diagram.png?raw=true)

## Опиcание диаграммы
 **users:**

    Содержит данные о пользователях

    user_id - Первичный ключ, уникальный id пользователя;

    login - Логин пользователя;

    name - Имя пользователя;

    email - Электронная почта пользователя;

    birthday - День рождения пользователя.
  
  **user_friends:**
     
    Содержит информацию о друзьях пользователя
     
    id - Первичный составной ключ (user_id, friend_id);
     
    user_id - id пользователя;
     
    friend_id - id друга пользователя;
     
    status_id - id статуса дружбы пользователей.
     
  **friend_status:**
     
    Содержит информацию о статусах дружбы
     
    status_id - Первичный ключ, id статуса дружбы;
     
    status - Название статуса дружбы.
    
  **films:**
  
    Содержит информацию о фильмах

    film_id - Первичный ключ, id пользователя;

    name - Название фильма;

    description - Описание фильма;

    rating - Рейтиг фильма;

    release_date - Дата выхода фильма в прокат;

    duration - Продолжительность фильма в минутах.
  
  **film_likes:**
  
    Связующая таблица, содержит информацию о том, кто ставил лайк фильму
  
    film_id - Первичный ключ, id фильма;
  
    user_id - id пользователя.
  
  **genre:**
  
    Содержит информацию о жанрах
  
    genre_id - Первичный ключ, id жанра;
  
    name - Название жанра.
  
  **film_genre:**
  
    Связующая таблица, содержит информацию о том к какому жанру/жанрам относится фильм
  
    film_id - Первичный ключ, id фильма;

    genre_id - id жанра.
  
  **rating_mpa:**
  
    Сожержит информацию о рейтингах Motion Picture Association (МРА)
    
    raiting_id - id рейтинга;
    
    rating - Название рейтинга.
    
    
  ## Примеры запросов
  
   **Вывод всех пользователей:**
    
     SELECT *
     FROM users;
     
   **Вывод всех друзей пользователя N и статуса дружбы**
   
     SELECT users.name AS name_of_friends
     FROM user_friends AS uf
     LEFT OUTER JOIN users ON uf.friend_id = users.user_id
     LEFT OUTER JOIN friend_status AS fs ON uf.status_id = fs.status_id
     WHERE user_id = N;

  **Вывод общих друзей пользователя N и пользователя T:**
    
     SELECT users.user_id AS n_t_friends
     FROM user_friends AS nuf
     LEFT OUTER JOIN users ON nuf.friend_id = users.user_id
     WHERE user_id = N AND
     	   user_id IN (SELECT users.user_id AS n_friends
     FROM user_friends AS tuf
     LEFT OUTER JOIN users ON tuf.friend_id = users.user_id
     WHERE user_id = T);

  **Вывод всех фильмов:**
    
     SELECT *
     FROM films;
     
  **Вывод топ 10 фильмов и количества лайков к ним:**
   
     SELECT films.name AS film,
	        COUNT(user_id) AS likes
     FROM film_likes AS fl
     INNER JOIN films ON fl.film_id = films.film_id
     GROUP BY fl.film_id
     ORDER BY likes DESC
     LIMIT 10;

  **Вывод жанра/жанров фильма N:**
    
     SELECT g.name AS genre
     FROM film_genre AS fg
     INNER JOIN films ON fg.film_id = films.film_id
     WHERE film_id = N
     GROUP BY film_id

   **Вывод рейтинга фильма N:**
    
     SELECT films.name,
	        r.rating
     FROM films
     INNER JOIN rating_mpa AS r ON films.rating = r.rating_id
     WHERE film_id = N
