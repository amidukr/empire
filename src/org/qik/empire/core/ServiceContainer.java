package org.qik.empire.core;

import org.qik.empire.core.scopes.PrototypeScope;
import org.qik.empire.core.scopes.SingletonScope;
import org.qik.empire.utils.ReflectionUtils;

import static org.qik.empire.utils.Lambda.asLambda;
import static org.qik.empire.utils.ReflectionUtils.createInstance;
import static org.qik.empire.utils.ReflectionUtils.getXFields;

/**
 * Created by qik on 16.10.2014.
 */
public class ServiceContainer {

    private final Scope PROTOTYPE = new PrototypeScope();
    private final Scope SINGLETON = new SingletonScope();

    private  <T> T getService(Class<T> clazz, Scope scope) {
        T service = scope.get(clazz);

        if(service == null) {
            service = createInstance(clazz);
            scope.put(clazz, service);
            doInjects(service);
        }

        return service;
    }

    public <T> T getService(Class<T> clazz) {
        return getService(clazz, clazz.isAnnotationPresent(Prototype.class) ? PROTOTYPE : SINGLETON);
    }

    private void doInjects(Object service) {
        asLambda(getXFields(service))
                .filter (field -> field.isAnnotationPresent(Inject.class))
                .forEach(field -> field.setFieldValue(getService(field.getType())));


        asLambda(
            ReflectionUtils.getXMethods(service))
                .filter( method -> method.isAnnotationPresent(Inject.class))
                .forEach(method -> {

                    Object[] injectsArguments = asLambda(method.getParameterTypes())
                            .map(type -> (Object) getService(type))
                            .toArray(Object.class);

                    method.invoke(injectsArguments);
                });
    }


    public interface Scope {
        <T> T get(Class<T> clazz);
        <T> void put(Class<T>  clazz, T service);
    }
}
