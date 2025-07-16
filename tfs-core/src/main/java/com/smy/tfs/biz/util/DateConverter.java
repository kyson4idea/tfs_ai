package com.smy.tfs.biz.util;

import org.apache.commons.beanutils.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateConverter implements Converter {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public <T> T convert(Class<T> type, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            try {
                return type.cast(DATE_FORMAT.parse((String) value));
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd HH:mm:ss", e);
            }
        }
        throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
    }
}
