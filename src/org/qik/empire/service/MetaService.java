package org.qik.empire.service;

import org.qik.empire.core.Command;
import org.qik.empire.core.EntryPointsContainer;
import org.qik.empire.core.Inject;
import org.qik.empire.core.Service;

/**
 * Created by qik on 05.10.2014.
 */
public class MetaService implements Service {
    private EntryPointsContainer entryPointsContainer;
    private AuthService         authService;


    @Inject
    public void inject(EntryPointsContainer entryPointsContainer,
                       AuthService authService) {
        this.entryPointsContainer = entryPointsContainer;
        this.authService      = authService;
    }

    @Command("commands")
    public String help() {
        return entryPointsContainer.getCommandList().toString();
    }

    @Command
    public String describe(String command) {
        if(!"admin".equals(authService.getUser())){
            return "Not enough permissions";
        }

        EntryPointsContainer.EntryPoint ep = entryPointsContainer.getEntryPoint(command);

        return String.format("%s::%s -> to %s", ep.getCommandName(),
                                                ep.getReturnType(),
                                                ep.getMethod());
    }
}
