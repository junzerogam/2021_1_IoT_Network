package MQTT_project;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MqttPublisher_API implements MqttCallback{ // implement callback 추가 & 필요한 메소드 정의
	static MqttClient sampleClient;// Mqtt Client 객체 선언
	
    public static void main(String[] args) {
    	MqttPublisher_API obj = new MqttPublisher_API();
    	obj.run();
    }
    public void run() {    	
    	connectBroker(); // 브로커 서버에 접속
    	try { // 여기 추가
    		sampleClient.subscribe("Arrive Time");
		} catch (MqttException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	while(true) {
    		try {
    			int Toward_Chuncheon = get_traffic_time_toward_Chuncheon(); //남양주 -> 남춘천 토픽
    			int Toward_Namyangju = get_traffic_time_toward_Namyangju(); //남춘천 -> 남양주 토픽
    			
    	       	publish_data("Toward_Chuncheon", "{\"Toward_Chuncheon\": "+ Toward_Chuncheon+"}"); //데이터 publish
    	       	publish_data("Toward_Namyangju", "{\"Toward_Namyangju\": "+ Toward_Namyangju+"}"); //데이터 publish
    	       	
    	       	Thread.sleep(3000); // 시간설정만큼 발행 (10분마다 발행으로 기본설정) 
    	       						//왜냐하면 기본 api가 15분마다 새로운 정보로 업데이트 되기 때문에
    		}catch (Exception e) {
				// TODO: handle exception
    			try {
    				sampleClient.disconnect();
				} catch (MqttException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    			e.printStackTrace();
    	        System.out.println("Disconnected");
    	        System.exit(0);
			}
    	}
    }
    
    public void connectBroker() {
        String broker = "tcp://127.0.0.1:1883"; // 브로커 서버의 주소 
        String clientId = "practice"; // 클라이언트의 ID
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            sampleClient = new MqttClient(broker, clientId, persistence);// Mqtt Client 객체 초기화
            MqttConnectOptions connOpts = new MqttConnectOptions(); // 접속시 접속의 옵션을 정의하는 객체 생성
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts); // 브로커서버에 접속
            sampleClient.setCallback(this);// Call back option 추가
            System.out.println("Connected");
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
    
    public void publish_data(String topic_input, String data) { 
        String topic = topic_input; // 토픽
        int qos = 0; // QoS level
        try {
            System.out.println("Publishing message: "+data);
            sampleClient.publish(topic, data.getBytes(), qos, false);
            System.out.println("Message published");
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
    
    public int get_traffic_time_toward_Namyangju() { // 남춘천 -> 남양주 소요시간 메소드
    	
    	String url = "http://data.ex.co.kr/openapi/odhour/trafficTimeByRoute" //한국도로공사 api 목록 중 노선 별 영업소간 구간내 소요시간 조회 주소
    			+ "?key=8238184168" // api 키값
    			+ "&type=xml"
    			+ "&startUnitCode=656" //남춘천 영업소 코드 = 입구 영업소
    			+ "&endUnitCode=651"  //남양주 영업소 코드 = 출구 영업소
    			+ "&carType=1"; //1종
    							//1종을 선택한 이유 : 가장 빠르게 다니고 교통량이 가장 많이 때문에
    	
    	//데이터를 저장할 변수 초기화
		String timeAvg_toward_Namyangju = "0"; //남춘천 -> 남양주 소요시간 평균
		
    	Document doc = null;
		
		// Jsoup으로 API 데이터 가져오기
		try {
			doc = Jsoup.connect(url).get(); //위에 설정한 url을 가져와서 연결한다.
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Elements elements = doc.select("list"); //list항목을 선택하고
		for (Element e : elements) {
			if (e.select("startUnitCode").text().equals("656")) { //시작영업소가 남춘천(656)이면
				timeAvg_toward_Namyangju = e.select("timeAvg").text(); //평균소요시간을 변수에 저장한다
			}
		}
		int toward_Namyangju_Sec = Integer.parseInt(timeAvg_toward_Namyangju); //timeAvg_toward_Namyangju가 초 단위 문자열 형태이므로
		                                                                       // Integer.parseInt로 int형으로 바꿔준 다음에
		int timeAvg_toward_Namyangju_Min = toward_Namyangju_Sec / 60; //보기 쉽게 분 단위로 바꿔준다.
    	return timeAvg_toward_Namyangju_Min;
    }
    
    
    public int get_traffic_time_toward_Chuncheon() { // 남양주 -> 남춘천 소요시간 메소드
       	
    	String url = "http://data.ex.co.kr/openapi/odhour/trafficTimeByRoute" //한국도로공사 api 목록 중 노선 별 영업소간 구간내 소요시간 조회 주소
    			+ "?key=8238184168" // api 키값
    			+ "&type=xml"
    			+ "&tmType=1"
    			+ "&startUnitCode=651" //남양주 영업소 코드 = 입구 영업소
    			+ "&endUnitCode=656" //남춘천 영업소 코드 = 출구 영업소
    			+ "&carType=1"; //1종
    	
    	//데이터를 저장할 변수 초기화
		String timeAvg_toward_Chuncheon = "0"; //남양주 -> 남춘천 소요시간 평균
	
    	Document doc = null;
		
		// Jsoup으로 API 데이터 가져오기
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(doc);
		
		Elements elements = doc.select("list");
		for (Element e : elements) {
			if (e.select("startUnitCode").text().equals("651")) { // 시작영업소가 남양주(651)이면
				timeAvg_toward_Chuncheon = e.select("timeAvg").text(); //평균소요시간을 변수에 저장한다
			}
		}
		int toward_Chuncheon_Sec = Integer.parseInt(timeAvg_toward_Chuncheon); //timeAvg_toward_Chuncheon가 초 단위 문자열 형태이므로
																			   // Integer.parseInt로 int형으로 바꿔준 다음에
		int timeAvg_toward_Chuncheon_Min = toward_Chuncheon_Sec / 60; //보기 쉽게 분 단위로 바꿔준다
    	return timeAvg_toward_Chuncheon_Min;
    }
 
	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		System.out.println("Connection lost");
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception {
		// TODO Auto-generated method stub
		if (topic.equals("Arrive Time")){
			System.out.println("--------------------예상도착시간측정--------------------");
			System.out.println(msg.toString());
			System.out.println("시간을 측정해보자~!");
			System.out.println("----------------------------------------------------");
		}		
	}
}
