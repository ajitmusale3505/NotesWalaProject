package com.edunest.backend.modules.userprofile.entity;

import com.edunest.backend.modules.userprofile.enums.Gender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAcademicProfileTest {

    @Test
    void emptyProfileIsIncomplete() {
        UserAcademicProfile profile = new UserAcademicProfile();
        assertFalse(profile.isProfileCompleted());
        assertEquals(0, profile.getProfileCompletionPercentage());
    }

    @Test
    void academicIdentityCanBeCompleteWithoutCgpa() {
        UserAcademicProfile profile = new UserAcademicProfile();
        profile.setUniversity(new com.edunest.backend.modules.university.entity.University());
        profile.setCollege(new com.edunest.backend.modules.college.entity.College());
        profile.setBranch(new com.edunest.backend.modules.branch.entity.Branch());
        profile.setAcademicYear(new com.edunest.backend.modules.year.entity.AcademicYear());
        profile.setCurrentSemester(new com.edunest.backend.modules.semester.entity.Semester());
        profile.setRollNumber("CE101");
        profile.setDivision("A");
        profile.setGraduationYear(2028);
        profile.setBacklogCount(0);

        assertFalse(profile.isProfileCompleted());
        assertEquals(50, profile.getProfileCompletionPercentage());
    }

    @Test
    void personalFieldsCompleteTheProfile() {
        UserAcademicProfile profile = new UserAcademicProfile();
        profile.setUniversity(new com.edunest.backend.modules.university.entity.University());
        profile.setCollege(new com.edunest.backend.modules.college.entity.College());
        profile.setBranch(new com.edunest.backend.modules.branch.entity.Branch());
        profile.setAcademicYear(new com.edunest.backend.modules.year.entity.AcademicYear());
        profile.setCurrentSemester(new com.edunest.backend.modules.semester.entity.Semester());
        profile.setRollNumber("CE101");
        profile.setDivision("A");
        profile.setGraduationYear(2028);
        profile.setBacklogCount(0);
        profile.setPhoneNumber("9876543210");
        profile.setGender(Gender.MALE);
        profile.setCurrentYear(4);
        profile.setState("Maharashtra");
        profile.setCity("Pune");

        assertTrue(profile.isProfileCompleted());
        assertEquals(100, profile.getProfileCompletionPercentage());
    }
}
