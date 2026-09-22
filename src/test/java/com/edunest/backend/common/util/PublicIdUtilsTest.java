package com.edunest.backend.common.util;

import com.edunest.backend.common.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PublicIdUtilsTest {
    @Test void generatesExpectedPublicIds() {
        assertEquals("U100001", PublicIdUtils.universityId(1L));
        assertEquals("C10001", PublicIdUtils.collegeId(1L));
        assertEquals("B10001", PublicIdUtils.branchId(1L));
        assertEquals("AY10001", PublicIdUtils.academicYearId(1L));
        assertEquals("S10001", PublicIdUtils.semesterId(1L));
        assertEquals("SUB10001", PublicIdUtils.subjectId(1L));
        assertEquals("CB10001", PublicIdUtils.collegeBranchId(1L));
        assertEquals("UNIT10001", PublicIdUtils.unitId(1L));
    }

    @Test void parsesPublicIds() {
        assertEquals(1L, PublicIdUtils.parseUniversityId("U100001"));
        assertEquals(1L, PublicIdUtils.parseCollegeId("C10001"));
        assertEquals(1L, PublicIdUtils.parseBranchId("B10001"));
        assertEquals(1L, PublicIdUtils.parseAcademicYearId("AY10001"));
        assertEquals(1L, PublicIdUtils.parseSemesterId("S10001"));
        assertEquals(1L, PublicIdUtils.parseSubjectId("SUB10001"));
        assertEquals(1L, PublicIdUtils.parseCollegeBranchId("CB10001"));
        assertEquals(1L, PublicIdUtils.parseUnitId("UNIT10001"));
    }

    @Test void acceptsLegacyNumericIds() {
        assertEquals(7L, PublicIdUtils.parseUniversityId("7"));
        assertEquals(12L, PublicIdUtils.parseCollegeId("12"));
        assertEquals(25L, PublicIdUtils.parseBranchId("25"));
        assertEquals(4L, PublicIdUtils.parseUnitId("4"));
    }

    @Test void rejectsWrongPublicIdPrefix() {
        assertThrows(BadRequestException.class, () -> PublicIdUtils.parseUniversityId("C10001"));
    }
}