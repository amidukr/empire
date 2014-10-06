package org.qik.empire.service;

import org.qik.empire.core.Command;
import org.qik.empire.core.Service;

/**
 * Created by Dmytro_Brazhnyk on 23.06.2014.
 */
public class HelloWorld implements Service {
    @Command("Test")
    public void login(){}

    @Command
    public String echo(String msg){
        return msg;
    }
}
