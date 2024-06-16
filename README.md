# Vocabulary-API

## Content

1. [Pre-requirement](#pre-requirement)
2. [Building and Running](#building-and-running)
    - Building
        - [building jar](#building-jar)
    - Running
      - [running with java](#with-java)
      - [running with docker](#with-docker)
      - [running with docker compose](#with-docker-compose)
3. [Description](#description)
    - [Dependencies](#dependencies)
    - [Data Modeling](#data-modeling)
    - [Rest API endpoints](#rest-api-endpoints)

## Pre-requirement

- [MailHog](https://github.com/mailhog/MailHog) for mail server.
- [Postgresql](https://www.postgresql.org/) for database.
- [Docker](https://www.docker.com/) for running as container. ***(optional)***

***if you don't want to install the requirement please [run with docker-compose](#with-docker-compose)***

## Building and Running

### Building Jar

- open terminal or cmd or powershell
- ```shell
  cd path_to/vocabulary
- ```shell
  mvn package

### Running

#### With Java

- ```shell
    java -jar ./target/vocabulary.jar
- go to [http://localhost:8080/api/vocabularies](http://localhost:8080/api/vocabularies)


#### With Docker

- ``` shell
    docker build . -t vocabulary:snapshot-0.0.1

- Run with default value
   ```shell
   docker run --name vocabulary -d -p 8080:8080
  ```
   Custom configuration
   ```shell
   docker run --name vocabulary -d -p 8080:8080`
   -e DATASOURCE_URL={ipv4 address of host computer}:5432/{database schema} `
   -e DATASOURCE_PASSWORD={database password} `
   -e DATASOURCE_USERNAME={database username} `
   -e MAIL_HOST={ipv4 address of host computer} `
   -e MAIL_PORT={port of mailhog server} `
   -e DEFAULT_PAGESIZE={page size} `
   -e ALLOWED_METHODS={allowed crossorigin methods} `
   -e ALLOWED_HEADERS={allowed crossorigin headers} `
   -e ALLOWED_ORIGINS={allowed crossorigin url} `
   -e RESET_CODE_DURATIONI={email reset code duration} `
   -e ADMIN_EMAIL={admin email}
   -e ADMIN_USERNAME={admin username}
   -e ADMIN_PASSWORD={admin password}
   -e ADMIN_GENDER={admin gender MALE or FEMALE}
   vocabulary:snapshot-0.0.1
- go to [http://localhost:8080/api/vocabularies](http://localhost:8080/api/vocabularies)

#### With Docker-Compose

- ```shell
    docker compose up
- go to [http://localhost:8080/api/vocabularies](http://localhost:8080/api/vocabularies)



## Description

A backend RestApi application with java using Spring, Spring boot, Hibernate and Postgresql database.

### Dependencies

- [Spring Boot](https://spring.io/projects/spring-boot) - require to create standalone web application.
- [Spring Boot Starter Web](https://spring.io/projects/spring-boot) require to create web application.
- [Spring Boot Starter Validation](https://spring.io/projects/spring-boot) require to validate the request body.
- [Spring Boot Starter Test](https://spring.io/projects/spring-boot), [Spring Boot Starter Security Test](https://spring.io/projects/spring-boot) use to test web application.
- [Spring Boot Starter Data Jpa](https://spring.io/projects/spring-boot) - use for manipulating with database.
- [Spring Boot Starter Mail](https://spring.io/projects/spring-boot) use for sending mail.
- [Spring Boot Starter Security](https://spring.io/projects/spring-boot) use for securing web application.
- [Springdoc Openapi Starter Webmvc Ui](https://springdoc.org/) use for documenting api.
- [jjwt-api](https://github.com/jwtk/jjwt), [jjwt-impl](https://github.com/jwtk/jjwt), [jjwt-jackson](https://github.com/jwtk/jjwt) use for generating and validating jwt-token.
- [Opt-java](https://github.com/BastiaanJansen/otp-java) use for generating and validating reset password otp code.
- [lombok](https://projectlombok.org/) use to auto-generate code.
- [Postgresql](https://jdbc.postgresql.org/) - use to communicate with database server.
- [H2 Database]() - use as embedded database for testing.
- [Dumbster Mock SMTP](https://github.com/kirviq/dumbster) - use for mocking SMTP server.

### Data Modeling

``` mermaid
    erDiagram

    account {
        email character_varying PK "not null"
        profile_id bigint FK "null"
        name character_varying "not null"
        password character_varying "not null"
        gender smallint "not null"
        role smallint "not null"
        profile_id bigint "null"
    }

    account_vocabularies {
        vocabularies_id bigint FK "not null"
        account_email character_varying FK "not null"
    }

    definition {
        id timestamp_without_time_zone PK "not null"
        lang_id bigint FK "null"
        vocabulary_id bigint FK "null"
        type_id integer FK "null"
        def character_varying "not null"
    }

    lang {
        id bigint PK "not null"
        lang character_varying "not null"
    }

    photo {
        id bigint PK "not null"
        url character_varying "not null"
    }

    type {
        id integer PK "not null"
        type character_varying "not null"
    }

    vocabulary {
        id bigint PK "not null"
        photo_id bigint FK "null"
        spelling character_varying "not null"
        photo_id bigint "null"
    }

    vocabulary_others {
        others_id bigint FK "not null"
        vocabulary_id bigint FK "not null"
        others_id bigint "not null"
    }

    vocabulary_types {
        vocabularies_id bigint FK "not null"
        types_id integer FK "not null"
    }

    account ||--o{ account_vocabularies : "account_vocabularies(account_email) -> account(email)"
    lang ||--o{ definition : "definition(lang_id) -> lang(id)"
    photo ||--o{ account : "account(profile_id) -> photo(id)"
    photo ||--o{ vocabulary : "vocabulary(photo_id) -> photo(id)"
    type ||--o{ definition : "definition(type_id) -> type(id)"
    type ||--o{ vocabulary_types : "vocabulary_types(types_id) -> type(id)"
    vocabulary ||--o{ account_vocabularies : "account_vocabularies(vocabularies_id) -> vocabulary(id)"
    vocabulary ||--o{ definition : "definition(vocabulary_id) -> vocabulary(id)"
    vocabulary ||--o{ vocabulary_others : "vocabulary_others(others_id) -> vocabulary(id)"
    vocabulary ||--o{ vocabulary_others : "vocabulary_others(vocabulary_id) -> vocabulary(id)"
    vocabulary ||--o{ vocabulary_types : "vocabulary_types(vocabularies_id) -> vocabulary(id)"
```

### Rest-API-Endpoints

- [run application](#running)
- goto http://localhost:8080/swagger-ui.html


