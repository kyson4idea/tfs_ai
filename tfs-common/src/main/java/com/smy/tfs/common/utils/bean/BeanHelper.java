package com.smy.tfs.common.utils.bean;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 对象赋值工具类
 */
@Slf4j
public class BeanHelper {

    /**
     * 赋值对象
     *
     * @param source
     * @param targetClass
     * @param consumer
     * @param ignoreProperties
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> R copyObject(T source, Class<R> targetClass, BiConsumer<T, R> consumer, String... ignoreProperties) {
        if (source == null) {
            return null;
        }
        R target = null;
        try {
            target = targetClass.newInstance();
            BeanUtils.copyProperties(source, target, ignoreProperties);
            if (consumer != null) {
                consumer.accept(source, target);
            }
        } catch (Exception e) {
            log.error("copyObject error:{}", e);
        }
        return target;
    }

    /**
     * 赋值对象
     *
     * @param source
     * @param targetClass
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> R copyObject(T source, Class<R> targetClass) {
        return copyObject(source, targetClass, null);
    }

    /**
     * 赋值列表
     *
     * @param sourceList
     * @param targetClass
     * @param consumer
     * @param ignoreProperties
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> copyList(List<T> sourceList, Class<R> targetClass, BiConsumer<T, R> consumer, String... ignoreProperties) {
        if (ObjectHelper.isEmpty(sourceList)) {
            return new ArrayList<>();
        }
        List<R> targetList = new ArrayList<>(sourceList.size());
        sourceList.stream().map(source -> {
            R target = null;
            try {
                target = targetClass.newInstance();
                BeanUtils.copyProperties(source, target, ignoreProperties);
                if (consumer != null) {
                    consumer.accept(source, target);
                }
            } catch (Exception e) {
                log.error("copyList error:{}", e);
            }
            return target;
        }).forEach(targetList::add);
        return targetList;
    }

    /**
     * 赋值列表
     *
     * @param sourceList
     * @param targetClass
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> copyList(List<T> sourceList, Class<R> targetClass) {
        return copyList(sourceList, targetClass, null, null);
    }

    /**
     * 将map的键值对赋值给对象
     *
     * @param obj
     * @param map
     * @throws Exception
     */
    public static void mapProperties(Map<String, String> map, Object obj) throws Exception {
        Class<?> clazz = obj.getClass();
        for (String key : map.keySet()) {
            //对于某些多的字段直接忽略掉
            try {
                Method setter = clazz.getMethod("set" + key.substring(0, 1).toUpperCase() + key.substring(1), String.class);
                setter.invoke(obj, map.get(key));
            } catch (Exception e){
                //需要知道哪些字段需要 再放开注释
//                log.error("mapProperties error:{}", e);
            }
        }
    }


}
