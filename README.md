# 57project
## Функционал:

- Регистрация в 2 формах(админ и обычный пользователь) :
  - обычный пользователь: регистрация одобряется админом
  - админ: должен ввести специальный ключ(например)

- Печать файлов:
  - пользователь(и админ, и обычный пользователь) закидывает файл, и он автоматически печатается.
  - если произошла какая-то ошибка(например: закончилась бумага), админ может повторно запустить печать нераспечатанных файлов.

## User  story:
- Пользователь: <br />
  Как пользователь без прямого доступа к принтеру, я хочу печать файлы, не подходя к принтеру, чтобы не делать лишние действия по пересылке файлов на компьютер, подключенный к принтеру.

- Администратор: <br />
  Как администратор, я хочу поддерживать сервис в рабочем состоянии, чтобы система работала стабильно и пользователям не приходилось исправлять ошибки.

- Бизнес: <br />
  Как бизнес, я хочу представить пользователям сервис для удобной печати файлов, чтобы пользователи не испытывали трудности с печатью файлов, если их принтер не имеет возможность подключения по wifi или bluetooth
