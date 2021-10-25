#!/usr/bin/env node

/**
 * Module dependencies.
 */

 var app = require('../app');
 var debug = require('debug')('iotserver:server');
 var http = require('http');
 
 /**
  * Get port from environment and store in Express.
  */
 
 var port = normalizePort(process.env.PORT || '3000');
 app.set('port', port);
 
 /**
  * Create HTTP server.
  */
 
 var server = http.createServer(app);
 
 // Connect Mongo DB 
 var mongoDB = require("mongodb").MongoClient;
 var url = "mongodb://127.0.0.1:27017/IoTDB";
 var dbObj = null;
 mongoDB.connect(url, function(err, db){
   dbObj = db;
   console.log("DB connect");
 });
 
 /**
  * MQTT subscriber (MQTT Server connection & Read resource data)
  */
 var mqtt = require("mqtt");
 var client = mqtt.connect("mqtt://127.0.0.1") //local host를 의미 , 내 컴퓨터 자신
 
 // 접속에 성공하면, 2가지 토픽을 구독.
 client.on("connect", function(){
   client.subscribe("Toward_Chuncheon"); //남양주 -> 남춘천 평균 소요시간 토픽 구독
   console.log("Subscribing Toward_Chuncheon"); //console에 출력

   client.subscribe("Toward_Namyangju");  //남춘천 -> 남양주 평균 소요시간 토픽 구독
   console.log("Subscribing Toward_Namyangju"); //console에 출력
 })

// MQTT 응답 메세지 수신시 동작
 client.on("message", function(topic, message){
   console.log(topic+ ": " + message.toString()); // 수신한 메세지 Topic 출력
   var obj = JSON.parse(message); // 수신한 메세지의 데이터를 obj(JSON파일) 저장
   obj.create_at = new Date(); // 현재 날짜 데이터를 obj에 추가함. 날짜라는 키를 json파일에 저장
   console.log(obj);
 
   // send the received data to MongoDB
   // 수신한 메세지를 Mongo DB에 저장
   if (topic == "Toward_Chuncheon"){ // 남양주 -> 남춘천 평균 소요시간 토픽이면,
     var Toward_Chuncheon  = dbObj.collection("Toward_Chuncheon"); // Toward_Chuncheon 라는 이름을 가진 collection 선택
     Toward_Chuncheon.save(obj, function(err, result){ // collection에 남양주 -> 남춘천 평균 소요시간 저장
       if (err){
         console.log(err);
       }else{
         console.log(JSON.stringify(result));
       }		
     });	
   }else if (topic == "Toward_Namyangju"){ // 남춘천 -> 남양주 평균 소요시간 토픽이면
     var Toward_Namyangju  = dbObj.collection("Toward_Namyangju"); // Toward_Namyangju라는 이름을 가진 collection선택
     Toward_Namyangju.save(obj, function(err, result){ // collection에 남춘천 -> 남양주 평균 소요시간 저장
       if (err){
         console.log(err);
       }else{
         console.log(JSON.stringify(result));
       }		
     });
   }
 });
  
 // get data from MongDB and then send it to HTML page using socket
 // Mongo DB에서 최근 데이터 불러와서, HTML 페이지에 업데이트
 var io = require("socket.io")(server);
 io.on("connection", function(socket){
   socket.on("socket_evt_update", function(data){ //socket_evt_updated 이벤트가 발생되면, 실행
     var Toward_Chuncheon = dbObj.collection("Toward_Chuncheon"); //Toward_Chuncheon 콜렉션을 선택한다
     var Toward_Namyangju = dbObj.collection("Toward_Namyangju"); //Toward_Namyangju 콜렉션을 선택한다

     Toward_Chuncheon.find({}).sort({_id:-1}).limit(1).toArray(function(err, results){ //-1은 마지막부터 접근을 의미, 마지막db에 들어온 데이터부터 정렬
       // collection에서 가장 최근 데이터 정렬-> 하나의 데이터만 불러옴 -> 배열로 만듬(result에 저장)
       if(!err){
         console.log(results[0]); //콘솔로 찍어줌
         socket.emit("socket_up_Toward_Chuncheon", JSON.stringify(results[0])); //소켓으로 전달, 이벤트는 socket_up_Toward_Chuncheon
       }
     });
     
     Toward_Namyangju.find({}).sort({_id:-1}).limit(1).toArray(function(err, results){
       // collection에서 가장 최근 데이터 정렬-> 하나의 데이터만 불러옴 -> 배열로 만듬
       if(!err){
         console.log(results[0]); //콘솔로 찍어준다
         socket.emit("socket_up_Toward_Namyangju", JSON.stringify(results[0])); //이벤트는 socket_up_Toward_Namyangju
       }
     });
   }); 
   //html 페이지에서 예상도착시간 버튼이 눌리면 실행되는 메소드
   socket.on("socket_evt_bnt", function(data){
    var now = new Date();
    console.log(data);
    client.publish("Arrive Time",data);
   }); 
 });
 
 
 /**
  * Listen on provided port, on all network interfaces.
  */
 server.listen(port);
 server.on('error', onError);
 server.on('listening', onListening);
 
 /**
  * Normalize a port into a number, string, or false.
  */
 function normalizePort(val) {
   var port = parseInt(val, 10);
   if (isNaN(port)) {
     // named pipe
     return val;
   }
   if (port >= 0) {
     // port number
     return port;
   }
   return false;
 }
 
 /**
  * Event listener for HTTP server "error" event.
  */
 
 function onError(error) {
   if (error.syscall !== 'listen') {
     throw error;
   }
 
   var bind = typeof port === 'string'
     ? 'Pipe ' + port
     : 'Port ' + port;
 
   // handle specific listen errors with friendly messages
   switch (error.code) {
     case 'EACCES':
       console.error(bind + ' requires elevated privileges');
       process.exit(1);
       break;
     case 'EADDRINUSE':
       console.error(bind + ' is already in use');
       process.exit(1);
       break;
     default:
       throw error;
   }
 }
 
 /**
  * Event listener for HTTP server "listening" event.
  */
 
 function onListening() {
   var addr = server.address();
   var bind = typeof addr === 'string'
     ? 'pipe ' + addr
     : 'port ' + addr.port;
   debug('Listening on ' + bind);
 }
 