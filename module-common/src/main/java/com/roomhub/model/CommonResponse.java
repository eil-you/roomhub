package com.roomhub.model;

public record CommonResponse<T>(int code, String message, T content) {

}
