# My Blog Backend Application

## Описание

Это бэкенд-приложение для блога, разработанное с использованием Spring Boot. Оно предоставляет RESTful API для управления постами и комментариями.

## Требования

- Java 17  
- Maven

## Сборка проекта

Чтобы собрать проект, выполните следующую команду в корне проекта:
bash
mvn clean package
```

Эта команда создаст исполняемый JAR-файл в каталоге target.

Запуск проекта
Для запуска приложения используйте следующую команду, заменив my-blog-back-app-1.0.0.jar на имя вашего JAR-файла:

java -jar target/my-blog-back-app-1.0.0.jar
После запуска приложение будет доступно по умолчанию на http://localhost:8080.

Тестирование
Проект включает модульные тесты, которые можно запустить с помощью следующей команды:

mvn test
Тесты проверяют функциональность сервисов и DAO, обеспечивая надежность приложения. Тесты используют Spring Boot Test.

Запуск тестов
Вы можете запускать отдельные тесты, например, для класса PostServiceTest, с помощью команды:

mvn -Dtest=PostServiceTest test
Использование API
Получить все посты
GET /posts?search={search}&pageNumber={pageNumber}&pageSize={pageSize}
Получить пост по ID
GET /posts/{id}
Создать новый пост
POST /posts    
Content-Type: application/json

{  
    "title": "My New Post",  
    "text": "This is the content of the post.",  
    "tags": ["tag1", "tag2"]  
}
Обновить пост
PUT /posts/{id}  
Content-Type: application/json

{  
    "title": "Updated Title",  
    "text": "Updated content.",  
    "tags": ["tag1", "tag3"]  
}
Удалить пост
DELETE /posts/{id}
Увеличить количество лайков
POST /posts/{id}/likes
Уменьшить количество лайков
DELETE /posts/{id}/likes
Сохранить изображение для поста
PUT /posts/{id}/image    
Content-Type: multipart/form-data

{  
    "image": {file}  
}
Получить изображение поста
GET /posts/{id}/image