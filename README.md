#  Миграция проекта на Spring Boot 4.0.3



---

## Сборка бэкенда

```bash
# Установка зависимостей
mvn clean install

# Сборка проекта
mvn clean package
```

Результат: `target/my-blog-back-app-1.0.0.jar`



## Запуск бэкенда

Вариант 1 - Запуск main метода класса Application.java;

Вариант 2 - Запуск через исполняемый jar файл:

```bash
java -jar target/my-blog-back-app-1.0.0.jar  
```
Backend запустится на http://localhost:8080, убедитесь, что порт свободен.



## Проверка 

Откройте в браузере: http://localhost:8080/api/posts?search=&pageNumber=1&pageSize=10

Результат:
```
{
"posts": [],
"hasPrev": false,
"hasNext": false,
"lastPage": 1
}
```



## Работа бэкенда
Используя командную строку или postman

### 1) Создание поста

```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Тестовый пост",
    "text": "Это тестовый пост",
    "tags": ["тест", "первый"]
  }'
```
Результат (200 OK):
```bash
{
    "id": 1,
    "title": "Тестовый пост",
    "text": "Это тестовый пост",
    "tags": [
        "тест",
        "первый"
    ],
    "likesCount": 0,
    "commentsCount": 0
}
```

### 2) Удаление поста
```bash
curl -X DELETE http://localhost:8080/api/posts/{id}
```

### 3) Получение списка постов

```bash
curl http://localhost:8080/api/posts?search=&pageNumber=1&pageSize=10
```
Для powershell необходимо обернуть амперсанд в двойные кавычки "&"

Результат (200 OK):
```bash
{
    "posts": [
        {
            "id": 1,
            "title": "Тестовый пост",
            "text": "Это тестовый пост",
            "tags": [
                "тест",
                "первый"
            ],
            "likesCount": 0,
            "commentsCount": 0
        }
    ],
    "hasPrev": false,
    "hasNext": false,
    "lastPage": 1
}
```

### 4) Получение поста по ID

```bash
curl http://localhost:8080/api/posts/1
```
Результат (200 OK):
```bash
{
    "id": 1,
    "title": "Тестовый пост",
    "text": "Это тестовый пост",
    "tags": [
        "тест",
        "первый"
    ],
    "likesCount": 0,
    "commentsCount": 0
}
```

### 5) Добавление лайка

```bash
curl -X POST http://localhost:8080/api/posts/1/likes
```

Результат (200 OK):
```bash
1
```

### 6) Удаление лайка

```bash
curl -X DELETE http://localhost:8080/api/posts/1/likes
```
Результат (200 OK):
```bash
0
```
### 7) Создание комментария

```bash
curl -X POST http://localhost:8080/api/posts/1/comments \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Отличный пост!",
    "postId": 1
  }'
```
Результат (200 OK):
```bash
{
"id": 1,
"text": "Отличный пост!",
"postId": 1
}
```


### 8) Удаление комментария

```bash
curl -X DELETE http://localhost:8080/api/posts/1/comments/1
```

## Запуск тестов

```bash
mvn test
```





