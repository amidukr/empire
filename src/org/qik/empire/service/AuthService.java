package org.qik.empire.service;

import org.qik.empire.core.Service;
import org.qik.empire.core.Command;

import static org.qik.empire.context.SessionContext.currentSession;

/**
 * Created by qik on 05.10.2014.
 */
public class AuthService implements Service {

    @Command
    public void login(String username) {
        currentSession().put(String.class, "username", username);
    }

    @Command("getuser")
    public String getUser(){
        return currentSession().get(String.class, "username");
    }
}
