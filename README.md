# MyBlog Backend

Бэкенд для приложения-блога на Spring Boot.

## Технологии

- Java 17
- Spring Boot 3.2
- Spring Web MVC
- Spring Data JDBC
- H2 Database
- Maven
- JUnit 5 + SpringBootTest

## Сборка проекта

```bash
# Сборка без тестов
mvn clean package -DskipTests

# Сборка с тестами
mvn clean package

# Только тесты
mvn test

# Запуск через Java (после сборки)
java -jar target/myblog-backend-1.0.0.jar
```

## Примеры API запросов (curl)

```bash
# ============== ПОСТЫ ==============

# Получить все посты (с пагинацией)
curl "http://localhost:8080/api/posts?search=&pageNumber=1&pageSize=10"

# Получить пост по ID
curl http://localhost:8080/api/posts/1

# Создать новый пост
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Мой первый пост",
    "text": "Это содержание моего первого поста",
    "tags": ["java", "spring", "blog"]
  }'

# Обновить пост
curl -X PUT http://localhost:8080/api/posts/1 \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "title": "Обновленный заголовок",
    "text": "Обновленное содержание",
    "tags": ["updated", "java"]
  }'

# Удалить пост
curl -X DELETE http://localhost:8080/api/posts/1

# ============== ЛАЙКИ ==============

# Поставить лайк
curl -X POST http://localhost:8080/api/posts/1/likes

# Убрать лайк
curl -X DELETE http://localhost:8080/api/posts/1/likes

# ============== КОММЕНТАРИИ ==============

# Получить все комментарии к посту
curl http://localhost:8080/api/posts/1/comments

# Добавить комментарий
curl -X POST http://localhost:8080/api/posts/1/comments \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Отличный пост! Спасибо за информацию",
    "postId": 1
  }'

# Получить комментарий по ID
curl http://localhost:8080/api/posts/1/comments/1

# Редактировать комментарий
curl -X PUT http://localhost:8080/api/posts/1/comments/1 \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "text": "Обновленный комментарий",
    "postId": 1
  }'

# Удалить комментарий
curl -X DELETE http://localhost:8080/api/posts/1/comments/1

# ============== ИЗОБРАЖЕНИЯ ==============

# Загрузить изображение для поста
curl -X PUT http://localhost:8080/api/posts/1/image \
  -F "image=@/путь/к/вашему/изображению.jpg"

# Получить изображение поста
curl http://localhost:8080/api/posts/1/image --output image.jpg
```

### Примечания
- Замените `1` в URL на реальные ID постов и комментариев
- Для создания поста обязательны поля: `title`, `text`
- Поле `tags` опционально
- При загрузке изображения укажите правильный путь к файлу на вашем компьютере
- Все команды выполняются в терминале (Linux/Mac) или в Git Bash (Windows)
