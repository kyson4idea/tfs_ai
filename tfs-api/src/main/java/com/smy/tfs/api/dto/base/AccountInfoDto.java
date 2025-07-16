package com.smy.tfs.api.dto.base;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class AccountInfoDto implements Serializable {


    private static final long serialVersionUID = 8604370631243487634L;
    private String accountType;
    private String accountId;
    private String sameOriginId;
    private String accountName;

    /**
     * 企业微信ID，抄送的时候使用
     */
    private String qywxId;

    public AccountInfoDto() {

    }

    public AccountInfoDto(String sameOriginId, String accountType, String accountId, String accountName, String qywxId) {
        this.accountType = accountType;
        this.accountId = accountId;
        this.sameOriginId = sameOriginId;
        this.accountName = accountName;
        this.qywxId = qywxId;
    }

    public AccountInfo ToAccountInfo(){
        return new AccountInfo(this.sameOriginId, this.accountType,this.accountId,this.accountName);
    }

    public static AccountInfoDto ToAccountInfo(String accountInfoDtoStr) {
        return JSON.parseObject(accountInfoDtoStr, AccountInfoDto.class);
    }

    public static List<AccountInfoDto> ToAccountInfoDtoList(String... accountInfoDtoListStrArgs) {
        List<AccountInfoDto> accountInfoDtoList = new ArrayList<>();
        for (String accountInfoDtoStrArgs : accountInfoDtoListStrArgs) {
            if (JSONUtil.isJsonArray(accountInfoDtoStrArgs)) {
                List<AccountInfoDto> tempList = JSON.parseArray(accountInfoDtoStrArgs, AccountInfoDto.class);
                if (CollUtil.isNotEmpty(tempList)) {
                    accountInfoDtoList.addAll(tempList);
                }
            } else if (StrUtil.isNotBlank(accountInfoDtoStrArgs)) {
                AccountInfoDto accountInfoDto = JSON.parseObject(accountInfoDtoStrArgs, AccountInfoDto.class);
                accountInfoDtoList.add(accountInfoDto);
            }
        }
        return distinct(accountInfoDtoList);
    }



    public static List<AccountInfoDto> distinct(List<AccountInfoDto> list){
        Map<String,AccountInfoDto> thisMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(list)){
            for(AccountInfoDto accountInfoDto : list){
                thisMap.putIfAbsent(accountInfoDto.getAccountId() + "_" + accountInfoDto.getAccountType(),accountInfoDto);
            }
        }
        return new ArrayList<>(thisMap.values());
    }
}
