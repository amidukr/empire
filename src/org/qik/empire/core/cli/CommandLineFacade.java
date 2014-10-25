package org.qik.empire.core.cli;

import org.qik.empire.core.EntryPointsContainer;

import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static org.qik.empire.core.EntryPointsContainer.EntryPoint;
import static org.qik.empire.core.EntryPointsContainer.ReturnType.STRING;
import static org.qik.empire.utils.Utils.defaultValue;
import static org.qik.empire.utils.Utils.toStr;


public class CommandLineFacade {
    private final EntryPointsContainer entryPointsContainer;

    public CommandLineFacade(EntryPointsContainer entryPointsContainer) {
        this.entryPointsContainer = entryPointsContainer;
    }

    public Object execute(String command) {
        String[] commands = command.split(" ");
        String commandName = commands[0];
        String[] arguments = new String[commands.length - 1];

        arraycopy(commands, 1, arguments, 0, arguments.length);

        EntryPoint entryPoint = entryPointsContainer.getEntryPoint(commandName);

        if(entryPoint == null) return format(":No such command: %s", commandName);

        String defaultValue = entryPoint.getReturnType() == STRING ? ":Null": ":Done";

        return defaultValue(toStr(entryPoint.invoke(arguments)), defaultValue);
    }
}
