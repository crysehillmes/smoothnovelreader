package org.cryse.novelreader.util;

public class EmptyContentException extends RuntimeException{
    public EmptyContentException() {
    }

    public EmptyContentException(String detailMessage) {
        super(detailMessage);
    }

    public EmptyContentException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public EmptyContentException(Throwable throwable) {
        super(throwable);
    }
}
