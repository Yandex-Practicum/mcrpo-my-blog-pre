# My Blog Backend

Spring Boot 3.2+ REST API для блога с постами, комментариями и тегами.

## Описание проекта

Проект на Spring Boot 3.2+ с H2 базой данных. Поддерживает CRUD операции для постов и комментариев, систему лайков, управление тегами и загрузку изображений.

## Технологии

- Java 17
- Spring Boot 3.2+
- Maven
- H2 Database
- JUnit 5 + Mockito

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

Приложение запустится на http://localhost:8080

## API Endpoints

### Posts
- `GET /posts?search=&pageNumber=1&pageSize=10` - получить список постов (пагинация и поиск; параметры обязательны)
- `POST /posts` - создать новый пост
- `GET /posts/{id}` - получить пост по ID
- `PUT /posts/{id}` - обновить пост
- `DELETE /posts/{id}` - удалить пост
- `POST /posts/{id}/likes` - добавить лайк
- `DELETE /posts/{id}/likes` - удалить лайк
- `GET /posts/{id}/image` - получить изображение поста
- `PUT /posts/{id}/image` - загрузить изображение поста (multipart form-data, поле `image`)

### Comments
- `GET /posts/{postId}/comments` - получить комментарии поста
- `POST /posts/{postId}/comments` - создать комментарий
- `GET /posts/{postId}/comments/{commentId}` - получить комментарий
- `PUT /posts/{postId}/comments/{commentId}` - обновить комментарий
- `DELETE /posts/{postId}/comments/{commentId}` - удалить комментарий

## Конфигурация

- `application.properties` - настройки приложения
- `schema.sql` - SQL схема базы данных
- База данных H2 доступна по адресу: http://localhost:8080/h2-console

## Структура проекта

```
src/main/java/com/myblog/
├── config/          # Spring конфигурация
├── controller/      # REST контроллеры
├── service/         # Бизнес-логика
├── dao/             # Работа с БД
├── model/           # Модели данных
└── dto/             # Data Transfer Objects
```
