## Дипломный проект по профессии «Android Developer»

#### Разработка Android-приложения - социальная сеть для профессионалов.

Описание

Приложение реализует следующие блоки пользовательского интерфейса:

#### Основной экран
Стартовый экран должен содержать:

нижнее меню с тремя кнопками: посты, события и пользователи,
кнопку меню в AppBar с возможностью перехода на вход в аккунт и регистрацию или просмотр профиля.

Экран со списком постов.

Этот фрагмент главного экрана содержит:

список постов,
кнопку +, нажатие которой ведёт на создание нового поста или диалог с предложением войти/зарегистрироваться.

#### Карточка поста 
Карточка поста должна включать:

 аватар автора поста;
 имя автора;
 дату публикации в формате dd.mm.yyyy HH:mm;
 кнопку лайка с количеством лайков;
 вложение, при наличии: аудио, видео или фото;
 ссылку, при наличии, с переходом в браузер;
 кнопку вызова меню с возможностью удаления или перехода к редактированию, в случае, если текущий пользователь является автором;
 текст поста.

#### Экран со списком событий
Этот фрагмент главного экрана содержит:

список событий,
кнопку +, нажатие которой ведёт на создание нового события или диалог с предложением войти/зарегистрироваться.

Карточка события должна включать:

 аватар автора события;
 имя автора;
 дату публикации в формате dd.mm.yyyy HH:mm;
 дату проведения в формате dd.mm.yyyy HH:mm;
 тип события: офлайн или онлайн;
 кнопку лайка с количеством лайков;
 вложение, при наличии: аудио, видео или фото;
 ссылку, при наличии, с переходом в браузер;
 кнопку вызова меню с возможностью удаления или перехода к редактированию, в случае, если текущий пользователь является автором;
 текст поста.

#### Экран со списком пользователей
Этот фрагмент главного экрана содержит список пользователей.

Карточка пользователя должна включать:

 логин пользователя;
 имя пользователя;
 аватар пользователя.
При нажатии на карточку пользователя должен быть произведён переход к детальному виду.

#### Экран входа в приложение 
Необходимо использовать поля ввода для логина и пароля.

При этом должны проверяться основные ограничения на значения указанных полей:

 логин — непустая строка;
 пароль — непустая строка.
При несоответствии требованиям в полях должны отображаться определённые информативные сообщения с возможностью исправления данных.

В случае получения от сервера 400 кода следует отобразить ошибку в виде тоста «Неправильный логин или пароль».

#### Экран регистрации
Необходимо использовать поля ввода для логина, имени, пароля и повтора пароля. А также кнопку с возможностью выбора фото и её превью.

При этом должны проверяться основные ограничения на значения указанных полей:

 логин — непустая строка;
 имя — непустая строка;
 пароль — непустая строка;
 аватар — изображение в формате jpeg, png, максимальный размер 2048*2048.
При несоответствии требованиям в полях должны отображаться определённые информативные сообщения с возможностью исправления данных.

В случае получения от сервера 400 кода следует отобразить ошибку в виде тоста «Пользователь с таким логином уже зарегистрирован».

#### Экран создания/редактирования поста
На этот экран может попасть только авторизованный пользователь.

Экран содержит:

поле ввода текста;
кнопку выбора локации: переход на фрагмент с картой;
кнопку выбора упомянутых пользователей (экран со списком и множественным выбором);
кнопки выбора изображения: фото или галерея;
кнопки выбора вложения: аудио, видео;
кнопку сохранить в AppBar.
Размер вложений не должен превышать 15 МБ.

После создания поста следует возврат назад к списку постов.

#### Экран создания/редактирования события
На этот экран может попасть только авторизованный пользователь.

Экран содержит:

поле ввода текста;
кнопку выбора локации: переход на фрагмент с картой;
radio button с выбором типа между online и offline (по умолчанию online);
кнопку выбора даты проведения события;
кнопки выбора изображения: фото или галерея;
кнопки выбора вложения: аудио, видео;
кнопку выбора спикеров: диалог со списком пользователей;
кнопку сохранить в AppBar.

#### Экран просмотра стены
Стена представляет собой список постов, написанный одним автором. Внешний вид аналогичен карточкам из стартового экрана.

#### Экран просмотра пользователя
Экран содержит:

имя и логин в AppBar;
фото пользователя;
табы с выбором между стеной и работами пользователя.

#### Экран просмотра своего профиля
Функционал и внешний вид этого экрана аналогичен просмотру пользователя, но включает возможность удалять и создавать работы.


#### Инструменты / дополнительные материалы

Android Studio

Kotlin

Retrofit

Dagger Hilt

Swagger

Room

Glide

Figma

MapKit SDK

Git + GitHub