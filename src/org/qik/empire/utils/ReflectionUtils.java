package org.qik.empire.utils;

import org.qik.empire.core.Inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.qik.empire.utils.Lambda.asLambda;

/**
 * Created by qik on 25.10.2014.
 */
public class ReflectionUtils {

    public static <T> T createInstance(Class<T> clazz){
        return createInstance(clazz, false);
    }

    public static <T> T createInstance(Class<T> clazz, boolean allowPrivate){
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();

            if(allowPrivate){
                constructor.setAccessible(true);
            }

            return constructor.newInstance();
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invokeMethod(Method method, Object target, Object[] arguments) {
        try {
            return method.invoke(target, arguments);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] getFieldName(Class<?> target){
        return asLambda(getFields(target))
                   .map(field -> field.getName())
               .toArray(String.class);
    }

    public static Collection<Field> getFields(Class<?> target){
        List<Field> result = new ArrayList<>();

        while (target != null) {
            result.addAll(Arrays.asList(target.getDeclaredFields()));
            target = target.getSuperclass();
        }

        return result;
    }

    public static XField[] getXFields(Object target){
        Collection<Field> fields = getFields(target.getClass());

        return asLambda(fields)
                .map(field -> new XField(target, field))
              .toArray(XField.class);
    }

    public static XMethod[] getXMethods(Object target) {
        return  asLambda(    target.getClass().getMethods())
                                .map(method -> new XMethod(target, method))
                             .toArray(XMethod.class);
    }

    public static class XField{
        private final Object target;
        private final Field field;


        public XField(Object target, Field field) {
            this.target = target;
            this.field = field;
        }

        public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass)     { return field.isAnnotationPresent(annotationClass); }
        public Class<?> getType()                                                           { return field.getType(); }
        public String   getName()                                                           { return field.getName(); }


        public void setFieldValue(Object value) {
            try {
                field.setAccessible(true);
                field.set(target, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        public Object getValue() {
            try {
                field.setAccessible(true);
                return field.get(target);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class XMethod{
        private final Object target;
        private final Method method;

        public XMethod(Object target, Method method) {
            this.target = target;
            this.method = method;
        }

        public Object      invoke(Object ... arguments)                         { return invokeMethod(method, target, arguments); }
        public boolean     isAnnotationPresent(Class<Inject> annotationClass)   { return method.isAnnotationPresent(annotationClass); }
        public Class<?>[]  getParameterTypes()                                  { return method.getParameterTypes(); }
    }
}
