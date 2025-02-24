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
        user -> system "Запрос на получение ранее распечатанных файлов"
        system -> user "Ранее распечатанные файлы"
        user -> system "Отправка запрос на печать нераспечатанного файла"

        admin -> system "Регистрация администратора"
        system -> admin "Данные нового пользователя"
        admin -> system "Ответ на регистрацию нового пользователя"

        user -> system.wa "Отправка данных для регистрации"
        system.wa -> system.main_server "Отправка данных пользователя для регистрации"
        system.main_server -> system.wa "Отправка данных пользователя для регистрации админу"
        system.wa -> admin "Отправка данных регистрирующегося пользователя"
        admin -> system.wa "Ответ на запрос регистрации пользователя"
        system.wa -> system.main_server "Ответ на запрос регистрации пользователя"
        system.main_server -> system.db "Запись нового пользователя"

        admin -> system.wa "Отправка данных для регистрации"
        system.wa -> system.main_server "Отправка данных админа"
        system.main_server -> system.db "Запись нового админа"
        system.main_server -> system.wa "Ответ на регистрацию админа"
        system.wa -> admin "Ответ на регистрацию"

        user -> system.wa "Отправка файла для печати"
        system.wa -> system.main_server "Отправка файла на сервер печати"
        system.main_server -> system.printer "Запрос на печать файла"
        system.main_server -> system.db "Запись нового файла"
        system.printer -> system.main_server "Уведомление о завершении печати / ошибке"
        system.main_server -> system.wa "Уведомление о завершении печати / ошибке"
        system.wa -> user "Уведомление о завершении печати / ошибке"

        user -> system.wa "Запрос на получение всех нераспечатанных файлов"
        system.wa -> system.main_server "Запрос на получение всех нераспечатанных файлов"
        system.main_server -> system.db "Запрос на получение всех нераспечатанных файлов"
        system.db -> system.main_server "Все нераспечатанные файлы"
        system.main_server -> system.wa "Все нераспечатанные файлы"
        system.wa -> user "Все нераспечатанные файлы"

        user -> system.wa "Запрос на получение всех ранее распечатанных файлов"
        system.wa -> system.main_server "Запрос на получение всех ранее распечатанных файлов"
        system.main_server -> system.db "Запрос на получение всех ранее распечатанных файлов"
        system.db -> system.main_server "Все ранее распечатанные файлы"
        system.main_server -> system.wa "Все ранее распечатанные файлы"
        system.wa -> user "Все ранее распечатанные файлы"
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
