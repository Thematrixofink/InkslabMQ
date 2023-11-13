import com.mqSDK.SubscirberUtil;

public class SubscirberTestThread3 {
    public static void main(String[] args) {
        testSendTopicSub();
    }
    /**
     * 测试发布订阅模式
     */
    private static void testSendTopicSub(){
        String url = "http://localhost:8080";
        SubscirberUtil.registerAndListenTopic(url,"3","topic1");
    }
}
