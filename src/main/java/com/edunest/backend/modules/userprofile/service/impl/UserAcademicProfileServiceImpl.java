package com.edunest.backend.modules.userprofile.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.util.PublicIdUtils;
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
import com.edunest.backend.modules.userprofile.dto.request.UserAcademicProfilePatchRequest;
import com.edunest.backend.modules.userprofile.dto.request.UserAcademicProfileRequest;
import com.edunest.backend.modules.userprofile.dto.response.UserAcademicProfileResponse;
import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;
import com.edunest.backend.modules.userprofile.service.UserAcademicProfileService;
import com.edunest.backend.modules.year.entity.AcademicYear;
import com.edunest.backend.modules.year.repository.AcademicYearRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Locale;

@Service
public class UserAcademicProfileServiceImpl implements UserAcademicProfileService {

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

    @Override
    @Transactional(readOnly = true)
    public UserAcademicProfileResponse getByUserId(Long userId) {
        UserAcademicProfile profile = profileRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic profile not found"));
        return map(profile);
    }

    @Override
    @Transactional
    public UserAcademicProfileResponse saveProfile(Long userId, UserAcademicProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AcademicReferences references = resolveReferences(
                request.getUniversityId(), request.getCollegeId(), request.getBranchId(),
                request.getAcademicYearId(), request.getSemesterId());

        UserAcademicProfile profile = profileRepository.findByUserId(userId).orElse(null);

        if (profile == null) {
            profile = UserAcademicProfile.builder()
                    .user(user)
                    .active(true)
                    .build();
        } else {
            profile.setActive(true);
        }

        applyProfileData(profile, references, request.getRollNumber(), request.getDivision(),
                request.getGraduationYear(), request.getCgpa(), request.getBacklogCount());

        return map(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public UserAcademicProfileResponse updateProfile(Long userId, UserAcademicProfileRequest request) {
        UserAcademicProfile profile = profileRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic profile not found"));

        AcademicReferences references = resolveReferences(
                request.getUniversityId(), request.getCollegeId(), request.getBranchId(),
                request.getAcademicYearId(), request.getSemesterId());

        applyProfileData(profile, references, request.getRollNumber(), request.getDivision(),
                request.getGraduationYear(), request.getCgpa(), request.getBacklogCount());

        return map(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public UserAcademicProfileResponse patchProfile(Long userId, UserAcademicProfilePatchRequest request) {
        UserAcademicProfile profile = profileRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic profile not found"));

        University university = request.getUniversityId() == null ? profile.getUniversity() : findUniversity(request.getUniversityId());
        College college = request.getCollegeId() == null ? profile.getCollege() : findCollege(request.getCollegeId());
        Branch branch = request.getBranchId() == null ? profile.getBranch() : findBranch(request.getBranchId());
        AcademicYear academicYear = request.getAcademicYearId() == null ? profile.getAcademicYear() : findAcademicYear(request.getAcademicYearId());
        Semester semester = request.getSemesterId() == null ? profile.getCurrentSemester() : findSemester(request.getSemesterId());

        validateAcademicHierarchy(university, college, branch, academicYear, semester);

        applyProfileData(
                profile,
                new AcademicReferences(university, college, branch, academicYear, semester),
                request.getRollNumber() == null ? profile.getRollNumber() : request.getRollNumber(),
                request.getDivision() == null ? profile.getDivision() : request.getDivision(),
                request.getGraduationYear() == null ? profile.getGraduationYear() : request.getGraduationYear(),
                request.getCgpa() == null ? profile.getCgpa() : request.getCgpa(),
                request.getBacklogCount() == null ? profile.getBacklogCount() : request.getBacklogCount());

        return map(profileRepository.save(profile));
    }

    private AcademicReferences resolveReferences(String universityId, String collegeId, String branchId,
                                                  String academicYearId, String semesterId) {
        University university = findUniversity(universityId);
        College college = findCollege(collegeId);
        Branch branch = findBranch(branchId);
        AcademicYear academicYear = findAcademicYear(academicYearId);
        Semester semester = findSemester(semesterId);

        validateAcademicHierarchy(university, college, branch, academicYear, semester);
        return new AcademicReferences(university, college, branch, academicYear, semester);
    }

    private void validateAcademicHierarchy(
            University university, College college, Branch branch,
            AcademicYear academicYear, Semester semester) {

        if (!university.isActive()) throw new BadRequestException("Selected university is inactive");
        if (!college.isActive()) throw new BadRequestException("Selected college is inactive");
        if (!branch.isActive()) throw new BadRequestException("Selected branch is inactive");
        if (!academicYear.isActive()) throw new BadRequestException("Selected academic year is inactive");
        if (!semester.isActive()) throw new BadRequestException("Selected semester is inactive");

        if (!college.getUniversity().getId().equals(university.getId())) {
            throw new BadRequestException("Selected college does not belong to selected university");
        }
        if (!branch.getUniversity().getId().equals(university.getId())) {
            throw new BadRequestException("Selected branch does not belong to selected university");
        }
        if (!collegeBranchRepository.existsByCollegeIdAndBranchIdAndActiveTrue(college.getId(), branch.getId())) {
            throw new BadRequestException("Selected college does not offer selected branch");
        }
        if (!branch.getAcademicYear().getId().equals(academicYear.getId())) {
            throw new BadRequestException("Branch does not belong to selected academic year");
        }
        if (!semester.getAcademicYear().getId().equals(academicYear.getId())) {
            throw new BadRequestException("Semester does not belong to selected academic year");
        }
        if (academicYear.getUniversity() != null
                && !academicYear.getUniversity().getId().equals(university.getId())) {
            throw new BadRequestException("Selected academic year does not belong to selected university");
        }
        if (semester.getAcademicYear().getUniversity() != null
                && academicYear.getUniversity() != null
                && !semester.getAcademicYear().getUniversity().getId().equals(university.getId())) {
            throw new BadRequestException("Selected semester does not belong to selected university");
        }
    }

    private University findUniversity(String publicId) {
        return universityRepository.findByIdAndActiveTrue(PublicIdUtils.parseUniversityId(publicId))
                .orElseThrow(() -> new ResourceNotFoundException("University not found"));
    }

    private College findCollege(String publicId) {
        return collegeRepository.findByIdAndActiveTrue(PublicIdUtils.parseCollegeId(publicId))
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));
    }

    private Branch findBranch(String publicId) {
        return branchRepository.findByIdAndActiveTrue(PublicIdUtils.parseBranchId(publicId))
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
    }

    private AcademicYear findAcademicYear(String publicId) {
        return academicYearRepository.findByIdAndActiveTrue(PublicIdUtils.parseAcademicYearId(publicId))
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found"));
    }

    private Semester findSemester(String publicId) {
        return semesterRepository.findByIdAndActiveTrue(PublicIdUtils.parseSemesterId(publicId))
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found"));
    }

    private void applyProfileData(UserAcademicProfile profile, AcademicReferences references,
                                  String rollNumber, String division, Integer graduationYear,
                                  BigDecimal cgpa, Integer backlogCount) {
        profile.setUniversity(references.university());
        profile.setCollege(references.college());
        profile.setBranch(references.branch());
        profile.setAcademicYear(references.academicYear());
        profile.setCurrentSemester(references.semester());
        profile.setRollNumber(normalize(rollNumber));
        profile.setDivision(normalize(division));
        profile.setGraduationYear(graduationYear);
        profile.setCgpa(cgpa);
        profile.setBacklogCount(backlogCount);
    }

    private String normalize(String value) {
        if (value == null) return null;
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized.toUpperCase(Locale.ROOT);
    }

    private UserAcademicProfileResponse map(UserAcademicProfile profile) {
        return UserAcademicProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .userName(profile.getUser().getFullName())
                .universityId(PublicIdUtils.universityId(profile.getUniversity().getId()))
                .universityName(profile.getUniversity().getName())
                .collegeId(PublicIdUtils.collegeId(profile.getCollege().getId()))
                .collegeName(profile.getCollege().getName())
                .branchId(PublicIdUtils.branchId(profile.getBranch().getId()))
                .branchName(profile.getBranch().getName())
                .academicYearId(PublicIdUtils.academicYearId(profile.getAcademicYear().getId()))
                .academicYearName(profile.getAcademicYear().getName())
                .semesterId(PublicIdUtils.semesterId(profile.getCurrentSemester().getId()))
                .semesterName(profile.getCurrentSemester().getName())
                .rollNumber(profile.getRollNumber())
                .division(profile.getDivision())
                .graduationYear(profile.getGraduationYear())
                .cgpa(profile.getCgpa())
                .backlogCount(profile.getBacklogCount())
                .profileCompletionPercentage(profile.getProfileCompletionPercentage())
                .profileCompleted(profile.isProfileCompleted())
                .build();
    }

    private record AcademicReferences(
            University university, College college, Branch branch,
            AcademicYear academicYear, Semester semester) {
    }
}