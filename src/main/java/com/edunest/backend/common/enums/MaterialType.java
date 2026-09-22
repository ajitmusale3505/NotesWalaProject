package com.edunest.backend.common.enums;

public enum MaterialType {

    RAW_PYQ("Raw Previous Year Papers", false),
    RAW_NOTES("Class Notes", false),
    LAB_MANUAL("Lab Manual", false),

    SOLVED_PYQ("Solved Previous Year Papers", true),
    PREMIUM_NOTES("Premium Notes", true),
    LAB_MANUAL_CODES("Lab Manual with Codes", true),
    VIVA_QUESTIONS("Viva Questions", true),
    BOOK_PDF("Reference Book", true),
    FORMULA_SHEET("Formula Sheet", true),
    MCQ_BANK("MCQ Bank", true),
    VIMP_QUESTIONS("Very Important Questions", true),
    MOCK_TEST("Mock Test", true),

    PLACEMENT_PACK("Placement Pack", true);

    private final String displayName;
    private final boolean premium;

    MaterialType(String displayName, boolean premium) {
        this.displayName = displayName;
        this.premium = premium;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPremium() {
        return premium;
    }
}