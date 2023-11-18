import com.mqSDK.SubscirberUtil;

public class SubscirberTestThread2 {

    public static void main(String[] args) {
        //1.全广播模式测试
        //testSendAllSub();
        //2.点对点模式测试
        testSendMQSub();
        //3.发布订阅模式
        //testSendTopicSub();
    }

    /**
     * 测试全广播模式，消息接收方2
     */
    private static void testSendAllSub(){
        String url = "http://localhost:8080";
        SubscirberUtil.addModule(url, "2");
    }

    /**
     * 测试点对点模式，消息接收方2，向队列queue2注册
     */
    private static void testSendMQSub(){
        String url = "http://localhost:8080";
        SubscirberUtil.registerAndListenMQ(url, "queue2");
    }

    /**
     * 测试发布订阅模式
     */
    private static void testSendTopicSub(){
        String url = "http://localhost:8080";
        SubscirberUtil.registerAndListenTopic(url,"2","topic2");
    }
}
