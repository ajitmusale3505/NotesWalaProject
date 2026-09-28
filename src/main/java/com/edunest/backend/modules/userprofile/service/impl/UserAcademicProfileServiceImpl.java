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
                request.getGraduationYear(), request.getCgpa(), request.getBacklogCount(),
                request.getPhoneNumber(), request.getGender(), request.getCurrentYear(),
                request.getState(), request.getCity());
        applyExtendedAcademicData(profile, request.getDegree(), request.getMode(), request.getCurrentStatus(),
                request.getLastYearSgpa(), request.getTenthPercentage(), request.getTwelfthPercentage(),
                request.getDiplomaDetails(), request.getAdditionalInformation());

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
                request.getGraduationYear(), request.getCgpa(), request.getBacklogCount(),
                request.getPhoneNumber(), request.getGender(), request.getCurrentYear(),
                request.getState(), request.getCity());
        applyExtendedAcademicData(profile, request.getDegree(), request.getMode(), request.getCurrentStatus(),
                request.getLastYearSgpa(), request.getTenthPercentage(), request.getTwelfthPercentage(),
                request.getDiplomaDetails(), request.getAdditionalInformation());

        return map(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public UserAcademicProfileResponse patchProfile(Long userId, UserAcademicProfilePatchRequest request) {
        UserAcademicProfile profile = profileRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic profile not found"));

        boolean hierarchyChanged = hasText(request.getUniversityId())
                || hasText(request.getCollegeId())
                || hasText(request.getBranchId())
                || hasText(request.getAcademicYearId())
                || hasText(request.getSemesterId());

        University university = hasText(request.getUniversityId()) ? findUniversity(request.getUniversityId()) : profile.getUniversity();
        College college = hasText(request.getCollegeId()) ? findCollege(request.getCollegeId()) : profile.getCollege();
        Branch branch = hasText(request.getBranchId()) ? findBranch(request.getBranchId()) : profile.getBranch();
        AcademicYear academicYear = hasText(request.getAcademicYearId()) ? findAcademicYear(request.getAcademicYearId()) : profile.getAcademicYear();
        Semester semester = hasText(request.getSemesterId()) ? findSemester(request.getSemesterId()) : profile.getCurrentSemester();

        if (hierarchyChanged) {
            validateAcademicHierarchy(university, college, branch, academicYear, semester);
            profile.setUniversity(university);
            profile.setCollege(college);
            profile.setBranch(branch);
            profile.setAcademicYear(academicYear);
            profile.setCurrentSemester(semester);
        }

        if (hasText(request.getRollNumber())) profile.setRollNumber(request.getRollNumber().trim());
        if (hasText(request.getDivision())) profile.setDivision(request.getDivision().trim());
        if (request.getGraduationYear() != null) profile.setGraduationYear(request.getGraduationYear());
        if (request.getCgpa() != null) profile.setCgpa(request.getCgpa());
        if (request.getBacklogCount() != null) profile.setBacklogCount(request.getBacklogCount());
        if (hasText(request.getPhoneNumber())) profile.setPhoneNumber(request.getPhoneNumber().trim());
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getCurrentYear() != null) profile.setCurrentYear(request.getCurrentYear());
        if (hasText(request.getState())) profile.setState(request.getState().trim());
        if (hasText(request.getCity())) profile.setCity(request.getCity().trim());

        if (hasText(request.getDegree())) profile.setDegree(request.getDegree().trim());
        if (hasText(request.getMode())) profile.setMode(request.getMode().trim());
        if (hasText(request.getCurrentStatus())) profile.setCurrentStatus(request.getCurrentStatus().trim());
        if (request.getLastYearSgpa() != null) profile.setLastYearSgpa(request.getLastYearSgpa());
        if (request.getTenthPercentage() != null) profile.setTenthPercentage(request.getTenthPercentage());
        if (request.getTwelfthPercentage() != null) profile.setTwelfthPercentage(request.getTwelfthPercentage());
        if (hasText(request.getDiplomaDetails())) profile.setDiplomaDetails(request.getDiplomaDetails().trim());
        if (hasText(request.getAdditionalInformation())) profile.setAdditionalInformation(request.getAdditionalInformation().trim());

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
                                  BigDecimal cgpa, Integer backlogCount, String phoneNumber,
                                  com.edunest.backend.modules.userprofile.enums.Gender gender,
                                  Integer currentYear, String state, String city) {
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
        profile.setPhoneNumber(normalizePhoneNumber(phoneNumber));
        profile.setGender(gender);
        profile.setCurrentYear(currentYear);
        profile.setCountry("India");
        profile.setState(normalizeLocation(state));
        profile.setCity(normalizeLocation(city));
    }

    private void applyExtendedAcademicData(UserAcademicProfile profile, String degree, String mode,
                                            String currentStatus, BigDecimal lastYearSgpa,
                                            BigDecimal tenthPercentage, BigDecimal twelfthPercentage,
                                            String diplomaDetails, String additionalInformation) {
        profile.setDegree(normalizeLocation(degree));
        profile.setMode(normalizeLocation(mode));
        profile.setCurrentStatus(normalizeLocation(currentStatus));
        profile.setLastYearSgpa(lastYearSgpa);
        profile.setTenthPercentage(tenthPercentage);
        profile.setTwelfthPercentage(twelfthPercentage);
        profile.setDiplomaDetails(normalizeLocation(diplomaDetails));
        profile.setAdditionalInformation(normalizeLocation(additionalInformation));
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String normalize(String value) {
        if (value == null) return null;
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized.toUpperCase(Locale.ROOT);
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return null;
        String normalized = phoneNumber.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String normalizeLocation(String value) {
        if (value == null) return null;
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
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
                .phoneNumber(profile.getPhoneNumber())
                .gender(profile.getGender())
                .currentYear(profile.getCurrentYear())
                .degree(profile.getDegree())
                .mode(profile.getMode())
                .currentStatus(profile.getCurrentStatus())
                .lastYearSgpa(profile.getLastYearSgpa())
                .tenthPercentage(profile.getTenthPercentage())
                .twelfthPercentage(profile.getTwelfthPercentage())
                .diplomaDetails(profile.getDiplomaDetails())
                .additionalInformation(profile.getAdditionalInformation())
                .country(profile.getCountry() == null || profile.getCountry().isBlank() ? "India" : profile.getCountry())
                .state(profile.getState())
                .city(profile.getCity())
                .profileCompletionPercentage(profile.getProfileCompletionPercentage())
                .profileCompleted(profile.isProfileCompleted())
                .build();
    }

    private record AcademicReferences(
            University university, College college, Branch branch,
            AcademicYear academicYear, Semester semester) {
    }
}
