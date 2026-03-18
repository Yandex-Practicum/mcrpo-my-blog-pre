# My Blog Backend (Spring Boot)

Бэкенд приложения-блога, переписанный с использованием Spring Boot.

## Технологии

* Java 17
* Spring Boot 3.2
* Spring Boot Web
* Spring Boot JDBC
* H2 Database (in-memory)
* Maven
* JUnit 5 / Spring Boot Test

---

## Сборка проекта

```bash
mvn clean package
```

После сборки исполняемый файл появится в папке:

```text
target/my-blog-back-app.jar
```

---

## Запуск тестов

```bash
mvn clean test
```

Все тесты должны завершиться успешно (BUILD SUCCESS).

---

## Запуск приложения

```bash
java -jar target/my-blog-back-app.jar
```

После запуска приложение будет доступно по адресу:

```text
http://localhost:8080
```

---

## Проверка API

### Получить список постов

```text
GET http://localhost:8080/api/posts?search=&pageNumber=1&pageSize=10
```

---

### Создать пост

```bash
curl -X POST http://localhost:8080/api/posts \
-H "Content-Type: application/json" \
-d '{"title":"Test","text":"Hello","tags":["java","spring"]}'
```

---

### Получить пост по id

```text
GET http://localhost:8080/api/posts/{id}
```

---

### Обновить пост

```bash
curl -X PUT http://localhost:8080/api/posts/{id} \
-H "Content-Type: application/json" \
-d '{"id":1,"title":"Updated","text":"New text","tags":["spring"]}'
```

---

### Удалить пост

```text
DELETE http://localhost:8080/api/posts/{id}
```

---

### Лайки

Добавить лайк:

```text
POST http://localhost:8080/api/posts/{id}/likes
```

Убрать лайк:

```text
DELETE http://localhost:8080/api/posts/{id}/likes
```

---

### Комментарии

Создать комментарий:

```text
POST http://localhost:8080/api/posts/{postId}/comments
```

Обновить комментарий:

```text
PUT http://localhost:8080/api/posts/{postId}/comments/{id}
```

Удалить комментарий:

```text
DELETE http://localhost:8080/api/posts/{postId}/comments/{id}
```

---

## База данных

Используется встроенная база данных H2 (in-memory).

Настройки находятся в:

```text
src/main/resources/application.properties
```

Структура базы данных автоматически создаётся при старте приложения из файла:

```text
src/main/resources/schema.sql
```

---

## Реализовано

* Миграция проекта на Spring Boot
* Упаковка приложения в Executable JAR
* Запуск на встроенном сервере (Tomcat)
* REST API для:

  * постов
  * лайков
  * комментариев
* Каскадное удаление постов
* Unit и Integration тесты с использованием Spring Boot Test

---

## Проверка перед сдачей

Проект должен успешно выполнять команды:

```bash
mvn clean test
mvn clean package
java -jar target/my-blog-back-app.jar
```

И API должен открываться в браузере:

```text
http://localhost:8080/api/posts
```

---

## Примечание

База данных является временной (in-memory), поэтому все данные удаляются при перезапуске приложения.
