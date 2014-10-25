package org.qik.empire.core;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Dmytro_Brazhnyk on 23.06.2014.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Command {
    public String value() default "";
}
