Где моя маршрутка?
===============
Исходные тексты программы [Где моя маршруштка?][1]

Программа "притворяется" front-end'ом сервиса http://beta.ulroad.ru и получает данные через http GET запросы.

Зависимости
-------------
   * [Yandex Map Kit][2]

Дополнительная информация
---------------------------
Список маршрутов пришлось захардкодить в программе, т.к. сайт не предоставляет функций для их получения. Парсить же сайт для их получения кажется нецелесообразным.

В текущей версии Yandex Map Kit нет документированной возможностей реализовывать собственные слои, поэтому пришлось "реверсить" - в части программы есть использование "обфусцированных"(?) переменных

После обновления на сайте появился функционал, который не реализован в приложении:

   * Просмотр автобусных остановок
   * Просмотр сразу нескольких маршрутов


[1]: http://play.google.com/store/apps/details?id=com.ursinepaw.publictransport
[2]: http://github.com/yandexmobile/yandexmapkit-android