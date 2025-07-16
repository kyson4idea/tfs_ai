package com.smy.tfs.biz.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.smy.tfs.api.dbo.TicketAccount;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.service.ITicketAccountService;
import com.smy.tfs.common.constant.CacheConstants;
import com.smy.tfs.common.core.redis.RedisCache;
import com.smy.tfs.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AccountReturnComponent {
    @Resource
    private RedisCache redisCache;
    @Resource
    private ITicketAccountService ticketAccountService;

    public String toAccountInfoStrForFront(String accountListStr) {
        try {
            List<AccountInfo> accountInfoList = AccountInfo.ToAccountInfoList(accountListStr);
            if (CollUtil.isEmpty(accountInfoList)){
                return "";
            }

            List<String> userList = accountInfoList.stream()
                    .map(accountInfo -> {
                        String cacheRedisKey = CacheConstants.TFS_TICKET_ACCOUNT_TYPE_NAME + accountInfo.getAccountType();
                        String accountName = redisCache.getCacheObject(cacheRedisKey);

                        //缓存没有，查数据库，然后放缓存
                        if (accountName == null){
                            TicketAccount ticketAccount = ticketAccountService.lambdaQuery()
                                    .eq(TicketAccount::getTicketAccountType, accountInfo.getAccountType())
                                    .last("limit 1")
                                    .one();
                            if (ticketAccount != null && StrUtil.isNotBlank(ticketAccount.getTicketAccountName())){
                                accountName = ticketAccount.getTicketAccountName();
                            } else {
                                accountName = "";
                            }
                            //不管有么有查到，都要放值
                            redisCache.setCacheObject(cacheRedisKey, accountName);
                        }
                        return accountName + "-" + accountInfo.getAccountName();
                    }).collect(Collectors.toList());
            return CollUtil.join(userList, ";");
        } catch (Exception e){
            log.error("转换用户信息到前端展示失败，转换内容：{}", accountListStr);
            return "";
        }
    }

    public String getAccountName(String accountListStr) {
        try {
            if (StringUtils.isEmpty(accountListStr)) {
                return "";
            }
            List<AccountInfo> accountInfoList = AccountInfo.ToAccountInfoList(accountListStr);
            if (CollUtil.isEmpty(accountInfoList)){
                return "";
            }
            List<String> userList = accountInfoList.stream()
                    .map(accountInfo -> accountInfo.getAccountName())
                    .collect(Collectors.toList());
            return CollUtil.join(userList, ";");
        } catch (Exception e){
            log.error("转换用户信息到前端展示失败，转换内容：{}", accountListStr);
            return "";
        }
    }
}
