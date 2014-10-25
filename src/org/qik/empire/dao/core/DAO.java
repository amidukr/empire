package org.qik.empire.dao.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qik on 16.10.2014.
 */
public abstract class DAO<T extends Cloneable> {
    private final Map<String, T> byName = new HashMap<>();

    public T getByName(String name){
        return byName.get(name);
    }

    public void add(T entity){
        byName.put(getName(entity), entity);
    }

    protected abstract String getName(T entity);
}
