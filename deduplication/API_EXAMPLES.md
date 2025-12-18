# Примеры использования API дедупликации

## Управление конфигурацией через edit-config-user-service

Конфигурация дедупликации управляется через отдельный сервис `edit-config-user-service`.

### Добавить поле для исключения из дедупликации

```bash
POST /api/deduplication/{userId}/columns/{columnName}
```

Пример:
```bash
curl -X POST http://localhost:8080/api/deduplication/user1/columns/timestamp
```

### Добавить несколько полей одновременно

```bash
POST /api/deduplication/{userId}/columns/batch
Content-Type: application/json

{
  "columns": ["timestamp", "requestId", "traceId"]
}
```

Пример:
```bash
curl -X POST http://localhost:8080/api/deduplication/user1/columns/batch \
  -H "Content-Type: application/json" \
  -d '{
    "columns": ["timestamp", "requestId", "traceId"]
  }'
```

### Получить список исключенных полей

```bash
GET /api/deduplication/{userId}/columns
```

Пример:
```bash
curl http://localhost:8080/api/deduplication/user1/columns
```

Ответ:
```json
{
  "userId": "user1",
  "columns": ["timestamp", "requestId", "traceId"]
}
```

### Удалить поле из списка исключенных

```bash
DELETE /api/deduplication/{userId}/columns/{columnName}
```

Пример:
```bash
curl -X DELETE http://localhost:8080/api/deduplication/user1/columns/timestamp
```

### Очистить все исключенные поля

```bash
DELETE /api/deduplication/{userId}/columns
```

Пример:
```bash
curl -X DELETE http://localhost:8080/api/deduplication/user1/columns
```

### Проверить, исключено ли поле

```bash
GET /api/deduplication/{userId}/columns/{columnName}/exists
```

Пример:
```bash
curl http://localhost:8080/api/deduplication/user1/columns/timestamp/exists
```

Ответ:
```json
true
```

## Прямая работа с базой данных (для администраторов)

### Добавить конфигурацию дедупликации

```sql
INSERT INTO deduplication_config (user_id, time_window_seconds, excluded_fields)
VALUES ('user1', 600, '["timestamp", "requestId"]');
```

### Обновить временное окно

```sql
UPDATE deduplication_config
SET time_window_seconds = 900
WHERE user_id = 'user1';
```

### Обновить список исключенных полей

```sql
UPDATE deduplication_config
SET excluded_fields = '["timestamp", "requestId", "traceId"]'
WHERE user_id = 'user1';
```

### Получить конфигурацию

```sql
SELECT * FROM deduplication_config WHERE user_id = 'user1';
```

## Пример потока данных

### 1. Настройка конфигурации (через edit-config-user-service)

```bash
curl -X POST http://localhost:8080/api/deduplication/user1/columns/batch \
  -H "Content-Type: application/json" \
  -d '{
    "columns": ["timestamp", "requestId"]
  }'
```

### 2. Сообщения поступают в топик `filtered`

Сообщение 1:
```json
{
  "userId": "user1",
  "orderId": "order-123",
  "amount": 100.50,
  "timestamp": "2024-01-01T10:00:00",
  "requestId": "req-001"
}
```

Сообщение 2 (через 10 секунд):
```json
{
  "userId": "user1",
  "orderId": "order-123",
  "amount": 100.50,
  "timestamp": "2024-01-01T10:00:10",
  "requestId": "req-002"
}
```

### 3. Обработка в deduplication-service

- Сообщение 1: хеш вычисляется без полей `timestamp` и `requestId`
  - Хеш от: `{"userId": "user1", "orderId": "order-123", "amount": 100.50}`
  - Хеш не найден в Redis → сохраняем хеш → отправляем в `deduplicated`

- Сообщение 2: хеш вычисляется без полей `timestamp` и `requestId`
  - Хеш от: `{"userId": "user1", "orderId": "order-123", "amount": 100.50}`
  - Хеш найден в Redis → **игнорируем сообщение**

### 4. Результат

В топик `deduplicated` попадет только первое сообщение.

## Мониторинг Redis

### Просмотр сохраненных хешей

```bash
redis-cli KEYS "dedup:*"
```

### Проверка TTL для хеша

```bash
redis-cli TTL "dedup:user1:abc123..."
```

### Очистка всех хешей пользователя

```bash
redis-cli KEYS "dedup:user1:*" | xargs redis-cli DEL
```

