package me.flyray.bsin.mq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单的RocketMQ测试
 * 不依赖Spring Boot上下文，直接使用RocketMQ原生API
 */
public class SimpleRocketMQTest {

    @Test
    public void testRocketMQConnection() throws Exception {
        System.out.println("🚀 开始测试RocketMQ连接...");
        
        // 创建生产者
        DefaultMQProducer producer = new DefaultMQProducer("test_producer_group");
        producer.setNamesrvAddr("127.0.0.1:9876");
        
        try {
            // 启动生产者
            producer.start();
            System.out.println("✅ RocketMQ Producer 启动成功");
            
            // 创建测试消息
            String topic = "waas-test";
            String messageBody = createTestMessage();
            
            Message message = new Message(topic, messageBody.getBytes(RemotingHelper.DEFAULT_CHARSET));
            
            // 发送消息
            SendResult sendResult = producer.send(message);
            
            if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
                System.out.println("✅ 消息发送成功!");
                System.out.println("📨 消息ID: " + sendResult.getMsgId());
                System.out.println("📨 消息内容: " + messageBody);
                System.out.println("📨 发送状态: " + sendResult.getSendStatus());
            } else {
                System.out.println("❌ 消息发送失败: " + sendResult.getSendStatus());
            }
            
        } catch (Exception e) {
            System.err.println("❌ RocketMQ测试失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭生产者
            producer.shutdown();
            System.out.println("🔚 RocketMQ Producer 已关闭");
        }
    }
    
    @Test
    public void testMultipleMessages() throws Exception {
        System.out.println("🚀 开始测试批量消息发送...");
        
        DefaultMQProducer producer = new DefaultMQProducer("test_batch_producer_group");
        producer.setNamesrvAddr("127.0.0.1:9876");
        
        try {
            producer.start();
            System.out.println("✅ 批量测试Producer启动成功");
            
            String topic = "waas-test";
            int messageCount = 5;
            
            for (int i = 1; i <= messageCount; i++) {
                String messageBody = createBatchTestMessage(i);
                Message message = new Message(topic, messageBody.getBytes(RemotingHelper.DEFAULT_CHARSET));
                
                SendResult sendResult = producer.send(message);
                
                if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
                    System.out.println("✅ 消息 " + i + " 发送成功: " + sendResult.getMsgId());
                } else {
                    System.out.println("❌ 消息 " + i + " 发送失败: " + sendResult.getSendStatus());
                }
                
                // 稍微延迟一下
                Thread.sleep(100);
            }
            
            System.out.println("🎉 批量消息测试完成，共发送 " + messageCount + " 条消息");
            
        } catch (Exception e) {
            System.err.println("❌ 批量消息测试失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            producer.shutdown();
            System.out.println("🔚 批量测试Producer已关闭");
        }
    }
    
