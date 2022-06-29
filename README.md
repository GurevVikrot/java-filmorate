# java-filmorate
Template repository for Filmorate project.

## *[Диаграмма](https://dbdiagram.io/d/629e6c3f54ce2635276e8216) базы данных filmorate*
![db_diagram](/db_diagram.png?raw=true)

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
     
    Первичный составной ключ (user_id, friend_id);
     
    user_id - id пользователя;
     
    friend_id - id друга пользователя;
     
    status - boolean статуса дружбы пользователей.
    
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

    Первичный составной ключ (film_id, user_id);
  
    film_id -  id фильма;
  
    user_id - id пользователя.
  
  **genre:**
  
    Содержит информацию о жанрах
  
    genre_id - Первичный ключ, id жанра;
  
    name - Название жанра.
  
  **film_genre:**
  
    Связующая таблица, содержит информацию о том к какому жанру/жанрам относится фильм

    Первичный составной ключ (film_id, genre_id);
  
    film_id - id фильма;

    genre_id - id жанра.
  
  **rating_mpa:**
  
    Сожержит информацию о рейтингах Motion Picture Association (МРА)
    
    raiting_id - id рейтинга. Первичный ключ;
    
    rating - Название рейтинга.
    
    
  ## Примеры запросов
  
   **Вывод всех пользователей:**
    
     SELECT *
     FROM users;
     
   **Вывод всех друзей пользователя N и статуса дружбы**
   
     SELECT users.name AS name_of_friends,
            uf.status
     FROM user_friends AS uf
     LEFT OUTER JOIN users ON uf.friend_id = users.user_id
     WHERE user_id = N;

  **Вывод общих друзей пользователя N и пользователя T:**

     SELECT friend_id 
     FROM user_friends 
     WHERE user_id = ? AND friend_id IN
                    (SELECT friend_id
                     FROM user_friends 
                     WHERE user_id = ?)

  **Вывод всех фильмов:**
    
     SELECT *
     FROM films;
     
  **Вывод топ 10 названий фильмов и количества лайков к ним:**
   
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
