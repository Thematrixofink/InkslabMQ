import com.mqSDK.PublisherUtil;

public class PublisherTest {
    public static void main(String[] args) throws InterruptedException {
        //1.全广播模式测试
        //testSendAllPub();
        //2.点对点模式测试
        //testSendMegToMQ();
        //3.发布订阅模式测试
        testSendToTopic();
    }

    /**
     * 测试全广播模式，消息发送方
     */
    private static void testSendAllPub() throws InterruptedException {
        //消息队列地址
        String url = "http://localhost:8080";
        System.out.println(PublisherUtil.sendAll(url, "全广播消息1"));
        Thread.sleep(1000);
        System.out.println(PublisherUtil.sendAll(url, "全广播消息2"));
        Thread.sleep(1000);
        System.out.println(PublisherUtil.sendAll(url, "全广播消息3"));
        Thread.sleep(1000);
        System.out.println(PublisherUtil.sendAll(url, "全广播消息4"));
        Thread.sleep(1000);
        System.out.println(PublisherUtil.sendAll(url, "全广播消息5"));
        Thread.sleep(1000);
    }

    /**
     * 测试点对点通信
     * @throws InterruptedException
     */
    private static void testSendMegToMQ() throws InterruptedException {
        //消息队列地址
        String url = "http://localhost:8080";
        System.out.println(PublisherUtil.sendMQ(url, "queue1","队列1专属-消息1"));
        Thread.sleep(1000);
        System.out.println(PublisherUtil.sendMQ(url, "queue1","队列1专属-消息2"));
        Thread.sleep(2000);
        System.out.println(PublisherUtil.sendMQ(url, "queue1","队列1专属-消息3"));
        Thread.sleep(2000);
        System.out.println(PublisherUtil.sendMQ(url, "queue2","队列2专属-消息4"));
        Thread.sleep(2000);
        System.out.println(PublisherUtil.sendMQ(url, "queue2","队列2专属-消息5"));
        Thread.sleep(2000);
        System.out.println(PublisherUtil.sendMQ(url, "queue2","队列2专属-消息6"));
    }

    /**
     * 测试发布订阅模式
     * @throws InterruptedException
     */
    private static void testSendToTopic() throws InterruptedException {
        //消息队列地址
        String url = "http://localhost:8080";
        System.out.println(PublisherUtil.sendTopic(url, "topic1","主题1专属-hello MQ-1,存活时间10秒","100000"));
        Thread.sleep(1000);
        System.out.println(PublisherUtil.sendTopic(url, "topic1","主题1专属-hello MQ-2,存活时间1秒","1000"));
        Thread.sleep(1000);
        System.out.println(PublisherUtil.sendTopic(url, "topic1","主题1专属-hello MQ-3,存活时间1秒","1000"));
        Thread.sleep(1000);
        System.out.println(PublisherUtil.sendTopic(url, "topic2","主题2专属-hello MQ-4,存活时间1秒","1000"));
        Thread.sleep(1000);
        System.out.println(PublisherUtil.sendTopic(url, "topic2","主题2专属-hello MQ-5,存活时间1秒","1000"));
        Thread.sleep(1000);
        System.out.println(PublisherUtil.sendTopic(url, "topic2","主题2专属-hello MQ-6,存活时间1秒","1000"));
        Thread.sleep(1000);
        System.out.println(PublisherUtil.sendTopic(url, "topic1","主题1专属-hello MQ-7,存活时间8秒","8000"));
        Thread.sleep(1000);
        System.out.println(PublisherUtil.sendTopic(url, "topic1","主题1专属-hello MQ-8,存活时间8秒","8000"));
        Thread.sleep(1000);
    }
}