    /**
     * 测试消息接收
     */
    @Test
    public void testMessageConsumption() throws Exception {
        System.out.println("🚀 开始测试消息接收...");
        
        // 创建消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test_consumer_group");
        consumer.setNamesrvAddr("127.0.0.1:9876");
        
        // 设置消息监听器
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger messageCount = new AtomicInteger(0);
        
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(
                    List<MessageExt> messages,
                    ConsumeConcurrentlyContext context) {
                
                for (MessageExt message : messages) {
                    try {
                        String messageBody = new String(message.getBody(), RemotingHelper.DEFAULT_CHARSET);
                        System.out.println("📥 接收到消息:");
                        System.out.println("   📨 消息ID: " + message.getMsgId());
                        System.out.println("   📨 主题: " + message.getTopic());
                        System.out.println("   📨 标签: " + message.getTags());
                        System.out.println("   📨 内容: " + messageBody);
                        System.out.println("   📨 队列ID: " + message.getQueueId());
                        System.out.println("   📨 重试次数: " + message.getReconsumeTimes());
                        
                        int count = messageCount.incrementAndGet();
                        System.out.println("✅ 消息处理成功，已处理 " + count + " 条消息");
                        
                        // 如果收到测试消息，则完成测试
                        if (messageBody.contains("TEST_MESSAGE") || messageBody.contains("BATCH_TEST_MESSAGE")) {
                            latch.countDown();
                        }
                        
                    } catch (Exception e) {
                        System.err.println("❌ 消息处理失败: " + e.getMessage());
                        e.printStackTrace();
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                }
                
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        
        try {
            // 订阅主题
            consumer.subscribe("waas-test", "*");
            
            // 启动消费者
            consumer.start();
            System.out.println("✅ 消费者启动成功，等待消息...");
            
            // 等待消息（最多等待30秒）
            boolean received = latch.await(30, TimeUnit.SECONDS);
            
            if (received) {
                System.out.println("🎉 消息接收测试成功！");
                System.out.println("📊 总共处理了 " + messageCount.get() + " 条消息");
            } else {
                System.out.println("⏰ 消息接收测试超时，未收到测试消息");
                System.out.println("💡 提示：请先运行发送消息的测试，然后再运行此测试");
            }
            
        } catch (Exception e) {
            System.err.println("❌ 消息接收测试失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭消费者
            consumer.shutdown();
            System.out.println("🔚 消费者已关闭");
        }
    }
    
    /**
     * 测试发送和接收的完整流程
     */
    @Test
    public void testSendAndReceiveFlow() throws Exception {
        System.out.println("🚀 开始测试发送和接收完整流程...");
        
        // 1. 先启动消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test_flow_consumer_group");
        consumer.setNamesrvAddr("127.0.0.1:9876");
        
        final CountDownLatch consumerLatch = new CountDownLatch(1);
        final AtomicInteger receivedCount = new AtomicInteger(0);
        
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(
                    List<MessageExt> messages,
                    ConsumeConcurrentlyContext context) {
                
                for (MessageExt message : messages) {
                    String messageBody = null;
                    try {
                        messageBody = new String(message.getBody(), RemotingHelper.DEFAULT_CHARSET);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("📥 消费者接收到消息: " + messageBody);
                    
                    int count = receivedCount.incrementAndGet();
                    System.out.println("✅ 已接收 " + count + " 条消息");
                    
                    // 收到消息后完成测试
                    consumerLatch.countDown();
                }
                
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        
        try {
            consumer.subscribe("waas-test", "*");
            consumer.start();
            System.out.println("✅ 消费者启动成功");
            
            // 等待消费者启动
            Thread.sleep(2000);
            
            // 2. 发送测试消息
            DefaultMQProducer producer = new DefaultMQProducer("test_flow_producer_group");
            producer.setNamesrvAddr("127.0.0.1:9876");
            producer.start();
            System.out.println("✅ 生产者启动成功");
            
            String testMessage = createFlowTestMessage();
            Message message = new Message("waas-test", testMessage.getBytes(RemotingHelper.DEFAULT_CHARSET));
            
            SendResult sendResult = producer.send(message);
            System.out.println("📤 发送消息: " + testMessage);
            System.out.println("📤 发送结果: " + sendResult.getSendStatus());
            
            // 3. 等待消费者接收消息
            boolean received = consumerLatch.await(10, TimeUnit.SECONDS);
            
            if (received) {
                System.out.println("🎉 发送和接收流程测试成功！");
                System.out.println("📊 发送了 1 条消息，接收了 " + receivedCount.get() + " 条消息");
            } else {
                System.out.println("❌ 发送和接收流程测试失败：未收到消息");
            }
            
            producer.shutdown();
            
        } catch (Exception e) {
            System.err.println("❌ 发送和接收流程测试失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            consumer.shutdown();
            System.out.println("🔚 测试完成，所有组件已关闭");
        }
    }
    
    private String createTestMessage() {
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("eventCode", "TEST_MESSAGE");
        messageData.put("timestamp", System.currentTimeMillis());
        messageData.put("message", "Hello RocketMQ from Bsin-PaaS!");
        messageData.put("source", "SimpleRocketMQTest");
        
        return "{\n" +
                "  \"eventCode\": \"" + messageData.get("eventCode") + "\",\n" +
                "  \"timestamp\": " + messageData.get("timestamp") + ",\n" +
                "  \"message\": \"" + messageData.get("message") + "\",\n" +
                "  \"source\": \"" + messageData.get("source") + "\"\n" +
                "}";
    }
    
    private String createBatchTestMessage(int index) {
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("eventCode", "BATCH_TEST_MESSAGE");
        messageData.put("index", index);
        messageData.put("timestamp", System.currentTimeMillis());
        messageData.put("message", "批量测试消息 #" + index);
        messageData.put("source", "SimpleRocketMQTest");
        
        return "{\n" +
                "  \"eventCode\": \"" + messageData.get("eventCode") + "\",\n" +
                "  \"index\": " + messageData.get("index") + ",\n" +
                "  \"timestamp\": " + messageData.get("timestamp") + ",\n" +
                "  \"message\": \"" + messageData.get("message") + "\",\n" +
                "  \"source\": \"" + messageData.get("source") + "\"\n" +
                "}";
    }
    
    private String createFlowTestMessage() {
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("eventCode", "FLOW_TEST_MESSAGE");
        messageData.put("timestamp", System.currentTimeMillis());
        messageData.put("message", "发送和接收流程测试消息");
        messageData.put("source", "SimpleRocketMQTest");
        messageData.put("testId", "flow-test-" + System.currentTimeMillis());
        
        return "{\n" +
                "  \"eventCode\": \"" + messageData.get("eventCode") + "\",\n" +
                "  \"timestamp\": " + messageData.get("timestamp") + ",\n" +
                "  \"message\": \"" + messageData.get("message") + "\",\n" +
                "  \"source\": \"" + messageData.get("source") + "\",\n" +
                "  \"testId\": \"" + messageData.get("testId") + "\"\n" +
                "}";
    }
}
