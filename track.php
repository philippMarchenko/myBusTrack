<?php // сохранить в utf-8 !

// ---------------------------------------------------------- эти значения задавались при создании БД на сервере
$mysql_host = "s2.s-host.com.ua"; // sql сервер
$mysql_user = "efbubzgb_philipp"; // пользователь
$mysql_password = "2a}gDqbXKCkn"; // пароль
$mysql_database = "efbubzgb_trackingDb"; // имя базы данных efbubzgb_trackingDb


$event = "asc";
$trackId = 1;

$linkServ = mysql_connect($mysql_host, $mysql_user, $mysql_password); // коннект к серверу SQL
if (!$linkServ) {
    //echo('Ошибка соединения к серверу: <br>' . mysql_error());
}
else
//echo 'Успешно соединились с сервером <br>';

$linkDb = mysql_select_db($mysql_database); // коннект к БД на сервере

if(!linkDb){
	//echo 'Ошибка соединения к БД <br>';
}
else
	//echo 'Успешно соединились с БД <br>';

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
if (isset($_GET["time"])) { 
    $time = $_GET['time'];
}
if (isset($_GET["trackId"])) { 
    $trackId = $_GET['trackId'];
}

$current_time = round(microtime(1) * 1000);

if($event == insert){
	$result = mysql_query("INSERT INTO `tracking`(`latitude`,`longitude`,`time`,`trackId`) VALUES ('$latitude','$longitude','$current_time','$trackId')");
		if (!$result) {
			//die('Неверный запрос на добавление: ' . mysql_error());
		}
	}
	
	if($event == update){
	
	$result = mysql_query("UPDATE `tracking` SET time='$current_time', latitude='$latitude', longitude='$longitude' WHERE trackId='$trackId'");
	
		//"UPDATE $table SET title='$title',meta_d='$meta_d',meta_k='$meta_k',img='$img' WHERE id='$update'";  
	
	//$result = mysql_query("UPDATE `tracking`(`latitude`,`longitude`,`date`,`time`,`trackId`) VALUES ('$latitude','$longitude','$date','$current_time','$trackId')");
		if (!$result) {
			//die('Неверный запрос на обновление: ' . mysql_error());
		}
	}
	
	
	
	
	
if($event == select){
	//$output[] = array();
	$result = mysql_query("SELECT * FROM tracking WHERE trackId = $trackId");
	//echo $result;
	//$row = mysql_fetch_assoc($result)
		while ($row = mysql_fetch_assoc($result)) {
		 $output=$row;
	}
	//echo $output[0];
	//echo $output[1];
	//echo $output[2];
	
	echo json_encode($output[0],JSON_FORCE_OBJECT);
	//$output[] = $
	//echo $output[0];
	//echo $output[1];
	//echo json_encode($output,JSON_FORCE_OBJECT);
   // print(json_encode($output[0],JSON_FORCE_OBJECT));
        
	   
	//print(json_encode($output));
	
    //$out = json_encode($output);
	//echo $out + trackId;
	//echo $trackId;
	  /* очищаем результирующий набор */
    //mysqli_free_result($result);
	//mysql_free_result($result);
}
	
if($event == delete){

$result = mysql_query("TRUNCATE TABLE `tracking`");	

		if (!$result) {
			//die('Неверный запрос очищение: ' . mysql_error());
		}
}

//if($action == insert && $author != null && $client != null && $text != null){ // если действие INSERT и есть все что нужно

// время = время сервера а не клиента !

// пример передачи скрипту данных:
// chat.php?action=insert&author=author&client=client&text=text
// вставим строку с переданными параметрами
//}

mysql_close();

//echo "event $event longitude $longitude latitude $latitude date $date result $result time $current_time trackId $trackId <br/>";


//echo 'Hello World!';


?>