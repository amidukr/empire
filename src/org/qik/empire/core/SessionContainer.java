package org.qik.empire.core;

import org.qik.empire.context.SessionContext;

import java.util.Map;

/**
 * Created by qik on 05.10.2014.
 */
public class SessionContainer {


    public Session openSession() {
        return new Session();
    }

    public void closeSession(Session session) {
        if(session.sessionContext == null) throw new IllegalArgumentException("Session is no more active");

        session.sessionContext = null;
    }

    public static void setCurrent(SessionContext sessionContext) {

    }

    public class Session {
        private SessionContext sessionContext = new SessionContext();

        public SessionContext getSessionContext() {
            if(sessionContext == null) throw new IllegalArgumentException("Session is no more active");

            return sessionContext;
        }
    };
}
