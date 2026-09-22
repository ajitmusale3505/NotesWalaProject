package com.edunest.backend.common.response;

import com.edunest.backend.common.enums.ErrorCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

    private boolean success;
    private ErrorCode errorCode;
    private String message;
}