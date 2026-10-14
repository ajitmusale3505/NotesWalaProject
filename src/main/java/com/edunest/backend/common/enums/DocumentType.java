package com.edunest.backend.common.enums;

/**
 * File format for a resource. Older values are retained for resources created
 * before DocumentType represented the uploaded file format.
 */
public enum DocumentType {
    PDF,
    WORD,
    ZIP,
    RAR,
    SEVEN_ZIP,
    POWERPOINT,
    EXCEL,
    TEXT,
    CSV,
    OTHER,

    // Legacy values retained so existing database records continue to deserialize.
    QUESTION_PAPER,
    SOLVED_PAPER,
    BOOK,
    NOTE,
    IMPORTANT_QUESTION,
    STUDY_MATERIAL
}
