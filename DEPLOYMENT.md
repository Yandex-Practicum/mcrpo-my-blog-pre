# Развертывание My Blog Backend

## Сборка и запуск

### 1. Сборка проекта

```bash
mvn clean package
```

Результат: `target/my-blog-back-app.jar`

### 2. Запуск приложения

```bash
java -jar target/my-blog-back-app.jar
```

Приложение запустится на http://localhost:8080

## Проверка API

### Создание поста

```bash
curl -X POST http://localhost:8080/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Мой первый пост",
    "text": "Это тестовый пост",
    "tags": ["тест", "первый"]
  }'
```

### Получение списка постов

```bash
curl "http://localhost:8080/posts?search=&pageNumber=1&pageSize=10"
```

### Получение поста по ID

```bash
curl http://localhost:8080/posts/1
```

### Добавление лайка

```bash
curl -X POST http://localhost:8080/posts/1/likes
```

### Создание комментария

```bash
curl -X POST http://localhost:8080/posts/1/comments \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Отличный пост!",
    "postId": 1
  }'
```

### Получение комментариев поста

```bash
curl http://localhost:8080/posts/1/comments
```

## Проверка работоспособности

После запуска приложения проверьте основные endpoints:

1. **Проверка пустого списка постов:**
   ```bash
   curl "http://localhost:8080/posts?search=&pageNumber=1&pageSize=10"
   ```

2. **Проверка получения поста по id:**
   ```bash
   curl http://localhost:8080/posts/1
   ```

## Логи

Логи приложения выводятся в консоль. Уровень логирования настраивается в `application.properties`.

## Troubleshooting

### Приложение не запускается

1. Проверьте версию Java:
   ```bash
   java -version
   # Должна быть Java 17 или выше
   ```

2. Убедитесь, что порт 8080 свободен:
   ```bash
   lsof -i :8080  # Mac/Linux
   netstat -ano | findstr :8080  # Windows
   ```

3. Проверьте права доступа к файлу JAR:
   ```bash
   chmod +x target/my-blog-back-app.jar  # Mac/Linux
   ```

### Ошибки базы данных

H2 база данных создается автоматически при старте. Если возникают проблемы:
1. Проверьте `schema.sql`
2. Доступ к H2 консоли: http://localhost:8080/h2-console

### CORS ошибки

Проверьте конфигурацию CORS в `WebConfig.java`.

## Остановка приложения

Для остановки приложения нажмите `Ctrl+C` в терминале или используйте:

```bash
# Найдите PID процесса
jps | grep my-blog-back-app

# Остановите процесс (замените PID на реальный)
kill PID
```

