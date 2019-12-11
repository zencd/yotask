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

- H2 in-memory DB used
- Java 8 (checked with)

## Run

1. cd the project
1. `gradlew clean test bootJar && java -jar build/libs/service-0.0.1-SNAPSHOT.jar`
1. Locate http://localhost:8080/ for swagger and DB console

## TODO

- Validation
- Scheduler?
- Check SQL restrictions
- Check Constraints: @NotNull, @NotBlank
- UUID ids
- Check what exactly rest responses on an error and is it reliable
- Javadoc
- Inspect TODOs
