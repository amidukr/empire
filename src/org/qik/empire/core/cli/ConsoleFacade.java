package org.qik.empire.core.cli;

import org.qik.empire.core.ServiceContainer;
import org.qik.empire.utils.Utils;

import java.io.*;

import static org.qik.empire.utils.Utils.defaultValue;

/**
 * Created by qik on 05.10.2014.
 */
public class ConsoleFacade {
    private final ServiceContainer serviceContainer;
    private final PrintStream out;
    private final InputStream in;

    public ConsoleFacade(ServiceContainer serviceContainer, PrintStream out, InputStream in) {
        this.serviceContainer = serviceContainer;
        this.out = out;
        this.in = in;
    }

    public void start() {

        try(CommandLineSession cli = new CommandLineSession(serviceContainer)){

            while (true) {

                try{
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

                    String command = bufferedReader.readLine();

                    out.println(cli.execute(command));

                }catch (RuntimeException|IOException ex){
                    ex.printStackTrace();
                }
            }
        }
    }
}
