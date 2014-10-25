package org.qik.empire;


import org.qik.empire.context.SessionContext;
import org.qik.empire.core.Inject;
import org.qik.empire.core.Prototype;
import org.qik.empire.core.ServiceContainer;
import org.qik.empire.core.cli.IO;
import org.qik.empire.core.cli.IOSwitch;
import org.qik.empire.core.cli.IOSwitch.ActionEvent;
import org.qik.empire.dialog.EnterGameDialog;
import org.qik.empire.resources.Messages;
import org.qik.empire.service.AuthService;
import org.qik.empire.service.UserService;

import java.io.IOException;

import static org.qik.empire.core.cli.IOSwitch.doSwitch;


/**
 * Created by Dmytro_Brazhnyk on 23.06.2014.
 */

@Prototype
public class Empire {

    @Inject
    private AuthService authService;

    @Inject
    private UserService userService;

    @Inject
    private EnterGameDialog enterGameDialog;




    public void run(IO io) throws LogoutException, ExitException {

        try {

            io.println(Messages.WELCOME);

            io.println("~For quick startup you can use 'actions' command.~:");


            while (true) {

                doSwitch()
                        .inCase("actions",             this::printActions)
                        .inCase("look",
                                "look around",         this::lookAround)
                        .inCase("logout",       e -> { throw new LogoutException(); })
                        .inCase("quit", "exit", e -> { throw new ExitException();   })
                .doIO(io);

            }

        }catch (LogoutException | ExitException e) {
            throw e;
        } catch (IOSwitch.FlowControlException e) {
            throw new RuntimeException(e);
        }
    }



    public void printActions(ActionEvent event){
        event.io().println("For the current moment you can only 'look around' ");
    }

    public void lookAround(ActionEvent event){
        event.io().println("You are staying on the flatland(%s). Nearby to you:", userService.getCurrentUser().getEmpireName())
                  .println("- forest")
                  .println("- river")
                  .println("- mountain");
    }







    public void startup(IO io) throws ExitException {
        while (true) {
            try{
                enterGameDialog.enterGame(io);
                run(io);
            }catch (LogoutException e) {}
        }
    }

    public static class LogoutException      extends IOSwitch.FlowControlException{}
    public static class ExitException        extends IOSwitch.FlowControlException{}

    public static void main(String[] args) throws IOException {
        ServiceContainer container = new ServiceContainer();

        SessionContext.openSession(ctx -> {
            IO io = new IO();
            try {

                Empire empire = container.getService(Empire.class);
                empire.startup(io);

            } catch (ExitException | IO.IOCanceledException e){
                io.println();
                io.println("Goodbye!!!");
            }
        });
    }
}
