package com.mheisters.pizzafactory.exception;

import com.mheisters.pizzafactory.annotations.FactoryAnnotatedClass;


public class IdAlreadyInUseException extends Exception {

    private final FactoryAnnotatedClass existing;

    public IdAlreadyInUseException(FactoryAnnotatedClass existing) {
        super();
        this.existing = existing;
    }

    public FactoryAnnotatedClass getExisting() {
        return existing;
    }
}
