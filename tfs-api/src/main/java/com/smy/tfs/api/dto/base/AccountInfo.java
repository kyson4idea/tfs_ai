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
import java.util.stream.Collectors;

//JSONString到数据库的存储对象，谨慎添加字段
@Data
@Slf4j
public class AccountInfo implements Serializable {


    private static final long serialVersionUID = 7764750346275436590L;
    private String accountType;
    private String accountId;
    private String accountName;
    private String sameOriginId;

    /**
     * 企业微信ID，抄送的时候使用
     */
    //private String qywxId;
    public AccountInfo() {
    }

    public AccountInfo(String sameOriginId, String accountType, String accountId, String accountName) {
        this.accountType = accountType;
        this.accountId = accountId;
        this.accountName = accountName;
        this.sameOriginId = sameOriginId;
    }

    public String ToJsonString() {
        return JSONUtil.toJsonStr(this);
    }

    public String ToJsonArrayString() {
        return JSONUtil.toJsonStr(CollUtil.newArrayList(this));
    }

    public static List<AccountInfo> ToAccountInfoList(String... accountListStrArgs) {
        List<AccountInfo> accountInfoList = new ArrayList<>();
        for (String accountListStrArg : accountListStrArgs) {
            if (JSONUtil.isJsonArray(accountListStrArg)) {
                List<AccountInfo> tempList = JSON.parseArray(accountListStrArg, AccountInfo.class);
                if (CollUtil.isNotEmpty(tempList)) {
                    accountInfoList.addAll(tempList);
                }
            } else if (StrUtil.isNotBlank(accountListStrArg)) {
                AccountInfo accountInfo = JSON.parseObject(accountListStrArg, AccountInfo.class);
                accountInfoList.add(accountInfo);
            }
        }
        return Distinct(accountInfoList);
    }



    public static List<AccountInfo> Distinct(List<AccountInfo> list){
        Map<String,AccountInfo> thisMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(list)){
            for(AccountInfo accountInfo : list){
                thisMap.putIfAbsent(accountInfo.getAccountId() + "_" + accountInfo.getAccountType(),accountInfo);
            }
        }
        return new ArrayList<>(thisMap.values());
    }

    public static List<AccountInfo> ToAccountInfoList(List<String> accountStrList) {
        List<AccountInfo> accountInfoList = new ArrayList<>();

        for (String accountStr : accountStrList) {
            if (JSONUtil.isJsonArray(accountStr)) {
                List<AccountInfo> tempList = JSON.parseArray(accountStr, AccountInfo.class);
                if (CollUtil.isNotEmpty(tempList)) {
                    accountInfoList.addAll(tempList);
                }
            } else if (StrUtil.isNotBlank(accountStr)) {
                AccountInfo accountInfo = JSONUtil.toBean(accountStr, AccountInfo.class);
                accountInfoList.add(accountInfo);
            }
        }
        return Distinct(accountInfoList);
    }



    /**
     * 解析用户组信息  json数组 -》工号-姓名（列表）
     *
     * @param accountListStr
     * @return
     */
    public static List<String> parseAccountInfoStrToUserList(String accountListStr) {
        List<AccountInfo> accountInfoList = ToAccountInfoList(accountListStr);
        return accountInfoList.stream().map(accountInfo -> accountInfo.getAccountId() + "-" + accountInfo.getAccountName())
                .collect(Collectors.toList());
    }

    public static String ToAccountInfoListStr(List<AccountInfo> accountInfoList) {
        return JSONUtil.toJsonStr(accountInfoList);
    }

    public static AccountInfo ToAccountInfo(String accountInfoStr) {
        return JSON.parseObject(accountInfoStr, AccountInfo.class);
    }

}