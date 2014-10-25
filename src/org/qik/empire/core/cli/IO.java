package org.qik.empire.core.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import static java.lang.String.format;

/**
 * Created by qik on 08.10.2014.
 */
public class IO {
    private final PrintStream writer;
    private final BufferedReader reader;


    public IO() {
        this.writer = System.out;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public IO println() {
        writer.println();
        return this;
    }

    public IO println(String o, Object...args) {
        writer.println(format(o, args));
        return this;
    }

    private static String trim(String value){
        return value == null ? null : value.trim();
    }

    public String readLine(){
        try {
            String value = reader.readLine();

            if(value == null) throw new IOCanceledException();

            return trim(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public IO print(Object s) {
        writer.print(s);
        return this;
    }

    public static class IOCanceledException extends RuntimeException{
        public IOCanceledException(){
            super("IO was canceled");
        }
    }
}
