package org.qik.empire.service;

import org.qik.empire.context.SessionContext;
import org.qik.empire.core.*;

import java.util.ArrayList;
import java.util.List;

import static org.qik.empire.context.SessionContext.currentSession;

/**
 * Created by qik on 05.10.2014.
 */
public class MetaService implements Service {
    private ServiceContainer    serviceContainer;
    private AuthService         authService;


    @Inject
    public void inject(ServiceContainer serviceContainer,
                       AuthService authService) {
        this.serviceContainer = serviceContainer;
        this.authService      = authService;
    }

    @Command
    public String help() {
        return serviceContainer.getCommandList().toString();
    }

    @Command
    public String describe(String command) {
        if(!"admin".equals(authService.getUser())){
            return "Not enough permissions";
        }

        ServiceContainer.EntryPoint ep = serviceContainer.getEntryPoint(command);

        return String.format("%s::%s -> to %s", ep.getCommandName(),
                                                ep.getReturnType(),
                                                ep.getMethod());
    }
}
