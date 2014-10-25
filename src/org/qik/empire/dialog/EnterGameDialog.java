package org.qik.empire.dialog;

import org.qik.empire.Empire;
import org.qik.empire.core.Inject;
import org.qik.empire.core.cli.IO;
import org.qik.empire.service.AuthService;
import org.qik.empire.service.UserService;

import static org.qik.empire.Empire.ExitException;
import static org.qik.empire.Empire.LogoutException;

/**
 * Created by qik on 25.10.2014.
 */
public class EnterGameDialog {

    @Inject
    private AuthService authService;

    @Inject
    private UserService userService;

    public void enterGame(IO io) throws ExitException {
        while (true) {

            io.println("Hello Dear Player!");
            io.println("Would You Like To Create a New World Order Today?");
            io.println();
            io.println("Could you please remind me your previous user name to proceed:");
            io.println("(or type 'register' to create a new game, 'quit' - to cancel):");

            while (true) {
                String command = io.readLine();

                switch (command){
                    case "":                                            break;
                    case "register":            createWorld(io);        return;
                    case "quit": case "exit":                           throw new ExitException();
                    default:                    if(login(io, command))  return;
                }
            }
        }
    }

    public boolean login(IO io, String username){
        if(authService.login(username)){
            return true;
        }else{
            io.println("Sorry..... can't recognize you name, please try again.");
            return false;
        }
    }

    public void createWorld(IO io) {
        io.println("Hello, I am the chat master of this game");
        io.println("I will guide you into the empire");
        io.println("Excuse me but how should I call you?:");

        String userName = io.readLine().trim();

        io.println();
        io.println("Greetings mr(s) %s", userName);
        io.println("This lands is your empire now.");
        io.println("You can do here everything you want.");
        io.println();

        io.println("How would you name this lands:");

        String empireName = io.readLine().trim();

        userService.register(userName, empireName);
    }
}
