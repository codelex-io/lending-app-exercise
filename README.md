# Lending Application

Your goal is to create an lending application.

This project contains tests which can be executed against your locally started application.

## Business Requirements

  - Customer can create an account and sign in with his email and password
  - Customer can apply for loan passing amount and term within the given constraints
  - Application may be rejected by risk assessment if:
    - the attempt to take loan is made after 00:00 with max possible amount
    - reached max applications (e.g. 3) per day from a single IP
  - Loan can be extended, interest factor per week is 1.5
  - Customer can view his loans, including extensions

## API Documentation

### Test API

**Reset time**

*Request:*

```POST http://localhost:8080/testing-api/reset-time```

*Response:*

Status code: ```200 OK```

**Set time**

*Request:*

```PUT http://localhost:8080/testing-api/time```

Request body: ```json "2019-04-19 14:47"```

*Response:*

Status code: ```200 OK```

**Get time**

*Request:*

```GET http://localhost:8080/testing-api/time```

*Response:*

Status code: ```200 OK```

Response body: ```json "2019-04-19 14:47"```

**Clear database**

*Request:*

```POST http://localhost:8080/testing-api/clear-database```

*Response:*

Status code: ```200 OK```

### Customer API

**Fetch product constraints**

*Request:*

```GET http://localhost:8080/api/constraints```

*Response:*

Status code: ```200 OK```

Response body:
```json
{
    "minAmount": 100,
    "maxAmount": 500,
    "minTermDays": 7,
    "maxTermDays": 30,
    "minExtensionDays": 7,
    "maxExtensionDays": 30
}
```

**Register**

*Request:*

```POST http://localhost:8080/api/register```

Request body:
```json
{
  "email": "example@codelex.io",
  "password": "Password123"
}
```

*Response:*

Status code: ```200 OK```

**Sign In**

*Request:*

```POST http://localhost:8080/api/sign-in```

Request body:
```json
{
  "email": "example@codelex.io",
  "password": "Password123"
}
```

*Response:*

Status code: ```200 OK```

**Sign Out**

*Request:*

```POST http://localhost:8080/api/sign-out```

*Response:*

Status code: ```200 OK```

**Fetch loans**

*Request:*

```GET http://localhost:8080/api/loans```

*Response:*

Status code: ```200 OK```

Response body:
```json
[
    {
        "id": "2755-7865",
        "status": "OPEN",
        "created": "2019-04-19",
        "dueDate": "2019-05-10",
        "principal": 500,
        "interest": 36.17,
        "total": 536.17,
        "extensions": [
            {
                "created": "2019-04-19",
                "days": 7,
                "interest": 12.84
            }
        ]
    }
]
```

**Apply for loan**

*Request:*

```POST http://localhost:8080/api/loans/apply```

Request body:
```json
{
  "amount": 500.0,
  "days": 14
}
```

*Response:*

Status code: ```200 OK```

Response body:
```json
{
    "status": "APPROVED"
}
```

**Extend loan**

*Request:*

```POST http://localhost:8080/api/loans/{loan-id}/extend```

Where query parameters are:

  - *days* - amount of days

*Response:*

Status code: ```200 OK```

Response body:
```json
{
    "id": "2755-7865",
    "status": "OPEN",
    "created": "2019-04-19",
    "dueDate": "2019-05-10",
    "principal": 500,
    "interest": 36.17,
    "total": 536.17,
    "extensions": [
        {
            "created": "2019-04-19",
            "days": 7,
            "interest": 12.84
        }
    ]
}
```

## IP Address handling

For the sake of exercise ip address is passed as header with name *X-FORWARDED-FOR*

## Authorization

Customer is authorized with *JSESSIONID* passed in cookies with each request 

## Useful Resources

- [Building a RESTful Web Service @spring.io](http://spring.io/guides/gs/rest-service)
- [Building Java Projects with Gradle @spring.io](http://spring.io/guides/gs/gradle/)
- [Simple example of spring-security](https://github.com/codelex-io/spring-security-simple-example)
- [Spring security architecture](https://spring.io/guides/topicals/spring-security-architecture/)