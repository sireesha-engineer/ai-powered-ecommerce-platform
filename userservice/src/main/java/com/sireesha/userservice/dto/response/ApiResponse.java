package com.sireesha.userservice.dto.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiResponse {
    public String success;

    public static ApiResponse success(String message) {
        return new ApiResponse(message);
    }
}
