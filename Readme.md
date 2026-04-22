# Клиентская часть задания

## Описание задания

1. Ваш проект должен состоять из двух частей - сервер и клиент (оба на языке Java)
   Все клиенты при запуске должны подключаться к единому серверу
2. По внешнему виду клиент должен как можно точнее повторять Telegram (темную тему)
3. Логика обмена сообщениями тоже должна быть реализована максимально похоже, но в
   объеме MVP
4. Не требуется шифрование, изменение или удаление уже отправленных сообщений,
   оповещение о прочтении, сохранение сообщений на сервере, передача чего-либо помимо
   текста, создание групповых чатов и т.д.
5. Авторизация предполагает ввод имени и пароля в окошке входа при каждом запуске
   приложения
6. Функция регистрации не обязательна, достаточно списка предопределенных тестовых
   пользователей
7. Для реализации графического интерфейса используйте библиотеки Java AWT/Swing и
   https://www.formdev.com/flatlaf
8. Сервер должен обеспечивать весь необходимый функционал передачи сообщений
   между несколькими клиентами и авторизации пользователей


# Telegram Desktop Chat

Мессенджер на Java с использованием WebSocket для обмена сообщениями в реальном времени.

## Требования

| Компонент | Версия | Примечание |
|-----------|--------|-----------|
| Java | 17 или выше | Требуется для записей (records) и switch-выражений |
| Maven | 3.8+ | Для сборки зависимостей |
| Spring Boot | 2.7+ | Серверная часть |

## Быстрый старт

### Сборка проекта

```bash
# Сборка сервера
cd server
mvn clean package

# Сборка клиента (в отдельном терминале)
cd ../client
mvn clean package
```

### Запуск сервера

```bash
cd server
mvn spring-boot:run
```


### Запуск клиента

```bash
cd client
mvn exec:java -Dexec.mainClass="ru.moskalev.client.Application"
```

Или через IDE:
1. Откройте проект в IntelliJ IDEA
2. Найдите класс `ru.moskalev.client.ClientApp`
3. Запустите метод `main()`

### Проверка работы

1. Откроется окно авторизации
2. Введите тестовые данные (см. раздел "Тестовые учётные данные")
3. После успешного входа отобразится список контактов
4. Выберите контакт и начните переписку

## Конфигурация сервера

Файл: `server/src/main/resources/application.properties`

```properties
# Порт HTTP/WebSocket
server.port=8080

# Логирование
logging.level.ru.moskalev=INFO
logging.level.org.springframework.web.socket=INFO
logging.pattern.console=%d{HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# Отключение автоконфигурации DataSource (данные хранятся в памяти)
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

### Изменение порта

Если порт 8080 занят:
```properties
server.port=8081
```

Не забудьте обновить URL подключения в клиенте: `ws://localhost:8081/ws`

## Конфигурация клиента

Файл: `client/src/main/resources/logging.properties`


### Изменение уровня логирования

Для отладки установите уровень `FINE`:
```properties
ru.moskalev.client.level=FINE
```

Доступные уровни:
| Уровень | Метод | Описание |
|---------|-------|----------|
| `SEVERE` | `logger.severe()` | Критические ошибки |
| `WARNING` | `logger.warning()` | Предупреждения |
| `INFO` | `logger.info()` | Основные события (по умолчанию) |
| `FINE` | `logger.fine()` | Детальная отладка |

### Иконки

Убедитесь, что файлы иконок присутствуют:
```
client/src/main/resources/icons/
├── send-icon.png      # 40×40 px или больше (масштабируется до 20×20)
└── telegram-logo.png  # 280×280 px или больше (масштабируется до 140×140)
```

## Протокол обмена сообщениями

Все сообщения передаются в формате JSON с обязательными полями `type` и `payload`.

### Авторизация

Запрос:
```json
{
  "type": "AUTH",
  "payload": {
    "login": "1",
    "password": "1"
  }
}
```

Успешный ответ:
```json
{
  "type": "AUTH_SUCCESS",
  "payload": {
    "displayName": "Василий"
  }
}
```

Ошибка:
```json
{
  "type": "AUTH_ERROR",
  "payload": {
    "code": "INVALID_PASSWORD",
    "message": "Неверный пароль"
  }
}
```

### Получение контактов

Запрос:
```json
{ "type": "GET_CONTACTS" }
```

Ответ:
```json
{
  "type": "CONTACTS_LIST",
  "payload": {
    "contacts": [
      {
        "login": "anna",
        "displayName": "Анна",
        "online": true
      }
    ]
  }
}
```

### Отправка сообщения

Запрос:
```json
{
  "type": "SEND_MESSAGE",
  "payload": {
    "to": "anna",
    "text": "Привет!"
  }
}
```

Подтверждение отправителю:
```json
{
  "type": "MESSAGE_ACK",
  "payload": {
    "from": "1",
    "to": "anna",
    "text": "Привет!",
    "timestamp": 1713780000000
  }
}
```

