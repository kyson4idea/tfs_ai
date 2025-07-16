package com.smy.tfs.biz.component;

import cn.hutool.core.util.StrUtil;
import com.smy.tfs.biz.service.NotificationService;
import com.smy.tfs.common.constant.CacheConstants;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

@Component
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Resource
    private NotificationService notificationService;
    @Resource
    private RedisLockRegistry redisLockRegistry;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        String prefix = CacheConstants.QW_CARD_MSG_CODE_KEY;
        if (expiredKey.startsWith(prefix)) {
            log.info("键过期：" + expiredKey);
            String[] split = expiredKey.replaceAll(prefix, "").split(StrUtil.COLON);
            if (split.length < 2) {
                log.error("QW_CARD_MSG_CODE_KEY is error, key = {}", expiredKey);
                return;
            }
            var responseCode = split[0];
            var userList = Arrays.asList(split[1].split(StrUtil.COMMA));
            log.info("expire card msg , responseCode = {} ,userList = {} ,", responseCode, split[1]);
            notificationService.disableCardButton(responseCode, "卡片消息已过期", userList);
        }
    }
}