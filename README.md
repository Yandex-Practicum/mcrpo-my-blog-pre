# My Blog Backend

Бэкенд приложения-блога на Spring Boot.

## Сборка проекта

```bash
mvn clean package
```

## Запуск тестов

```bash
mvn test
```

## Запуск приложения

```bash
java -jar target/my-blog-back-app.jar
```

Приложение запустится на `http://localhost:8080`. API доступен по пути `/api`.

## Запуск фронтенда

```bash
cd frontend
npm install
npm run dev
```

Фронтенд запустится на `http://localhost:3000`.

## Описание API

Базовый путь: `http://localhost:8080/api`

### Посты

| Метод | Путь | Описание |
| ----- | ---- | -------- |
| GET | `/posts?search=&pageNumber=1&pageSize=10` | Список постов с поиском и пагинацией |
| GET | `/posts/{id}` | Получить пост по ID |
| POST | `/posts` | Создать пост |
| PUT | `/posts/{id}` | Обновить пост |
| DELETE | `/posts/{id}` | Удалить пост |
| POST | `/posts/{id}/likes` | Поставить лайк |
| DELETE | `/posts/{id}/likes` | Убрать лайк |
| PUT | `/posts/{id}/image` | Загрузить изображение (multipart) |
| GET | `/posts/{id}/image` | Получить изображение |

### Комментарии

| Метод | Путь | Описание |
| ----- | ---- | -------- |
| GET | `/posts/{postId}/comments` | Все комментарии поста |
| GET | `/posts/{postId}/comments/{commentId}` | Получить комментарий |
| POST | `/posts/{postId}/comments` | Создать комментарий |
| PUT | `/posts/{postId}/comments/{commentId}` | Обновить комментарий |
| DELETE | `/posts/{postId}/comments/{commentId}` | Удалить комментарий |
