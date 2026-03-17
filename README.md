# 🎓 My Blog - Проект для студентов

## 👋 Добро пожаловать!

Это учебный проект для практики работы со **Spring Framework**.

Вам предстоит **дописать недостающую функциональность** backend приложения-блога.

**Репозиторий:** https://github.com/Yandex-Practicum/mcrpo-my-blog-pre

---

## 📚 Сборка и запуск backend (Spring Boot)

### 0. Требования

- **Java 17+**
- **Maven 3.9+**

### 1. Клонируйте проект

```bash
git clone https://github.com/Yandex-Practicum/mcrpo-my-blog-pre.git
cd mcrpo-my-blog-pre
```

Убедитесь, что вы на ветке с итоговой работой (например, `feature/spring-boot-migration`).

### 2. Сборка backend

```bash
mvn clean package
```

В результате будет собран **исполняемый Jar**:

- `target/my-blog-back-app-1.0.0.jar`

### 3. Запуск тестов

```bash
mvn test
```

Тесты используют **Spring Boot Test** и поднимают встроенный контекст приложения и H2-базу.

### 4. Запуск приложения

```bash
java -jar target/my-blog-back-app-1.0.0.jar
```

По умолчанию приложение стартует во **встроенном Tomcat** на:

- `http://localhost:8080`

Параметры подключения к H2 находятся в `src/main/resources/application.properties`. Структура БД создаётся автоматически из `schema.sql` при старте.

### 5. Основные endpoints backend

Базовый URL: `http://localhost:8080`

- **Посты**
  - `GET /posts?search=&pageNumber=1&pageSize=10` — список постов с пагинацией и поиском.
  - `GET /posts/{id}` — получить пост по id.
  - `POST /posts` — создать пост.
  - `PUT /posts/{id}` — обновить пост.
  - `DELETE /posts/{id}` — удалить пост (каскадно удаляются комментарии, теги-связи и изображение).
  - `POST /posts/{id}/likes` — поставить лайк посту.
  - `DELETE /posts/{id}/likes` — убрать лайк поста.
  - `PUT /posts/{id}/image` — загрузить картинку для поста (multipart/form-data).
  - `GET /posts/{id}/image` — получить картинку поста.

- **Комментарии**
  - `GET /comments?postId={postId}` — список комментариев к посту.
  - `POST /comments` — создать комментарий.
  - `PUT /comments/{id}` — обновить комментарий.
  - `DELETE /comments/{id}` — удалить комментарий.

---

## 📁 Структура проекта

```
mcrpo-my-blog-pre/
├── src/main/java/com/myblog/
│   ├── config/          # Spring конфигурация
│   ├── controller/      # REST контроллеры (TODO здесь!)
│   ├── service/         # Бизнес-логика (TODO здесь!)
│   ├── dao/             # Работа с БД (TODO здесь!)
│   ├── model/           # Модели данных
│   └── dto/             # Data Transfer Objects
├── src/test/           # Тесты (TODO: дописать)
├── frontend/           # Frontend (React + Vite)
├── pom.xml             # Maven конфигурация
├── ЗАДАНИЕ_ДЛЯ_СТУДЕНТОВ.md  # 📖 НАЧНИТЕ ОТСЮДА!
├── TODO_TASKS.md       # Краткий чек-лист
└── README_СТУДЕНТЫ.md  # Техническая документация
```

---

## 🎯 Что нужно сделать

| Задание | Баллы |
|---------|-------|
| 1. Добавление и удаление лайков | 10 |
| 2. Редактирование и удаление комментариев | 10 |
| 3. Удаление поста (каскадное) | 5 |
| 4. Unit-тесты (БОНУС) | +15 |
| **ИТОГО** | **25-40** |

---

## 🚀 Быстрый старт

```bash
# 1. Клонировать проект
git clone https://github.com/Yandex-Practicum/mcrpo-my-blog-pre.git
cd mcrpo-my-blog-pre

# 2. Собрать backend
mvn clean install

# 3. Запустить backend (Jetty)
mvn jetty:run -Dmaven.test.skip=true

# Backend запустится на http://localhost:8080

# 4. Запустить frontend (в другом терминале, опционально)
# ⚠️ Требуется Node.js 18+ и npm
# Установка: brew install node (macOS) или https://nodejs.org

cd frontend
npm install
npm run dev

# Frontend запустится на http://localhost:3000
# Или тестируйте backend через curl/Postman
```

---

## 🛠 Технологии

- Java 17
- Spring Framework 6.1+
- Maven
- H2 Database
- JUnit 5 + Mockito
- Tomcat 11

---

## 📖 Документация

- **[ЗАДАНИЕ_ДЛЯ_СТУДЕНТОВ.md](./ЗАДАНИЕ_ДЛЯ_СТУДЕНТОВ.md)** — полное описание задания
- **[TODO_TASKS.md](./TODO_TASKS.md)** — краткий чек-лист
- **[README_СТУДЕНТЫ.md](./README_СТУДЕНТЫ.md)** — техническая документация

---

## ❓ Часто задаваемые вопросы

### Как найти все TODO?

```bash
grep -rn "TODO:" src/
```

### Как запустить только тесты?

```bash
mvn test
```

### Как проверить endpoint через curl?

```bash
curl http://localhost:8080/api/posts/1/likes -X POST
```

### Что делать, если застрял?

1. Прочитайте существующий код — все TODO методы похожи на уже реализованные
2. Читайте логи Tomcat — ошибки видны в консоли
3. Смотрите БД через H2 консоль: http://localhost:8080/h2-console

---

## 🎓 Успехов!

**Помните:** Цель не просто сделать, а **научиться**!

Изучайте существующий код, экспериментируйте, задавайте вопросы.

---

**Удачи! 🚀**
