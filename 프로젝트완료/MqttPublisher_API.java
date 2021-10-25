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

public class MqttPublisher_API implements MqttCallback{ // implement callback �߰� & �ʿ��� �޼ҵ� ����
	static MqttClient sampleClient;// Mqtt Client ��ü ����
	
    public static void main(String[] args) {
    	MqttPublisher_API obj = new MqttPublisher_API();
    	obj.run();
    }
    public void run() {    	
    	connectBroker(); // ���Ŀ ������ ����
    	try { // ���� �߰�
    		sampleClient.subscribe("Arrive Time");
		} catch (MqttException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	while(true) {
    		try {
    			int Toward_Chuncheon = get_traffic_time_toward_Chuncheon(); //������ -> ����õ ����
    			int Toward_Namyangju = get_traffic_time_toward_Namyangju(); //����õ -> ������ ����
    			
    	       	publish_data("Toward_Chuncheon", "{\"Toward_Chuncheon\": "+ Toward_Chuncheon+"}"); //������ publish
    	       	publish_data("Toward_Namyangju", "{\"Toward_Namyangju\": "+ Toward_Namyangju+"}"); //������ publish
    	       	
    	       	Thread.sleep(3000); // �ð�������ŭ ���� (10�и��� �������� �⺻����) 
    	       						//�ֳ��ϸ� �⺻ api�� 15�и��� ���ο� ������ ������Ʈ �Ǳ� ������
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
        String broker = "tcp://127.0.0.1:1883"; // ���Ŀ ������ �ּ� 
        String clientId = "practice"; // Ŭ���̾�Ʈ�� ID
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            sampleClient = new MqttClient(broker, clientId, persistence);// Mqtt Client ��ü �ʱ�ȭ
            MqttConnectOptions connOpts = new MqttConnectOptions(); // ���ӽ� ������ �ɼ��� �����ϴ� ��ü ����
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts); // ���Ŀ������ ����
            sampleClient.setCallback(this);// Call back option �߰�
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
        String topic = topic_input; // ����
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
    
    public int get_traffic_time_toward_Namyangju() { // ����õ -> ������ �ҿ�ð� �޼ҵ�
    	
    	String url = "http://data.ex.co.kr/openapi/odhour/trafficTimeByRoute" //�ѱ����ΰ��� api ��� �� �뼱 �� �����Ұ� ������ �ҿ�ð� ��ȸ �ּ�
    			+ "?key=8238184168" // api Ű��
    			+ "&type=xml"
    			+ "&startUnitCode=656" //����õ ������ �ڵ� = �Ա� ������
    			+ "&endUnitCode=651"  //������ ������ �ڵ� = �ⱸ ������
    			+ "&carType=1"; //1��
    							//1���� ������ ���� : ���� ������ �ٴϰ� ���뷮�� ���� ���� ������
    	
    	//�����͸� ������ ���� �ʱ�ȭ
		String timeAvg_toward_Namyangju = "0"; //����õ -> ������ �ҿ�ð� ���
		
    	Document doc = null;
		
		// Jsoup���� API ������ ��������
		try {
			doc = Jsoup.connect(url).get(); //���� ������ url�� �����ͼ� �����Ѵ�.
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Elements elements = doc.select("list"); //list�׸��� �����ϰ�
		for (Element e : elements) {
			if (e.select("startUnitCode").text().equals("656")) { //���ۿ����Ұ� ����õ(656)�̸�
				timeAvg_toward_Namyangju = e.select("timeAvg").text(); //��ռҿ�ð��� ������ �����Ѵ�
			}
		}
		int toward_Namyangju_Sec = Integer.parseInt(timeAvg_toward_Namyangju); //timeAvg_toward_Namyangju�� �� ���� ���ڿ� �����̹Ƿ�
		                                                                       // Integer.parseInt�� int������ �ٲ��� ������
		int timeAvg_toward_Namyangju_Min = toward_Namyangju_Sec / 60; //���� ���� �� ������ �ٲ��ش�.
    	return timeAvg_toward_Namyangju_Min;
    }
    
    
    public int get_traffic_time_toward_Chuncheon() { // ������ -> ����õ �ҿ�ð� �޼ҵ�
       	
    	String url = "http://data.ex.co.kr/openapi/odhour/trafficTimeByRoute" //�ѱ����ΰ��� api ��� �� �뼱 �� �����Ұ� ������ �ҿ�ð� ��ȸ �ּ�
    			+ "?key=8238184168" // api Ű��
    			+ "&type=xml"
    			+ "&tmType=1"
    			+ "&startUnitCode=651" //������ ������ �ڵ� = �Ա� ������
    			+ "&endUnitCode=656" //����õ ������ �ڵ� = �ⱸ ������
    			+ "&carType=1"; //1��
    	
    	//�����͸� ������ ���� �ʱ�ȭ
		String timeAvg_toward_Chuncheon = "0"; //������ -> ����õ �ҿ�ð� ���
	
    	Document doc = null;
		
		// Jsoup���� API ������ ��������
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(doc);
		
		Elements elements = doc.select("list");
		for (Element e : elements) {
			if (e.select("startUnitCode").text().equals("651")) { // ���ۿ����Ұ� ������(651)�̸�
				timeAvg_toward_Chuncheon = e.select("timeAvg").text(); //��ռҿ�ð��� ������ �����Ѵ�
			}
		}
		int toward_Chuncheon_Sec = Integer.parseInt(timeAvg_toward_Chuncheon); //timeAvg_toward_Chuncheon�� �� ���� ���ڿ� �����̹Ƿ�
																			   // Integer.parseInt�� int������ �ٲ��� ������
		int timeAvg_toward_Chuncheon_Min = toward_Chuncheon_Sec / 60; //���� ���� �� ������ �ٲ��ش�
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
			System.out.println("--------------------�������ð�����--------------------");
			System.out.println(msg.toString());
			System.out.println("�ð��� �����غ���~!");
			System.out.println("----------------------------------------------------");
		}		
	}
}