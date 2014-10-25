package org.qik.empire.core.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * Created by qik on 08.10.2014.
 */
public class IOSwitch<T> {

    public final List<CommandHandler<T>> handlers = new ArrayList<>();
    public final Map<String, CommandHandler<T>> handlersByCommand = new HashMap<>();

    private IOSwitch(){}


    public IOSwitch<T> inCase(String pattern, CommandCallback<T> callback) {
        CommandHandler<T> handle = new CommandHandler<>(pattern, callback);

        handlers.add(handle);
        handlersByCommand.put(handle.getPattern(), handle);

        return this;
    }

    public IOSwitch<T> inCase(String pattern1, String pattern2, CommandCallback<T> callback) {
        return inCase(pattern1, callback)
              .inCase(pattern2, callback);
    }

    public T doIO(IO io) throws FlowControlException {
        while (true) {

            io.print(">:");

            String command = io.readLine();

            if (!handlersByCommand.containsKey(command)) {
                io.println(format(":Can't do the '%s'", command));
                continue;
            }

            CommandHandler<T> handler = handlersByCommand.get(command);

            ActionEvent<T> event = new ActionEvent<T>(io, this);
            handler.getCallback().call(event);

            return event.getResult();
        }

    }

    public static IOSwitch<?> doSwitch(){
        return new IOSwitch<>();
    }

    public static <T> IOSwitch<T> doSwitch(Class<T> dummy){
        return new IOSwitch<>();
    }

    private static class CommandHandler<T> {
        private final String pattern;
        private final CommandCallback<T> callback;

        private CommandHandler(String pattern, CommandCallback<T> callback) {
            this.pattern = pattern;
            this.callback = callback;
        }

        public String getPattern() {
            return pattern;
        }

        public CommandCallback<T> getCallback() {
            return callback;
        }
    }

    public static class ActionEvent<T>{
        private final IO io;
        private T result;
        private IOSwitch<T> ioSwitch;

        public ActionEvent(IO io, IOSwitch<T> ioSwitch) {
            this.io = io;
            this.ioSwitch = ioSwitch;
        }

        public IO io(){
            return io;
        }

        public IOSwitch<T> getSource(){
            return ioSwitch;
        }

        public T getResult() {
            return result;
        }

        public void setResult(T result) {
            this.result = result;
        }
    }

    public static interface CommandCallback<T> {
        void call(IOSwitch.ActionEvent<T> event) throws FlowControlException;
    }

    public static class FlowControlException extends Exception{}
}
