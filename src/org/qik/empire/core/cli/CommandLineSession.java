package org.qik.empire.core.cli;

import org.qik.empire.core.ServiceContainer;
import org.qik.empire.core.SessionContainer;
import org.qik.empire.utils.Utils;

import java.io.Closeable;

import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static org.qik.empire.core.ServiceContainer.EntryPoint;
import static org.qik.empire.core.ServiceContainer.ReturnType.STRING;
import static org.qik.empire.core.ServiceContainer.ReturnType.VOID;
import static org.qik.empire.utils.Utils.defaultValue;
import static org.qik.empire.utils.Utils.toStr;


public class CommandLineSession implements Closeable{
    private final ServiceContainer serviceContainer;
    private final SessionContainer.Session session;

    public CommandLineSession(ServiceContainer serviceContainer) {
        session = serviceContainer.sessions().openSession();
        this.serviceContainer = serviceContainer;
    }

    public Object execute(String command) {
        String[] commands = command.split(" ");
        String commandName = commands[0];
        String[] arguments = new String[commands.length - 1];

        arraycopy(commands, 1, arguments, 0, arguments.length);

        EntryPoint entryPoint = serviceContainer.getEntryPoint(commandName);

        if(entryPoint == null) return format(":No such command: %s", commandName);

        String defaultValue = entryPoint.getReturnType() == STRING ? ":Null": ":Done";

        return defaultValue(toStr(entryPoint.invoke(session, arguments)), defaultValue);
    }

    public void close(){
        serviceContainer.sessions().closeSession(session);
    }
}
