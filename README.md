

### 1. Подготовка окружения

Убедитесь, что у вас установлены:
- **Java JDK 17** или выше.
- **Maven** для управления зависимостями и сборкой проекта.
- **MySQL** для работы с базой данных.

### 2. Настройка базы данных

Создайте базу данных, если вы этого еще не сделали:

```sql  
CREATE DATABASE mydb;
```

### 3. Настройка параметров подключения

В файле `application.properties` укажите правильные параметры подключения к вашей базе данных:

```properties  
spring.datasource.url=jdbc:mysql://localhost:3306/mydb  
spring.datasource.username=your_username  
spring.datasource.password=your_password  
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver  
```

### 4. Сборка проекта

В корневом каталоге вашего проекта выполните:

```bash  
mvn clean package  
```

Эта команда соберет ваш проект и создаст JAR-файл в каталоге `target`.

### 5. Запуск тестов

Для запуска тестов выполните команду:

```bash  
mvn test  
```

Эта команда выполнит все тесты, которые вы написали для вашего приложения.

### 6. Запуск приложения

После успешной сборки вы можете запустить приложение:

```bash  
java -jar target/my-blog-0.0.1-SNAPSHOT.jar  
```

Если вы используете IDE, например IntelliJ IDEA, вы можете запустить класс `MyBlogApplication` напрямую.

### 7. Проверка API

Используйте Postman или curl для тестирования вашего API. Вот несколько примеров запросов:

- Получение постов:
  ```bash  
  GET http://localhost:8080/posts?search=example&pageNumber=1&pageSize=10  
  ```

- Создание комментария:
  ```bash  
  POST http://localhost:8080/posts/1/comments  
  Content-Type: application/json  
  {
      "text": "This is a comment",
      "postId": 1  
  }
  ```

- Обновление комментария:
  ```bash  
  PUT http://localhost:8080/posts/1/comments/1  
  Content-Type: application/json  
  {
      "text": "This is an updated comment"
  }
  ```

- Удаление поста:
  ```bash  
  DELETE http://localhost:8080/posts/1  
  ```

### 8. Логи и отладка

Следите за выводом логов в консоли, чтобы отладить возможные проблемы. Убедитесь, что база данных запущена и параметры подключения настроены правильно.

### 9. Завершение

Теперь ваше приложение готово к работе. Вы можете взаимодействовать с API и добавлять дополнительные функции по мере необходимости. Если у вас есть вопросы по коду или возникли проблемы, не стесняйтесь спрашивать!