package cn.jiateng.api.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MapUtil {

    public static Map<String, Object> obj2Map(Object obj) throws IllegalAccessException {
        Map<String, Object> res = new HashMap<>();
        Field[] allFields = obj.getClass().getDeclaredFields();
        for (Field field : allFields) {
            field.setAccessible(true);
            Object value = field.get(obj);
            res.put(field.getName(), value);
        }
        return res;
    }
}
