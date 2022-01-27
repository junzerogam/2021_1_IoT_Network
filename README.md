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
