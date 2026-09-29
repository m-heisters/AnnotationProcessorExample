package com.mheisters.pizzafactory.annotation.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Factory {

    // The name of the factory
    Class type();

    // The identifier to determine which item should be instantiated
    String id();
}
