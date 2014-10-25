package org.qik.empire.core.scopes;

import org.qik.empire.core.ServiceContainer;

import java.util.HashMap;
import java.util.Map;

/**
* Created by qik on 16.10.2014.
*/
public class SingletonScope implements ServiceContainer.Scope {

    private final Map<Class<?>, Object> services = new HashMap<>();

    @Override
    public <T> T get(Class<T> clazz) {
        return clazz.cast(services.get(clazz));
    }

    @Override
    public <T> void put(Class<T> clazz, T service) {
        services.put(clazz, service);
    }
}
