# Deduplication Service - Итоговая реализация

## Что было реализовано

### 1. Структура проекта
Создан полноценный Spring Boot микросервис со следующей структурой:

```
deduplication/
├── src/
│   ├── main/
│   │   ├── java/org/hsse/highloadstreamprocessor/deduplication/
│   │   │   ├── DeduplicationServiceApplication.java    # Главный класс приложения
│   │   │   ├── FilteredConsumer.java                    # Kafka consumer для топика filtered
│   │   │   ├── DeduplicatedSender.java                  # Kafka producer для топика deduplicated
│   │   │   ├── DeduplicationService.java                # Основная бизнес-логика
│   │   │   ├── config/
│   │   │   │   ├── TopicConfig.java                     # Конфигурация Kafka топиков
│   │   │   │   └── RedisConfig.java                     # Конфигурация Redis
│   │   │   └── db/
│   │   │       ├── DeduplicationConfigEntity.java       # JPA entity для конфигурации
│   │   │       └── DeduplicationConfigRepository.java   # Spring Data JPA репозиторий
│   │   └── resources/
│   │       ├── application.yaml                          # Конфигурация приложения
│   │       └── db/migration/
│   │           └── V1__create_deduplication_config_table.sql  # Flyway миграция
│   └── test/
│       └── java/org/hsse/highloadstreamprocessor/deduplication/
│           └── DeduplicationServiceTest.java             # Интеграционные тесты
├── pom.xml                                               # Maven конфигурация
├── Dockerfile                                            # Docker образ
├── README.md                                             # Документация сервиса
└── API_EXAMPLES.md                                       # Примеры использования API
```

### 2. Основные компоненты

#### DeduplicationService
Центральный компонент, реализующий логику дедупликации:
- Вычисление SHA-256 хеша сообщения
- Проверка наличия хеша в Redis
- Сохранение новых хешей с TTL
- Фильтрация полей по конфигурации из БД
- Поддержка исключения полей из дедупликации

#### FilteredConsumer
Kafka consumer, который:
- Слушает топик `filtered`
- Парсит JSON сообщения
- Проверяет на дубликаты через DeduplicationService
- Отправляет уникальные сообщения через DeduplicatedSender

#### DeduplicatedSender
Kafka producer для отправки уникальных сообщений в топик `deduplicated`

### 3. База данных PostgreSQL

Таблица `deduplication_config`:
- `user_id` - идентификатор пользователя (PRIMARY KEY)
- `time_window_seconds` - временное окно дедупликации в секундах (по умолчанию 300)
- `excluded_fields` - JSON массив с полями для исключения из сравнения

### 4. Redis
Используется для хранения хешей сообщений:
- Ключ: `dedup:{userId}:{hash}`
- Значение: "1"
- TTL: `time_window_seconds` из конфигурации

### 5. Kafka топики
- **Входной**: `filtered` (от Filter Service)
- **Выходной**: `deduplicated` (для Enrichment Service)

### 6. Docker Compose
Конфигурация включает:
- Deduplication Service
- PostgreSQL 17.6
- Redis 7.2
- Kafka + Zookeeper

### 7. Тесты
Интеграционные тесты с использованием Testcontainers:
- Проверка первого уникального сообщения
- Проверка дубликата
- Проверка разных сообщений
- Проверка исключения полей из дедупликации

## Алгоритм работы

1. Сообщение приходит в топик `filtered`
2. FilteredConsumer парсит JSON
3. Извлекается `userId` из сообщения
4. Загружается конфигурация из PostgreSQL для данного пользователя
5. Сообщение фильтруется (исключаются поля из `excluded_fields`)
6. Вычисляется SHA-256 хеш отфильтрованного сообщения
7. Проверяется наличие хеша в Redis
8. Если хеш есть → сообщение игнорируется (дубликат)
9. Если хеша нет → хеш сохраняется в Redis с TTL и сообщение отправляется в `deduplicated`

## Настройка через edit-config-user-service

Пользователь может управлять полями для исключения через REST API:

```bash
# Добавить поля для исключения
POST /api/deduplication/{userId}/columns/batch
{
  "columns": ["timestamp", "requestId"]
}

# Получить список исключенных полей
GET /api/deduplication/{userId}/columns
```

Эти данные сохраняются в таблице `user_deduplication` и преобразуются в JSON массив для таблицы `deduplication_config`.

## Преимущества реализации

1. **Масштабируемость**: Redis обеспечивает быстрый доступ к хешам
2. **Гибкость**: Настраиваемые временные окна и исключаемые поля
3. **Консистентность**: SHA-256 хеширование с сортировкой ключей
4. **Отказоустойчивость**: Использование TTL в Redis для автоматической очистки
5. **Наблюдаемость**: Логирование всех операций через Slf4j
6. **Тестируемость**: Интеграционные тесты с Testcontainers

## Соответствие требованиям

✅ Принимает данные от сервиса фильтрации (топик `filtered`)
✅ Игнорирует дубликаты за заданный промежуток времени (TTL в Redis)
✅ Использует Redis для хранения недавних сообщений (хеши с TTL)
✅ Исключает поля из дедупликации (конфигурируемо через БД)
✅ Структура проекта соответствует другим сервисам (filter-service)
✅ Использует те же технологии: Spring Boot, Kafka, PostgreSQL, Flyway
✅ Docker Compose для развертывания

## Как запустить

```bash
cd deduplication
docker-compose up --build
```

Сервис будет доступен и готов к обработке сообщений из топика `filtered`.

## Дальнейшее развитие

Возможные улучшения:
1. Добавить метрики (Prometheus/Micrometer)
2. Добавить health checks
3. Реализовать fallback стратегии при недоступности Redis
4. Добавить поддержку разных алгоритмов хеширования
5. Реализовать sliding window вместо fixed window
6. Добавить UI для мониторинга дедупликации

