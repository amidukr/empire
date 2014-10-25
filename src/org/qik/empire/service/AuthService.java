package org.qik.empire.service;

import org.qik.empire.core.Command;
import org.qik.empire.core.Inject;
import org.qik.empire.core.Service;

import static org.qik.empire.context.SessionContext.currentSession;

/**
 * Created by qik on 05.10.2014.
 */
public class AuthService implements Service {

    @Inject
    private UserService userService;

    @Command
    public boolean login(String username) {
        if(userService.getUser(username) == null) return false;

        currentSession().put(String.class, "username", username);
        return true;
    }

    @Command("getuser")
    public String getUser(){
        return currentSession().get(String.class, "username");
    }

    public void logout() {
        currentSession().put(String.class, "username", null);
    }
}
