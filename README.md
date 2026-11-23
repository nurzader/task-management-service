# Task Management Service

REST API для управления задачами. Java 21, Spring Boot, H2, Redis.

## Выполнено

**Основное**
- [x] CRUD REST API для задач
- [x] H2 (in-memory) + Spring Data JPA
- [x] Логирование запросов и ответов (`HttpLoggingFilter`)

**Дополнительное**
- [x] GET на `https://api.restful-api.dev/objects` при старте, ответ логируется
- [x] Unit-тесты CRUD-методов сервиса (JUnit 5 + Mockito)
- [x] Email при создании задачи (Spring Mail)
- [x] Basic Authentication
- [x] Кэш списка задач в Redis
- [x] Dockerfile + docker-compose

## Запуск

```bash
docker compose up --build
```

Сервис: http://localhost:8080
Тесты: `./mvnw test`

## Настройки

Для отправки писем задайте `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_TO`.

| Переменная | По умолчанию | Описание |
|---|---|---|
| `APP_USER` / `APP_PASSWORD` | `admin` / `admin123` | Basic Auth |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | пусто | SMTP (для Gmail нужен пароль приложения) |
| `MAIL_TO` | пусто | Получатель писем. Если пусто, письма не отправляются |

## API

Все запросы с Basic Auth.

| Метод | URL | Описание |
|---|---|---|
| POST | `/api/tasks` | Создать |
| GET | `/api/tasks` | Список (кэшируется) |
| GET | `/api/tasks/{id}` | По ID |
| PUT | `/api/tasks/{id}` | Обновить |
| DELETE | `/api/tasks/{id}` | Удалить |

```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Первая задача","description":"описание"}'
```

H2 Console: `/h2-console` (JDBC URL `jdbc:h2:mem:taskdb`, user `sa`, пароль пустой)
