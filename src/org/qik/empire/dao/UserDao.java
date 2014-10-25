package org.qik.empire.dao;

import static org.qik.empire.service.UserService.User;
import static org.qik.empire.storage.Storage.Schema;

/**
 * Created by qik on 16.10.2014.
 */
public class UserDao extends EntityDao<User> {

    public UserDao(){
        super(User.class, "userGroup");
    }

    @Override
    protected Schema groupSchema() {
        return schemaByClass();
    }

    @Override
    public String entityID(User user) {
        return user.getName();
    }
}