Доставка получателю:
```json
{
  "type": "NEW_MESSAGE",
  "payload": {
    "from": "1",
    "to": "anna",
    "text": "Привет!",
    "timestamp": 1713780000000
  }
}
```

### Запрос истории

Запрос:
```json
{
  "type": "GET_HISTORY",
  "payload": {
    "targetLogin": "anna"
  }
}
```

Ответ:
```json
{
  "type": "HISTORY",
  "payload": {
    "targetLogin": "anna",
    "messages": [
      {
        "from": "1",
        "to": "anna",
        "text": "Привет!",
        "timestamp": 1713780000000
      }
    ]
  }
}
```

### Редактирование сообщения

Запрос:
```json
{
  "type": "EDIT_MESSAGE",
  "payload": {
    "to": "anna",
    "originalText": "Привет!",
    "newText": "Привет, как дела?"
  }
}
```

Уведомление:
```json
{
  "type": "MESSAGE_UPDATED",
  "payload": {
    "originalText": "Привет!",
    "newText": "Привет, как дела?"
  }
}
```

### Удаление сообщения

Запрос:
```json
{
  "type": "DELETE_MESSAGE",
  "payload": {
    "to": "anna",
    "text": "Привет!"
  }
}
```

Уведомление:
```json
{
  "type": "MESSAGE_DELETED",
  "payload": {
    "text": "Привет!"
  }
}
```

### Служебные сообщения

Ping/Pong:
```json
{ "type": "PING" }  ->  { "type": "PONG" }
```

Ошибка сервера:
```json
{
  "type": "ERROR",
  "payload": {
    "code": "UNKNOWN_TYPE",
    "message": "Неизвестный тип: CUSTOM"
  }
}
```

## Тестовые учётные данные

| Логин | Пароль | Имя | Описание |
|-------|--------|-----|----------|
| `1` | `1` | Василий | Основной тестовый пользователь |
| `admin` | `admin` | Василий | Администратор |
| `anna` | `pass456` | Анна | Для тестирования диалогов |
| `ivan` | `qwerty` | Иван | Для тестирования диалогов |
| `maria` | `demo789` | Мария | Для тестирования диалогов |

При старте сервера автоматически создаются тестовые диалоги между этими пользователями.

## Логирование

### Сервер (SLF4J + Logback)

Логи выводятся в консоль в формате:
```
03:30:05 [ws-worker-1] INFO  r.m.s.h.ChatWebSocketHandler - Message sent: 1 -> anna
```

Изменение уровня:
```properties
# application.properties
logging.level.ru.moskalev=DEBUG
```

### Клиент (java.util.logging)

Логи выводятся в консоль в формате:
```
03:30:05 INFO ru.moskalev.client.network.MessengerWebSocketClient User authenticated: Василий
```

Изменение уровня:
```properties
# logging.properties
ru.moskalev.client.level=FINE
```

## Устранение неполадок

### Сервер не запускается

| Ошибка | Причина | Решение |
|--------|---------|---------|
| `Port 8080 was already in use` | Порт занят другим процессом | Измените `server.port` в `application.properties` или завершите конфликтующий процесс |
| `Failed to configure a DataSource` | Spring ищет базу данных | Добавьте `spring.autoconfigure.exclude=...DataSourceAutoConfiguration` |
| `ClassNotFoundException` | Отсутствует зависимость | Выполните `mvn clean install` |

### Клиент не подключается

| Ошибка | Причина | Решение |
|--------|---------|---------|
| `Connection refused` | Сервер не запущен | Запустите сервер перед клиентом |
| `Failed to send message of type: GET_CONTACTS` | Запрос до авторизации | Убедитесь, что `requestContacts()` вызывается после `AUTH_SUCCESS` |
| `Icon not found` | Отсутствует файл иконки | Проверьте путь: `src/main/resources/icons/` |

### Сообщения не отображаются

| Симптом | Проверка |
|---------|----------|
| Контакты не загружаются | В логах клиента: `Contacts request sent` -> `CONTACTS_LIST received` |
| История не грузится | Убедитесь, что выбран контакт перед запросом истории |
| Сообщения дублируются | Проверьте, что сервер не отправляет эхо отправителю |

### Включение отладочного логирования

Сервер:
```properties
# application.properties
logging.level.ru.moskalev=DEBUG
logging.level.org.springframework.web.socket=DEBUG
```

Клиент:
```properties
# logging.properties
ru.moskalev.client.level=FINE
```

## Разработка

### Добавление нового типа сообщения

1. Сервер: Обработайте тип в `ChatWebSocketHandler.handleTextMessage()`
2. Сервер: Добавьте метод-обработчик и логику в сервисе
3. Клиент: Добавьте обработку в `MessengerWebSocketClient.onMessage()`
4. Документация: Обновите раздел "Протокол обмена сообщениями"


### Сборка исполняемых JAR

```bash
# Сервер
cd server
mvn clean package
java -jar target/messenger-server.jar

# Клиент
cd client
mvn clean package
# Запуск через IDE или настройку exec-maven-plugin
```

## Лицензия

Проект предназначен для учебных целей.
