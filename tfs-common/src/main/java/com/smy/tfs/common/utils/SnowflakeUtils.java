package com.smy.tfs.common.utils;

import cn.hutool.core.lang.Snowflake;

public class SnowflakeUtils {
    /**
     * 使用hutool工具获取一个雪花id
     * @return
     */
    public static String getSnowflakeId() {
        Snowflake snowflake = new Snowflake();
        return snowflake.nextIdStr(); // 生成一个long类型的ID
    }
}
