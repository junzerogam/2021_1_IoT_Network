# IoT-Network-Project
* IoT Network Lecture Project (2021.05-2021.06)
* 2021-1 Spring
# Purpose
* MQTT Protocol을 통한 IoT 기기들의 통신 구현 및 작동원리 파악
# Theme
* 한국도로공사 공공API 활용
* 서울양양간고속도로 남양주-남춘천구간 실시간 소요시간 측정 및 가변차로 시행여부 확인
* 웹페이지 구현으로 표현
# Structure
<img src=브로커그림.jpg> 

# Code 
## 1. MQTT Publisher
### 1.1 Run Method
<img src=자바소스코드1TopicPublish.jpg> 

### 1.2 connectBroker
<img src=자바소스코드2ConnectBroker.jpg>

### 1.3 Publish Data
<img src=자바소스코드3PublishData.jpg>

### 1.4 Get Traffic Time
<img src=자바소스코드4towardNamyangj메소드.jpg>
<img src=자바소스코드5towardChuncheon메소드.jpg>

### 1.5 messageArrive Method
* 버튼 클릭시 동작되는 메소드
<img src=자바소스코드6messageArrived.jpg>

## 2. MQTT Subscriber
### 2.1 Connect MongoDB 
<img src=vscode캡쳐2.jpg.png>  

### 2.2 Subscire Topic
<img src=vscode캡쳐1.jpg>

### 2.3 Topic Save 
* 구독된 토픽을 데이터베이스에 저장
<img src=vscode캡쳐3.png>

### 2.4 Socket 
* 소켓통신을 이용한 데이터저장  
<img src=vscode캡쳐4.png>  

* 소켓통신을 이용한 버튼이벤트생성  
<img src=vscode캡쳐5.png>

## 3. Socket Communication / HTML Page 
### 3.1 Socket Communication
* 소켓통신에 의해 해당 파일이 수신된다면 다음과 같이 실행됨
<img src=vscode캡쳐6.png>

* 버튼이 눌렸을 때 실행되는 메소드
<img src=vscode캡쳐7.png>  

### 3.2 HTML Page Code
<img src=html코드1.png>

# Result
## 1. Broker Server Connect / Start Topic Publish
<img src=자바실행화면.jpg>  

## 2. Databace Connect / Start Topic Subscribe 
<img src=vscode실행화면1.jpg>  

## 3. Topic Subscribe
<img src=vscode실행화면2.jpg>  
<img src=vscode실행화면4.jpg>  

## 4. HTML Page Connect / Press the Button Event
<img src=vscode실행화면3.jpg>  
<img src=자바실행화면2.jpg>  

## 5. ROBO 3T Database
<img src=ROBO3T.jpg>    

## 6. HTML Page 
<img src=html페이지캡쳐.jpg>
<img src=html페이지캡쳐2.jpg>
