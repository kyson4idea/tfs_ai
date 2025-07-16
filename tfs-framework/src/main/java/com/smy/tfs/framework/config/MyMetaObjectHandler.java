package com.smy.tfs.framework.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.smy.tfs.common.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        String username = null;
        try {
            username = SecurityUtils.getAccountUserInfo();
        } catch (Exception e){
            //do nothing
        }
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "createBy", String.class, username);
        this.strictInsertFill(metaObject, "updateBy", String.class, username);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String username = null;
        try {
            username = SecurityUtils.getAccountUserInfo();
        } catch (Exception e){
            //do nothing
        }
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateBy", String.class, username);
    }
}
