package com.tinyhouse.exception;
import java.util.Map;
public class ValidationException extends RuntimeException {
    private final Map<String,String> errors;
    public ValidationException(String msg) { super(msg); this.errors = Map.of(); }
    public ValidationException(String msg, Map<String,String> errors) { super(msg); this.errors = errors; }
    public Map<String,String> getErrors() { return errors; }
}
