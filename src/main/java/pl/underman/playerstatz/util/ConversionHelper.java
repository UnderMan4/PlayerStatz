package pl.underman.playerstatz.util;

import java.lang.reflect.Field;
import java.util.Map;

public abstract class ConversionHelper {

    public static <T> T mapToClass(Map<String, Object> map, Class<T> clazz) throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            Logger.debug("ConversionHelper.mapToClass: " + field.getName());
            Logger.debug("ConversionHelper.mapToClass: " + field.getType().getName());
            Logger.debug("ConversionHelper.mapToClass: " + map.get(field.getName()).getClass().getName());
            field.setAccessible(true);
            field.set(instance, map.get(field.getName()));
        }
        return instance;
    }

}
