package com.smy.tfs.common.utils.bean;


import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 判断数各种据类型是否为空
 */
public class ObjectHelper {


    /**
     * 判断是否为空
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof Optional) {
            return !((Optional<?>) obj).isPresent();
        } else if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        } else if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        } else {
            return false;
        }
    }

    /**
     * 判断是否不为空
     *
     * @param obj
     * @return
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 判断任何一个对象Object是否为空
     *
     * @param objs
     * @return
     */
    public static boolean anyIsEmpty(Object... objs) {
        for (Object obj : objs) {
            if (isEmpty(obj)) return true;
        }
        return false;
    }

    /**
     * 获取第一条数据
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T mapOne(List<T> list) {
        return isEmpty(list) ? null : list.stream().findFirst().orElse(null);
    }

    /**
     * 获取最后一条数据
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T lastOne(List<T> list) {
        return isEmpty(list) ? null : list.get(list.size() - 1);
    }

}
