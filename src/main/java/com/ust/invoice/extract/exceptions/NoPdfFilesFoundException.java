package com.ust.invoice.extract.exceptions;

public class NoPdfFilesFoundException extends RuntimeException {
    public NoPdfFilesFoundException(String message) {
        super(message);
    }
}
