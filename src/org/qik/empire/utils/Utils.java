package org.qik.empire.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Spliterators.spliterator;
import static java.util.stream.StreamSupport.stream;

/**
 * Created by Dmytro_Brazhnyk on 23.06.2014.
 */
public final class Utils {


    public static boolean isEmpty(String empty) {
        return empty == null || empty.trim().isEmpty();
    }

    public static String defaultValue(String value, String defaultValue) {
        return isEmpty(value) ? defaultValue : value;
    }

    public static String toStr(Object value) {
        return value == null ? null : value.toString();
    }

    public static  <T extends Annotation> Stream<Method> getAnnotatedMethods(Class<?> clazz, Class<T> annotationClazz) {
        return asStream(clazz.getMethods()).filter(x -> x.isAnnotationPresent(annotationClazz));
    }


    public static <T> Stream<T> asStream(T ... array){
        return stream(spliterator(array, 0), false);
    }


    public static <T> T createInstance(Class<T> clazz){
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invokeMethod(Method method, Object target, Object... arguments) {
        try {
            return method.invoke(target, arguments);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
