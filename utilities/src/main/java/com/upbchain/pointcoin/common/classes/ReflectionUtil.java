package com.upbchain.pointcoin.common.classes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;


/**
 * @author kevin.wang.cy@gmail.com
 */
public class ReflectionUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtil.class);
    /**
     * Get static field value
     * @param classToReflect
     * @param fieldNameValueToFetch
     * @return
     */
    public static Object reflectValue(Class<?> classToReflect, String fieldNameValueToFetch) {
        try {
            Field reflectField  = reflectField(classToReflect, fieldNameValueToFetch);
            reflectField.setAccessible(true);
            Object reflectValue = reflectField.get(classToReflect);
            return reflectValue;
        } catch (Exception ex) {
            LOG.error("Failed to reflect "+fieldNameValueToFetch, ex);
        }

        return null;
    }

    /**
     * Get instance feild value
     * @param objToReflect
     * @param fieldNameValueToFetch
     * @return
     */
    public static Object reflectValue(Object objToReflect, String fieldNameValueToFetch) {
        try {
            Field reflectField  = reflectField(objToReflect.getClass(), fieldNameValueToFetch);
            Object reflectValue = reflectField.get(objToReflect);
            return reflectValue;
        } catch (Exception ex) {
            LOG.error("Failed to reflect "+fieldNameValueToFetch, ex);
        }
        return null;
    }

    /**
     * Get field
     * @param classToReflect
     * @param fieldNameValueToFetch
     * @return
     */
    public static Field reflectField(Class<?> classToReflect, String fieldNameValueToFetch) {
        try {
            Field reflectField = null;
            Class<?> classForReflect = classToReflect;
            do {
                try {
                    reflectField = classForReflect.getDeclaredField(fieldNameValueToFetch);
                } catch (NoSuchFieldException e) {
                    classForReflect = classForReflect.getSuperclass();
                }
            } while (reflectField==null || classForReflect==null);

            reflectField.setAccessible(true);
            return reflectField;
        } catch (Exception ex) {
            LOG.error("Failed to reflect " + fieldNameValueToFetch + " from "+ classToReflect, ex);
        }
        return null;
    }

    /**
     * Set feild value
     * @param objToReflect
     * @param fieldNameToSet
     * @param valueToSet
     */
    public static void refectSetValue(Object objToReflect, String fieldNameToSet, Object valueToSet) {
        try {
            Field reflectField  = reflectField(objToReflect.getClass(), fieldNameToSet);
            reflectField.set(objToReflect, valueToSet);
        } catch (Exception ex) {
            LOG.error("Failed to reflectively set "+ fieldNameToSet +"="+ valueToSet, ex);
        }
    }

}
