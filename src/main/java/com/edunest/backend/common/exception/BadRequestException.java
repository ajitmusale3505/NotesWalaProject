package com.edunest.backend.common.exception;

import com.edunest.backend.common.enums.ErrorCode;

public class BadRequestException extends BaseException {

    public BadRequestException(String message) {
        super(message, ErrorCode.BAD_REQUEST);
    }
}