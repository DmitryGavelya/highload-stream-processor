# Highload Stream Processor

Проект в рамках предмета проектный практикум МФТИ.

Инструмент потоковой обработки данных для высоконагруженных систем

Состав команды: 
- Чукалов Алексей
- Гавеля Дмитрий
- Жучков Павел

## Архитектура системы

Система состоит из трех микросервисов для обработки потоковых данных:

```
┌────────────┐      ┌──────────────────┐      ┌─────────────┐
│  Filter    │─────>│  Deduplication   │─────>│ Enrichment  │
│  Service   │      │     Service      │      │   Service   │
└────────────┘      └──────────────────┘      └─────────────┘
     │                       │                       │
     ├─> PostgreSQL          ├─> PostgreSQL          └─> PostgreSQL
     │   (фильтры)           ├─> Redis
                             │   (хеши сообщений)
```

### Сервисы

#### 1. Filter Service
Принимает JSON сообщения из Kafka топика `source` и фильтрует их на основе правил, хранящихся в PostgreSQL.

**Топики:**
- Входной: `source`
- Выходной: `filtered`

#### 2. Deduplication Service (✨ Новый)
Принимает отфильтрованные сообщения из топика `filtered` и проверяет, не приходило ли такое же сообщение за заданный промежуток времени.

**Особенности:**
- Использует Redis для хранения хешей сообщений
- Настраиваемое временное окно дедупликации
- Возможность исключения полей из сравнения (например, timestamp, requestId)
- SHA-256 хеширование для определения дубликатов

**Топики:**
- Входной: `filtered`
- Выходной: `deduplicated`

**База данных:** PostgreSQL (конфигурация), Redis (хранение хешей)

#### 3. Enrichment Service
Обогащает данные дополнительной информацией.

**Топики:**
- Входной: `deduplicated`
- Выходной: `enriched`

### Edit Config User Service
Отдельный REST API сервис для управления правилами обработки всех трех сервисов.

**Порт:** 8080

## Быстрый старт

### Запуск Deduplication Service

```bash
cd deduplication
docker-compose up --build
```

Сервис будет:
- Слушать топик `filtered` в Kafka
- Использовать PostgreSQL для конфигурации
- Использовать Redis для хранения хешей
- Отправлять уникальные сообщения в топик `deduplicated`

### Настройка правил дедупликации

Используйте Edit Config User Service для управления правилами:

```bash
# Добавить поля для исключения из дедупликации
curl -X POST http://localhost:8080/api/deduplication/user1/columns/batch \
  -H "Content-Type: application/json" \
  -d '{
    "columns": ["timestamp", "requestId"]
  }'

# Получить список исключенных полей
curl http://localhost:8080/api/deduplication/user1/columns
```

Подробнее см. `deduplication/API_EXAMPLES.md`

## Разработка

### Требования
- Java 21
- Maven
- Docker & Docker Compose (для запуска инфраструктуры)

### Сборка

```bash
cd deduplication
./mvnw clean install
```

### Тесты

```bash
cd deduplication
./mvnw test
```

Тесты используют Testcontainers и требуют запущенный Docker.

## Технологии

- **Java 21**
- **Spring Boot 3.5.6**
- **Spring Kafka** - интеграция с Apache Kafka
- **Spring Data JPA** - работа с PostgreSQL
- **Spring Data Redis** - работа с Redis
- **PostgreSQL** - хранение конфигураций
- **Redis** - хранение хешей сообщений
- **Flyway** - миграции базы данных
- **Testcontainers** - интеграционное тестирование
- **Lombok** - упрощение кода

## Документация

- [Deduplication Service README](deduplication/README.md) - подробное описание сервиса дедупликации
- [API Examples](deduplication/API_EXAMPLES.md) - примеры использования API

## Лицензия

Учебный проект МФТИ

