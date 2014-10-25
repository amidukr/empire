package org.qik.empire.core.cli;

import org.qik.empire.context.SessionContext;
import org.qik.empire.core.EntryPointsContainer;

import java.io.*;

/**
 * Created by qik on 05.10.2014.
 */
public class ConsoleFacade {
    private final EntryPointsContainer entryPointsContainer;
    private final PrintStream out;
    private final InputStream in;

    public ConsoleFacade(EntryPointsContainer entryPointsContainer, PrintStream out, InputStream in) {
        this.entryPointsContainer = entryPointsContainer;
        this.out = out;
        this.in = in;
    }

    public void start() {


        SessionContext.openSession(ctx -> {
            CommandLineFacade cli = new CommandLineFacade(entryPointsContainer);

            while (true) {

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

                    String command = bufferedReader.readLine();

                    out.println(cli.execute(command));

                } catch (RuntimeException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
