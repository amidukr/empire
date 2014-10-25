package org.qik.empire.core;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by qik on 05.10.2014.
 */
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface Inject {
}
