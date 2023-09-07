# YOTASK

![](logo.png)

Адаптировано в 2023 для Java 17+, Spring Boot 3, gradle 7.

## Objective

Необходимо сделать простую реализацию сервиса тарификации услуг сотовой связи (минут звонков и гигабайт интернета).

Сервис должен позволять:
- Начислять пакеты минут и гигабайтов, имеющих время жизни
- Расходовать минуты и гигабайты
- Получать количество доступных минут и гигабайт
- Активировать и блокировать сим-карты

Технологии для использования:
- Spring Boot
- Gradle
- Любая встраиваемая СУБД
- Любые другие библиотеки

Код должен быть опубликован на github и содержать всё необходимое для работы с приложением описание.

Решение должно быть реализовано в виде самостоятельного jar файла и не должно требовать каких-либо других зависимостей для запуска.

Ожидается высокая степень покрытия тестами.

## Implementation

- Java 17/20 are ok
- Spring Boot 3.1
- Validation
- H2 DB
- H2 console
- Gradle 7
- Lombok
- Mapstruct
- JUnit 4
- OpenApi v3 with UI, code-first

## Run

1. `cd yotask`
2. `./gradlew clean test bootJar && java -jar -Dspring.profiles.active=demo build/libs/simService-0.0.1-SNAPSHOT.jar`
3. Locate http://localhost:8080/ for further details
