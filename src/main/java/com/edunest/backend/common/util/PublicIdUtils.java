package com.edunest.backend.common.util;

import com.edunest.backend.common.exception.BadRequestException;

import java.util.Locale;

public final class PublicIdUtils {

    private static final long UNIVERSITY_OFFSET = 100_000L;
    private static final long COLLEGE_OFFSET = 10_000L;
    private static final long BRANCH_OFFSET = 10_000L;
    private static final long ACADEMIC_YEAR_OFFSET = 10_000L;
    private static final long SEMESTER_OFFSET = 10_000L;
    private static final long SUBJECT_OFFSET = 10_000L;
    private static final long COLLEGE_BRANCH_OFFSET = 10_000L;
    private static final long UNIT_OFFSET = 10_000L;

    private PublicIdUtils() {}

    public static String universityId(Long id) { return format("U", id, UNIVERSITY_OFFSET, 6); }
    public static String collegeId(Long id) { return format("C", id, COLLEGE_OFFSET, 5); }
    public static String branchId(Long id) { return format("B", id, BRANCH_OFFSET, 5); }
    public static String academicYearId(Long id) { return format("AY", id, ACADEMIC_YEAR_OFFSET, 5); }
    public static String semesterId(Long id) { return format("S", id, SEMESTER_OFFSET, 5); }
    public static String subjectId(Long id) { return format("SUB", id, SUBJECT_OFFSET, 5); }
    public static String collegeBranchId(Long id) { return format("CB", id, COLLEGE_BRANCH_OFFSET, 5); }
    public static String unitId(Long id) { return format("UNIT", id, UNIT_OFFSET, 5); }

    public static Long parseUniversityId(String value) { return parse("University", "U", value, UNIVERSITY_OFFSET); }
    public static Long parseCollegeId(String value) { return parse("College", "C", value, COLLEGE_OFFSET); }
    public static Long parseBranchId(String value) { return parse("Branch", "B", value, BRANCH_OFFSET); }
    public static Long parseAcademicYearId(String value) { return parse("Academic year", "AY", value, ACADEMIC_YEAR_OFFSET); }
    public static Long parseSemesterId(String value) { return parse("Semester", "S", value, SEMESTER_OFFSET); }
    public static Long parseSubjectId(String value) { return parse("Subject", "SUB", value, SUBJECT_OFFSET); }
    public static Long parseCollegeBranchId(String value) { return parse("College branch", "CB", value, COLLEGE_BRANCH_OFFSET); }
    public static Long parseUnitId(String value) { return parse("Unit", "UNIT", value, UNIT_OFFSET); }

    private static String format(String prefix, Long id, long offset, int width) {
        if (id == null || id < 1) throw new IllegalArgumentException("Internal identifier must be positive");
        String number = Long.toString(Math.addExact(id, offset));
        return prefix + "0".repeat(Math.max(0, width - number.length())) + number;
    }

    private static Long parse(String resourceName, String prefix, String value, long offset) {
        if (value == null || value.isBlank()) throw new BadRequestException(resourceName + " ID is required");
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (normalized.chars().allMatch(Character::isDigit)) return parsePositiveLong(resourceName, normalized);
        if (!normalized.startsWith(prefix)) throw new BadRequestException("Invalid " + resourceName + " ID. Expected format " + prefix + "10001");
        String numberPart = normalized.substring(prefix.length());
        if (numberPart.isEmpty() || !numberPart.chars().allMatch(Character::isDigit))
            throw new BadRequestException("Invalid " + resourceName + " ID. Expected format " + prefix + "10001");
        long externalNumber = parsePositiveLong(resourceName, numberPart);
        if (externalNumber <= offset) throw new BadRequestException("Invalid " + resourceName + " ID");
        return externalNumber - offset;
    }

    private static Long parsePositiveLong(String resourceName, String value) {
        try {
            long id = Long.parseLong(value);
            if (id < 1) throw new BadRequestException("Invalid " + resourceName + " ID");
            return id;
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Invalid " + resourceName + " ID");
        }
    }
}