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