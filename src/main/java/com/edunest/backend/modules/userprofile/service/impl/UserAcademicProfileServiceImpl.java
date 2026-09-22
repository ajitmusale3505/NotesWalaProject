package com.edunest.backend.modules.userprofile.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;


import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.branch.repository.BranchRepository;
import com.edunest.backend.modules.college.entity.College;
import com.edunest.backend.modules.college.repository.CollegeRepository;
import com.edunest.backend.modules.collegebranch.repository.CollegeBranchRepository;
import com.edunest.backend.modules.semester.entity.Semester;
import com.edunest.backend.modules.semester.repository.SemesterRepository;
import com.edunest.backend.modules.university.entity.University;
import com.edunest.backend.modules.university.repository.UniversityRepository;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import com.edunest.backend.modules.userprofile.dto.request.UserAcademicProfileRequest;
import com.edunest.backend.modules.userprofile.dto.response.UserAcademicProfileResponse;
import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;
import com.edunest.backend.modules.userprofile.service.UserAcademicProfileService;
import com.edunest.backend.modules.year.entity.AcademicYear;
import com.edunest.backend.modules.year.repository.AcademicYearRepository;

import com.edunest.backend.modules.userprofile.dto.request.UserAcademicProfilePatchRequest;

@Service
public class UserAcademicProfileServiceImpl
        implements UserAcademicProfileService {

    private final UserAcademicProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final CollegeRepository collegeRepository;
    private final BranchRepository branchRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SemesterRepository semesterRepository;
    private final CollegeBranchRepository collegeBranchRepository;

    public UserAcademicProfileServiceImpl(
            UserAcademicProfileRepository profileRepository,
            UserRepository userRepository,
            UniversityRepository universityRepository,
            CollegeRepository collegeRepository,
            BranchRepository branchRepository,
            AcademicYearRepository academicYearRepository,
            SemesterRepository semesterRepository,
            CollegeBranchRepository collegeBranchRepository) {

        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.universityRepository = universityRepository;
        this.collegeRepository = collegeRepository;
        this.branchRepository = branchRepository;
        this.academicYearRepository = academicYearRepository;
        this.semesterRepository = semesterRepository;
        this.collegeBranchRepository = collegeBranchRepository;
    }

    private UserAcademicProfileResponse map(UserAcademicProfile profile) {
        return UserAcademicProfileResponse.builder()
                .id(profile.getId())

                .userId(profile.getUser().getId())
                .userName(profile.getUser().getFullName())

                .universityId(profile.getUniversity().getId())
                .universityName(profile.getUniversity().getName())

                .collegeId(profile.getCollege().getId())
                .collegeName(profile.getCollege().getName())

                .branchId(profile.getBranch().getId())
                .branchName(profile.getBranch().getName())

                .academicYearId(profile.getAcademicYear().getId())
                .academicYearName(profile.getAcademicYear().getName())

                .semesterId(profile.getCurrentSemester().getId())
                .semesterName(profile.getCurrentSemester().getName())

                .rollNumber(profile.getRollNumber())
                .division(profile.getDivision())
                .graduationYear(profile.getGraduationYear())
                .cgpa(profile.getCgpa())
                .backlogCount(profile.getBacklogCount())

                .profileCompleted(true)
                .build();
    }
    @Override
    public UserAcademicProfileResponse getByUserId(Long userId) {
        UserAcademicProfile profile = profileRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found"));

        return map(profile);
    }
    
    @Override
    public UserAcademicProfileResponse patchProfile(
            Long userId,
            UserAcademicProfilePatchRequest request) {

        UserAcademicProfile profile = profileRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found"));

        if (request.getUniversityId() != null) {
            University university = universityRepository
                    .findById(request.getUniversityId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("University not found"));
            profile.setUniversity(university);
        }

        if (request.getCollegeId() != null) {
            College college = collegeRepository
                    .findById(request.getCollegeId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("College not found"));
            profile.setCollege(college);
        }

        if (request.getBranchId() != null) {
            Branch branch = branchRepository
                    .findById(request.getBranchId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Branch not found"));
            profile.setBranch(branch);
        }

        if (request.getAcademicYearId() != null) {
            AcademicYear year = academicYearRepository
                    .findById(request.getAcademicYearId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Academic year not found"));
            profile.setAcademicYear(year);
        }

        if (request.getSemesterId() != null) {
            Semester semester = semesterRepository
                    .findById(request.getSemesterId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Semester not found"));
            profile.setCurrentSemester(semester);
        }

        if (request.getRollNumber() != null)
            profile.setRollNumber(request.getRollNumber());

        if (request.getDivision() != null)
            profile.setDivision(request.getDivision());

        if (request.getGraduationYear() != null)
            profile.setGraduationYear(request.getGraduationYear());

        if (request.getCgpa() != null)
            profile.setCgpa(request.getCgpa());

        if (request.getBacklogCount() != null)
            profile.setBacklogCount(request.getBacklogCount());

        profile.setProfileVersion(
                profile.getProfileVersion() + 1
        );

        profile = profileRepository.save(profile);

        return map(profile);
    }

    @Override
    public UserAcademicProfileResponse saveProfile(
            UserAcademicProfileRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        University university = universityRepository
                .findById(request.getUniversityId())
                .orElseThrow(() -> new ResourceNotFoundException("University not found"));

        College college = collegeRepository
                .findById(request.getCollegeId())
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));

        Branch branch = branchRepository
                .findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        AcademicYear academicYear = academicYearRepository
                .findById(request.getAcademicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found"));

        Semester semester = semesterRepository
                .findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found"));

        // Validation 1
        if (!college.getUniversity().getId().equals(university.getId())) {
            throw new BadRequestException(
                    "Selected college does not belong to selected university");
        }

        // Validation 2
        if (!branch.getUniversity().getId().equals(university.getId())) {
            throw new BadRequestException(
                    "Selected branch does not belong to selected university");
        }

        // Validation 3
        if (!collegeBranchRepository.existsByCollegeIdAndBranchId(
                college.getId(), branch.getId())) {
            throw new BadRequestException(
                    "Selected college does not offer selected branch");
        }

        // Validation 4
        if (!branch.getAcademicYear().getId()
                .equals(academicYear.getId())) {
            throw new BadRequestException(
                    "Branch does not belong to selected academic year");
        }

        // Validation 5
        if (!semester.getAcademicYear().getId()
                .equals(academicYear.getId())) {
            throw new BadRequestException(
                    "Semester does not belong to selected academic year");
        }

        UserAcademicProfile profile = profileRepository
                .findByUserId(user.getId())
                .orElse(null);

        if (profile == null) {
            profile = UserAcademicProfile.builder()
                    .user(user)
                    .profileVersion(1)
                    .active(true)
                    .build();
        } else {
            profile.setProfileVersion(
                    profile.getProfileVersion() + 1);
        }

        profile.setUniversity(university);
        profile.setCollege(college);
        profile.setBranch(branch);
        profile.setAcademicYear(academicYear);
        profile.setCurrentSemester(semester);
        profile.setRollNumber(request.getRollNumber());
        profile.setDivision(request.getDivision());
        profile.setGraduationYear(request.getGraduationYear());
        profile.setCgpa(request.getCgpa());
        profile.setBacklogCount(request.getBacklogCount());

        profile = profileRepository.save(profile);

        return map(profile);
    }
    
    @Override
    public UserAcademicProfileResponse updateProfile(
            Long userId,
            UserAcademicProfileRequest request) {

        UserAcademicProfile profile = profileRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found"));

        University university = universityRepository
                .findById(request.getUniversityId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("University not found"));

        College college = collegeRepository
                .findById(request.getCollegeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("College not found"));

        Branch branch = branchRepository
                .findById(request.getBranchId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found"));

        AcademicYear academicYear = academicYearRepository
                .findById(request.getAcademicYearId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Academic year not found"));

        Semester semester = semesterRepository
                .findById(request.getSemesterId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Semester not found"));

        // validations
        if (!college.getUniversity().getId().equals(university.getId())) {
            throw new BadRequestException(
                    "Selected college does not belong to selected university");
        }

        if (!branch.getUniversity().getId().equals(university.getId())) {
            throw new BadRequestException(
                    "Selected branch does not belong to selected university");
        }

        if (!collegeBranchRepository.existsByCollegeIdAndBranchId(
                college.getId(),
                branch.getId())) {
            throw new BadRequestException(
                    "Selected college does not offer selected branch");
        }

        if (!branch.getAcademicYear().getId()
                .equals(academicYear.getId())) {
            throw new BadRequestException(
                    "Branch does not belong to selected academic year");
        }

        if (!semester.getAcademicYear().getId()
                .equals(academicYear.getId())) {
            throw new BadRequestException(
                    "Semester does not belong to selected academic year");
        }

        profile.setUniversity(university);
        profile.setCollege(college);
        profile.setBranch(branch);
        profile.setAcademicYear(academicYear);
        profile.setCurrentSemester(semester);

        profile.setRollNumber(request.getRollNumber());
        profile.setDivision(request.getDivision());
        profile.setGraduationYear(request.getGraduationYear());
        profile.setCgpa(request.getCgpa());
        profile.setBacklogCount(request.getBacklogCount());

        profile.setProfileVersion(
                profile.getProfileVersion() + 1
        );

        profile = profileRepository.save(profile);

        return map(profile);
    }
}