package com.smy.tfs.biz.mq.producer;


import cn.hutool.core.util.StrUtil;
import com.smy.framework.core.config.Property;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class TfsMqProducer {

    private DefaultMQProducer producer = null;

    /**
     * 初始化消息生产者
     */
    @PostConstruct
    private void init() {
        try {
            producer = new DefaultMQProducer("tfs-mq-producer");
            producer.setNamesrvAddr(Property.getProperty("new-namesrvAddr"));
            producer.start();
        } catch (Exception ex) {
            throw new RuntimeException("tfs-mq-producer 生产者启动失败", ex);
        }
    }

    public Boolean sendMsg(String topic, String tag, String key, String content) {
        if (StrUtil.hasBlank(topic, tag, key, content)) {
            log.error("发送业务审批结果mq消息topic:{},tags:{},key:{},content:{} 失败，参数不全", topic, tag, key, content);
            return false;
        }
        try {
            // 1.生成消息唯一key
            log.info("开始发送业务审批结果消息topic:{},tags:{},key:{},content:{}", topic, tag, key, content);
            Message message = new Message(topic, tag, key, content.getBytes(StandardCharsets.UTF_8));
            SendResult sendResult = producer.send(message);
            log.info("发送业务审批结果消息:{} 结果 is {}", key, sendResult);
            return sendResult != null && SendStatus.SEND_OK.equals(sendResult.getSendStatus());
        } catch (Exception e) {
            log.error("发送业务审批结果消息:{} ,失败 ", key, e);
            return false;
        }
    }

}
