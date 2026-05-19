package com.example.music.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回结果，便于前后端约定固定的数据格式。
 *
 * @param <T> 返回的数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 状态码，200 表示成功，500 表示失败。
     */
    private Integer code;

    /**
     * 提示信息。
     */
    private String message;

    /**
     * 实际返回的数据。
     */
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
}
