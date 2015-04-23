package org.sydenham.blabber.exception;

public class ApplicationException extends RuntimeException {
    public ApplicationException(Throwable e) {
        super(e);
    }
}
