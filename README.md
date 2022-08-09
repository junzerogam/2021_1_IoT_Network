# IoT Network Project
* MQTT Protocol 및 실시간 교통정보 API를 활용한 웹페이지 구현 (2021.05-2021.06)
* 2021-1 Spring IoT Network Project
## :exclamation: Purpose
1. MQTT Protocol을 통한 IoT 기기들의 통신 구현 및 작동원리 파악
2. 공공 API 활용 능력 배양
3. 간단한 웹페이지 구현
## :bulb: Summary
> IoT 기기들이 MQTT 프로토콜을 가지고 어떻게 통신하고 구현되는지 프로젝트를 통하여 경험해보고 구현해 봄으로서, 작동 원리를 파악 하는 것이 프로젝트의 주목적이다.<br><br>
한국도로공사 공공데이터포탈의 ‘노선별 영업소간 구간내 소요시간 일괄조회’ API를 통하여, 여름 휴가철에 상습 정체구간인 남양주 – 남춘천 사이의 평균 시간들의 데이터를 기반하여, 양방향 소요시간을 측정하고 예상 도착시간과 가변차로 시행여부 정보를 표현한다.<br><br>
이를 통해 집에서 출발하기 전, 소요시간을 파악하여, 고속도로를 우회여부를 결정하여 휴가철에 유용하게 사용할 수 있다.
## 💻 Structure
<p align="center">
  <img src=docs/img/브로커그림.jpg width="70%" height="70%"> 
</p>  

## 🏃 Ongoing
### :zero: About MQTT Protocol
<p align="left">
  <img src=docs/img/mqtt.jpg width="50%" height="50%"> 
</p>  

### 1️⃣ Connect Broker Server
<p align="left">
  <img src=docs/img/자바실행화면.jpg> 
</p>  

### 2️⃣ Publish Topic
<p align="left">
  <img src=docs/img/자바실행화면3.jpg> 
</p>  

### 3️⃣ Connect MongoDB & start Subscirbe Topic
<p align="left">
  <img src=docs/img/vscode실행화면1.jpg>  
  <img src=docs/img/vscode실행화면4.jpg> 
</p>  

### 4️⃣ Store at Database
<p align="left">
  <img src=docs/img/ROBO3T.jpg> 
</p>  

### 5️⃣ TCP Socket Communication
<p align="left">
  <img src=docs/img/vscode캡쳐4.png width="50%" height="50%"> 
</p>  

### 6️⃣ Representation
<p align="left">
  <img src=docs/img/html페이지캡쳐.jpg width="50%" height="50%">
  <img src=docs/img/html페이지캡쳐2.jpg width="50%" height="50%">
</p>  

## 📌 Result
✔️ MQTT Protocol의 전반적인 이해  
✔️ API 활용  
✔️ 데이터베이스 활용  
✔️ HTML 기반 웹페이지 구현  

## 😄 What's Next Project ?
✔️ Coming Soon !
