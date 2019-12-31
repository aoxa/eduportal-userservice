package io.zuppelli.userservice.service;

public interface Builder<T> {
    T build();
    Builder<T> add(String method, Object content);
}
