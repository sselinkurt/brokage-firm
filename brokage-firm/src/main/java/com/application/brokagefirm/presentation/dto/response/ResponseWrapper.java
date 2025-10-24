package com.application.brokagefirm.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWrapper<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ResponseWrapper<T> success(String message, T data) {
        return new ResponseWrapper<>(true, message, data);
    }

    public static <T> ResponseWrapper<T> failure(String message) {
        return new ResponseWrapper<>(false, message, null);
    }

    public static <T> ResponseWrapper<T> failure(String message, T data) {
        return new ResponseWrapper<>(false, message, data);
    }
}
