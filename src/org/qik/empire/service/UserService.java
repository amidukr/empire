package org.qik.empire.service;

import org.qik.empire.core.Inject;
import org.qik.empire.dao.UserDao;

/**
 * Created by qik on 23.10.2014.
 */
public class UserService {

    @Inject
    private UserDao userDao;

    @Inject
    private AuthService authService;

    public void register(String userName, String empireName) {
        authService.logout();

        userDao.insert(new User(userName, empireName));

        authService.login(userName);
    }

    public User getCurrentUser(){
        return getUser(authService.getUser());
    }

    public User getUser(String username) {
        return userDao.get(username);
    }

    public static class User{
        private String name;
        private String empireName;

        private User() {}

        public User(String name, String empireName) {
            this.name = name;
            this.empireName = empireName;
        }

        public String getName() {
            return name;
        }

        public String getEmpireName() {
            return empireName;
        }
    }
}
