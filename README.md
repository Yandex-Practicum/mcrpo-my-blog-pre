# My Blog Backend (Spring Boot)

Backend приложения-блога на Spring Boot 3.2+, собирается через Maven и запускается как executable JAR со встроенным Tomcat.

## Технологии

- Java 17
- Spring Boot 3.2.x
- Spring Boot Starter Web
- Spring Boot Starter Data JDBC
- H2 (in-memory) (база данных)
- JUnit 5 / Spring Boot Test

## Структура backend

```text
src/main/java/com/myblog/
  MyBlogApplication.java
  controller/
  service/
  dao/
  model/
  dto/
  config/
src/main/resources/
  application.properties
  schema.sql
src/test/java/com/myblog/
  controller/
  service/
  dao/
```

## Сборка

```bash
mvn clean package
```

После сборки создается executable jar в `target/`.

## Запуск тестов

```bash
mvn test
```

Тесты используют `@SpringBootTest` и контекст Spring кешируется между запусками test-классов.

## Запуск backend

```bash
java -jar target/my-blog-back-app.jar
```

По умолчанию приложение стартует на `http://localhost:8080`.

## База данных

- Используется H2 in-memory база.
- Схема БД автоматически создается при старте из `src/main/resources/schema.sql`.
- H2 console доступна по адресу `http://localhost:8080/h2-console`.

Параметры подключения по умолчанию:

- URL: `jdbc:h2:mem:blogdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- Username: `sa`
- Password: *(пустой)*

## Запуск Frontend
cd frontend
npm install
npm run dev
http://localhost:3000/


## Примеры использования API

Получить список постов:

```bash
curl "http://localhost:8080/api/posts?search=&pageNumber=1&pageSize=10"
```

Создать пост:

```bash
curl -X POST "http://localhost:8080/api/posts" \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"Post title\",\"text\":\"Post body\",\"tags\":[\"spring\",\"java\"]}"
```

Поставить лайк:

```bash
curl -X POST "http://localhost:8080/api/posts/1/likes"
```


