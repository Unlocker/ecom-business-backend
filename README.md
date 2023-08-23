# ecom-business-backend

## API для пользователей

### Авторизация

Запросы авторизуются с использованием JWT, переданного в заголовке "Authorization: Bearer JWT_VALUE". Заголовок "Content-Type" равен "application/json".

POST /api/v1/user/sign-in

Request {"login": "string", "password": "string"} 

Response {"access_token": "string", "expires_in": unixtime}

### Регистрация

POST /api/v1/user/sign-up

Request {
  "login": "string", 
  "password": "string", 
  "passwordRepeat": "string", 
  "name": "string"
} 

Response {"access_token": {token:string}, "expires_in": {expires:unixtime}}

### Выход

[Auth] GET /api/v1/user/sign-off

Response code 204 (No Content)

## API для банка Точка

В настройках сервиса нужно будет сохранить идентификатор нашего сервиса для банковской авторизации и секрет сервиса.

В таблице настройки интеграции с банком нужно сохранить токен доступа (access), токен обновления (refresh), дата и время истечения токена доступа (expires).

### Авторизация на стороне банка

Если пользователь имеет действующий токен, то tokenReceived=true. Если необходимо получить новый токен, то формируется ссылка redirectUrl для перенаправления пользователя.

[Auth] GET /api/v1/bank/TOCHKA/authorize

Response {"tokenReceived": boolean, "redirectUrl": "url_string"}

### Приём колбека от банка

Используется code для получения и сохранения токена.

[Auth] GET /api/v1/bank/TOCHKA/accept-oauth?code={code:string}&state={state:string}

Response 200 {}

### Запрос баланса

[Auth] GET /api/v1/bank/TOCHKA/balance

[
  {
    "accountId": "40817810802000000008/044525104",
    "bank": "TOCHKA",
    "amount": {
      "amount": 13600,
      "currency": "RUB"
    }
  }
]

### Запрос истории операций по счёту

[Auth] GET /api/v1/bank/TOCHKA/balance/{accountId}/statement/{startDate}/{endDate}

* accountId -- идентификатор счёта
* startDate -- начальная дата периода, ISO 8601
* endDate -- завершающая дата периода, ISO 8601

[
  {
    "statementDate": "2023-08-22",
    "counterParty": {
      "name": "ООО Ромашка",
      "taxId": "1234567890"
    },
    "direction": "INCOME" // "OUTCOME",
    "accountId": "40817810802000000008/044525104",
    "bank": "TOCHKA",
    "amount": {
      "amount": 13600,
      "currency": "RUB"
    },
    "statementId": "ABCD21345"
  }
]
