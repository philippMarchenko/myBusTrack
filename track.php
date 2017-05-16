<?php // сохранить в utf-8 !

// ---------------------------------------------------------- эти значения задавались при создании БД на сервере
$mysql_host = "s2.s-host.com.ua"; // sql сервер
$mysql_user = "efbubzgb_philipp"; // пользователь
$mysql_password = "2a}gDqbXKCkn"; // пароль
$mysql_database = "efbubzgb_trackingDb"; // имя базы данных efbubzgb_trackingDb

$linkServ = mysql_connect($mysql_host, $mysql_user, $mysql_password); // коннект к серверу SQL
if (!$linkServ) {
    echo('Ошибка соединения к серверу: <br>' . mysql_error());
}
else
	echo 'Успешно соединились с сервером <br>';

$linkDb = mysql_select_db($mysql_database); // коннект к БД на сервере
if(!linkDb){
	echo 'Ошибка соединения к БД <br>';
}
else
	echo 'Успешно соединились с БД <br>';

mysql_set_charset('utf8'); // кодировка

echo 'Hello World! '


?>