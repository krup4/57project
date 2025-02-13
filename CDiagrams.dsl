workspace {
    !identifiers hierarchical

    model {
        user = person "Пользователь" "Человек, который использует систему для печати файлов."
        admin = person "Администратор" "Человек, который управляет пользователями и системой печати."
        system = softwareSystem "Система" "Центральная система, которая обрабатывает запросы на регистрацию и печать." {
            wa = container "Веб-интерфейс"
            db = container "База данных" {
                tags "Database"
            }
            server = container "Сервер печати" "Сервер, который управляет процессом печати."
            printer = container "Принтер" "Устройство, которое выполняет печать файлов."
            main_server = container "Главный сервер, обрабатывающий запросы"
        }

        user -> system "Отправка данных для регистрации"
        user -> system "Отправка файла для печати"
        system -> user "Уведомление о завершении печати / ошибке"

        admin -> system "Регистрация администратора"
        admin -> system "Отправка запрос на печать нераспечатанного файла"
        system -> admin "Уведомление о завершении печати / ошибке"

        user -> system.wa "Отправка данных для регистрации"
        system.wa -> system.main_server "Отправка данных пользователя для регистрации"
        system.main_server -> system.wa "Отправка данных пользователя для регистрации админу"
        system.wa -> admin "Отправка данных регистрирующегося пользователя"
        admin -> system.wa "Ответ на запрос регистрации пользователя"
        system.wa -> system.main_server "Ответ на запрос регистрации пользователя"
        system.main_server -> system.db "Запись нового пользователя"

        user -> system.wa "Отправка файла для печати"
        system.wa -> system.main_server "Отправка файла на сервер печати"
        system.main_server -> system.server "Отправка файла на сервер печати"
        system.server -> system.printer "Запрос на печать файла"
        system.printer -> system.server "Уведомление о завершении печати / ошибке"
        system.server -> system.main_server "Уведомление о завершении печати / ошибке"
        system.main_server -> system.wa "Уведомление о завершении печати / ошибке"
        system.wa -> user "Уведомление о завершении печати / ошибке"
        system.wa -> admin "Уведомление о завершении печати / ошибке"

        admin -> system.wa "Запрос на получение всех нераспечатанных файлов"
        system.wa -> system.main_server "Запрос на получение всех нераспечатанных файлов"
        system.main_server -> system.db "Запрос на получение всех нераспечатанных файлов"
        system.db -> system.main_server "Все нераспечатанные файлы"
        system.main_server -> system.wa "Все нераспечатанные файлы"
        system.wa -> admin "Все нераспечатанные файлы"
        admin -> system.wa "Запрос на печать нераспечатанного файла"


    }

    views {
        systemContext system "Diagram1" {
            include *
            autolayout lr
        }

        container system "Diagram2" {
            include *
            autolayout lr
        }

        styles {
            element "Element" {
                color #ffffff
            }
            element "Person" {
                background #05527d
                shape person
            }
            element "Software System" {
                background #066296
            }
            element "Container" {
                background #0773af
            }
            element "Database" {
                shape cylinder
            }
        }
    }

    configuration {
        scope softwaresystem
    }
}
