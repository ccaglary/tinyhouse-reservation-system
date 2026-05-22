package com.tinyhouse.exception;
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String msg) { super(msg); }
    public ResourceNotFoundException(String res, String field, Object val) { super(String.format("%s bulunamadı: %s = '%s'", res, field, val)); }
}
