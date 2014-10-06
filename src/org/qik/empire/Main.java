package org.qik.empire;




import org.qik.empire.core.ServiceContainer;
import org.qik.empire.core.cli.CommandLineSession;
import org.qik.empire.core.cli.ConsoleFacade;
import org.qik.empire.service.AuthService;
import org.qik.empire.service.HelloWorld;
import org.qik.empire.service.MetaService;

/**
 * Created by Dmytro_Brazhnyk on 23.06.2014.
 */
public class Main {


    public static void main(String[] args) {
        ServiceContainer serviceContainer = new ServiceContainer();

        serviceContainer.addService(new HelloWorld());
        serviceContainer.addService(new AuthService());
        serviceContainer.addService(new MetaService());

        serviceContainer.reload();


        CommandLineSession session = new CommandLineSession(serviceContainer);

        session.execute("login user1");
        System.out.println("User is: " + session.execute("getuser"));

        session.close();

        new ConsoleFacade(serviceContainer, System.out, System.in).start();
    }
}
