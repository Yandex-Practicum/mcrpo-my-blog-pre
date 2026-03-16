# My Blog Backend

Бэкенд блога на Java 17, Spring Boot 3.4, Maven и H2.

## Что реализовано

- REST API для постов
- Добавление и удаление лайков
- Создание, редактирование и удаление комментариев
- Каскадное удаление постов вместе с комментариями, тегами и изображениями
- Автоматическое создание схемы БД при старте приложения
- Тесты на Spring Boot Test

## Технологии

- Java 17
- Spring Boot 3.4
- Spring Web
- Spring Data JDBC
- Maven
- H2
- Spring Boot Test

## Сборка

```bash
mvn clean package
```

После сборки исполняемый jar находится в `target/my-blog-back-app.jar`.

## Запуск тестов

```bash
mvn test
```

## Запуск приложения

```bash
java -jar target/my-blog-back-app.jar
```

Приложение стартует на `http://localhost:8080`.

## Конфигурация

Параметры подключения к базе и настройки приложения находятся в `src/main/resources/application.yml`.

Основные настройки:

- H2 in-memory database
- servlet path `/api`
- H2 console: `http://localhost:8080/h2-console`

## API

### Посты

- `GET /api/posts?search=&pageNumber=1&pageSize=10`
- `GET /api/posts/{id}`
- `POST /api/posts`
- `PUT /api/posts/{id}`
- `DELETE /api/posts/{id}`
- `POST /api/posts/{id}/likes`
- `DELETE /api/posts/{id}/likes`
- `PUT /api/posts/{id}/image`
- `GET /api/posts/{id}/image`

Пример создания поста:

```json
{
  "title": "Spring Boot migration",
  "text": "Post content",
  "tags": ["spring", "boot"]
}
```

### Комментарии

- `GET /api/posts/{postId}/comments`
- `GET /api/posts/{postId}/comments/{commentId}`
- `POST /api/posts/{postId}/comments`
- `PUT /api/posts/{postId}/comments/{commentId}`
- `DELETE /api/posts/{postId}/comments/{commentId}`

Пример создания комментария:

```json
{
  "text": "Nice post"
}
```

## Полезные ссылки

- API: `http://localhost:8080/api/posts`
- H2 console: `http://localhost:8080/h2-console`
