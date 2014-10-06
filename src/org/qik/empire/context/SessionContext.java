package org.qik.empire.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qik on 05.10.2014.
 */
public class SessionContext {

    private final static ThreadLocal<SessionContext> CURRENT_CONTEXT = new ThreadLocal<>();

    private final Map<String, Object> variables = new HashMap<>();


    public  <T> void put(Class<T> clazz, String key, T value) {
        variables.put(clazz.getName() + ":" + key, value);
    }

    public <T> T get(Class<T> clazz, String key) {
        return clazz.cast(variables.get(clazz.getName() + ":" + key));
    }





    public static SessionContext currentSession() {
        SessionContext sessionContext = CURRENT_CONTEXT.get();

        if(sessionContext == null) throw new IllegalStateException("Session context isn't set");

        return sessionContext;
    }


    public static void setCurrentContext(SessionContext sessionContext) {
        if(sessionContext == null) throw new IllegalArgumentException("sessionContext cannot be null");

        CURRENT_CONTEXT.set(sessionContext);
    }

    public static void releaseCurrentContext() {
        CURRENT_CONTEXT.remove();
    }
}
