﻿<?php // сохранить в utf-8 !

// ---------------------------------------------------------- эти значения задавались при создании БД на сервере
$mysql_host = "s2.s-host.com.ua"; // sql сервер
$mysql_user = "efbubzgb_philipp"; // пользователь
$mysql_password = "2a}gDqbXKCkn"; // пароль
$mysql_database = "efbubzgb_trackingDb"; // имя базы данных efbubzgb_trackingDb


$event = "asc";

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


if (isset($_GET["event"])) { 
    $event = $_GET['event'];
}
if (isset($_GET["longitude"])) { 
    $longitude = $_GET['longitude'];
}
if (isset($_GET["latitude"])) { 
    $latitude = $_GET['latitude'];
}
if (isset($_GET["date"])) { 
    $date = $_GET['date'];
}
if (isset($_GET["time"])) { 
    $time = $_GET['time'];
}
if (isset($_GET["trackId"])) { 
    $trackId = $_GET['trackId'];
}

$result = mysql_query("INSERT INTO `tracking`(`latitude`,`longitude`,`date`,`time`) VALUES ('$latitude','$longitude','$date','$current_time')");

if (!$result) {
    die('Неверный запрос: ' . mysql_error());
}

//if($action == insert && $author != null && $client != null && $text != null){ // если действие INSERT и есть все что нужно

// время = время сервера а не клиента !
$current_time = round(microtime(1) * 1000);
// пример передачи скрипту данных:
// chat.php?action=insert&author=author&client=client&text=text
// вставим строку с переданными параметрами
//}

mysql_close();

echo "event $event longitude $longitude latitude $latitude date $date result $result time $current_time trackId $trackId<br/>";


echo 'Hello World!';


?>