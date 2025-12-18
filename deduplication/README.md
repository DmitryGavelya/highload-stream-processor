# Deduplication Service

Сервис дедупликации сообщений для системы потоковой обработки данных.

## Описание

Сервис принимает отфильтрованные сообщения из Kafka топика `filtered` и проверяет, не приходило ли такое же сообщение за последний заданный промежуток времени. Если сообщение уникальное, оно отправляется в топик `deduplicated`. Если сообщение является дубликатом, оно игнорируется.

## Особенности

- **Хранение хешей в Redis**: Для каждого сообщения вычисляется SHA-256 хеш, который сохраняется в Redis с TTL
- **Настройка окна времени**: Для каждого пользователя можно настроить временное окно дедупликации (по умолчанию 300 секунд)
- **Исключение полей**: Можно указать поля, которые нужно игнорировать при вычислении хеша (например, timestamp, requestId)
- **Конфигурация в PostgreSQL**: Правила дедупликации хранятся в базе данных

## Схема базы данных

```sql
CREATE TABLE deduplication_config (
    user_id VARCHAR(255) PRIMARY KEY,
    time_window_seconds INTEGER NOT NULL DEFAULT 300,
    excluded_fields TEXT  -- JSON массив, например: ["timestamp", "requestId"]
);
```

## Конфигурация

Настройки в `application.yaml`:

```yaml
deduplication:
  listen-to: filtered      # Топик, из которого читаем
  send-to: deduplicated    # Топик, в который отправляем уникальные сообщения

spring:
  data:
    redis:
      host: localhost
      port: 6379
```

## Пример работы

### Конфигурация пользователя

```json
{
  "userId": "user1",
  "timeWindowSeconds": 600,
  "excludedFields": "[\"timestamp\", \"requestId\"]"
}
```

### Входящие сообщения

Сообщение 1 (время T):
```json
{
  "userId": "user1",
  "data": "important message",
  "timestamp": "2024-01-01T10:00:00",
  "requestId": "req-123"
}
```

Сообщение 2 (время T+30s):
```json
{
  "userId": "user1",
  "data": "important message",
  "timestamp": "2024-01-01T10:00:30",
  "requestId": "req-456"
}
```

**Результат**: Сообщение 2 будет определено как дубликат (так как `timestamp` и `requestId` исключены из сравнения) и проигнорировано.

## Запуск

### Локально

```bash
cd deduplication
./mvnw spring-boot:run
```

### Docker Compose

```bash
docker-compose up --build
```

## Тестирование

```bash
cd deduplication
./mvnw test
```

## Архитектура

```
┌─────────────┐      ┌──────────────────┐      ┌─────────────┐
│   Filter    │─────>│  Deduplication   │─────>│ Enrichment  │
│   Service   │      │     Service      │      │   Service   │
└─────────────┘      └──────────────────┘      └─────────────┘
                              │
                              ├──> PostgreSQL (конфигурация)
                              └──> Redis (хранение хешей)
```

## Алгоритм дедупликации

1. Получить сообщение из топика `filtered`
2. Извлечь `userId` из сообщения
3. Загрузить конфигурацию дедупликации для пользователя из PostgreSQL
4. Отфильтровать сообщение, исключив указанные поля
5. Вычислить SHA-256 хеш от отфильтрованного сообщения
6. Проверить наличие хеша в Redis с ключом `dedup:{userId}:{hash}`
7. Если хеш найден - сообщение является дубликатом, игнорируем
8. Если хеш не найден - сохраняем хеш в Redis с TTL = timeWindowSeconds и отправляем сообщение в топик `deduplicated`

## Технологии

- Java 21
- Spring Boot 3.5.6
- Spring Kafka
- Spring Data JPA
- Spring Data Redis
- PostgreSQL
- Redis
- Flyway (миграции БД)
- Testcontainers (тестирование)

