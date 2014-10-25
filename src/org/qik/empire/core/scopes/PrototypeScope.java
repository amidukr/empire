package org.qik.empire.core.scopes;

import org.qik.empire.core.ServiceContainer;

/**
* Created by qik on 16.10.2014.
*/
public class PrototypeScope implements ServiceContainer.Scope {

    @Override
    public <T> T get(Class<T> clazz) {
        return null;
    }

    @Override
    public <T> void put(Class<T> clazz, T service) {}
}
