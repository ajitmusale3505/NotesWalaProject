package com.edunest.backend.modules.userprofile.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.util.PublicIdUtils;
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
import com.edunest.backend.modules.userprofile.dto.request.UserAcademicProfilePatchRequest;
import com.edunest.backend.modules.userprofile.dto.request.UserAcademicProfileRequest;
import com.edunest.backend.modules.userprofile.dto.response.UserAcademicProfileResponse;
import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;
import com.edunest.backend.modules.userprofile.service.UserAcademicProfileService;
import com.edunest.backend.modules.year.entity.AcademicYear;
import com.edunest.backend.modules.year.repository.AcademicYearRepository;

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
                .profileCompleted(true)
                .build();
    }

    @Override
    public UserAcademicProfileResponse getByUserId(Long userId) {
        UserAcademicProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return map(profile);
    }

    @Override
    public UserAcademicProfileResponse patchProfile(
            Long userId,
            UserAcademicProfilePatchRequest request) {

        UserAcademicProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (request.getUniversityId() != null) {
            profile.setUniversity(findUniversity(request.getUniversityId()));
        }

        if (request.getCollegeId() != null) {
            profile.setCollege(findCollege(request.getCollegeId()));
        }

        if (request.getBranchId() != null) {
            profile.setBranch(findBranch(request.getBranchId()));
        }

        if (request.getAcademicYearId() != null) {
            profile.setAcademicYear(findAcademicYear(request.getAcademicYearId()));
        }

        if (request.getSemesterId() != null) {
            profile.setCurrentSemester(findSemester(request.getSemesterId()));
        }

        if (request.getRollNumber() != null) {
            profile.setRollNumber(request.getRollNumber());
        }
        if (request.getDivision() != null) {
            profile.setDivision(request.getDivision());
        }
        if (request.getGraduationYear() != null) {
            profile.setGraduationYear(request.getGraduationYear());
        }
        if (request.getCgpa() != null) {
            profile.setCgpa(request.getCgpa());
        }
        if (request.getBacklogCount() != null) {
            profile.setBacklogCount(request.getBacklogCount());
        }

        profile.setProfileVersion(profile.getProfileVersion() + 1);
        return map(profileRepository.save(profile));
    }

    @Override
    public UserAcademicProfileResponse saveProfile(
            UserAcademicProfileRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        University university = findUniversity(request.getUniversityId());
        College college = findCollege(request.getCollegeId());
        Branch branch = findBranch(request.getBranchId());
        AcademicYear academicYear = findAcademicYear(request.getAcademicYearId());
        Semester semester = findSemester(request.getSemesterId());

        validateAcademicHierarchy(
                university,
                college,
                branch,
                academicYear,
                semester);

        UserAcademicProfile profile = profileRepository.findByUserId(user.getId())
                .orElse(null);

        if (profile == null) {
            profile = UserAcademicProfile.builder()
                    .user(user)
                    .profileVersion(1)
                    .active(true)
                    .build();
        } else {
            profile.setProfileVersion(profile.getProfileVersion() + 1);
        }

        applyProfileData(profile, university, college, branch, academicYear, semester, request);
        return map(profileRepository.save(profile));
    }

    @Override
    public UserAcademicProfileResponse updateProfile(
            Long userId,
            UserAcademicProfileRequest request) {

        UserAcademicProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        University university = findUniversity(request.getUniversityId());
        College college = findCollege(request.getCollegeId());
        Branch branch = findBranch(request.getBranchId());
        AcademicYear academicYear = findAcademicYear(request.getAcademicYearId());
        Semester semester = findSemester(request.getSemesterId());

        validateAcademicHierarchy(
                university,
                college,
                branch,
                academicYear,
                semester);

        applyProfileData(profile, university, college, branch, academicYear, semester, request);
        profile.setProfileVersion(profile.getProfileVersion() + 1);

        return map(profileRepository.save(profile));
    }

    private University findUniversity(String publicId) {
        return universityRepository.findById(PublicIdUtils.parseUniversityId(publicId))
                .orElseThrow(() -> new ResourceNotFoundException("University not found"));
    }

    private College findCollege(String publicId) {
        return collegeRepository.findById(PublicIdUtils.parseCollegeId(publicId))
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));
    }

    private Branch findBranch(String publicId) {
        return branchRepository.findById(PublicIdUtils.parseBranchId(publicId))
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
    }

    private AcademicYear findAcademicYear(String publicId) {
        return academicYearRepository.findById(PublicIdUtils.parseAcademicYearId(publicId))
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found"));
    }

    private Semester findSemester(String publicId) {
        return semesterRepository.findById(PublicIdUtils.parseSemesterId(publicId))
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found"));
    }

    private void validateAcademicHierarchy(
            University university,
            College college,
            Branch branch,
            AcademicYear academicYear,
            Semester semester) {

        if (!college.getUniversity().getId().equals(university.getId())) {
            throw new BadRequestException(
                    "Selected college does not belong to selected university");
        }

        if (!branch.getUniversity().getId().equals(university.getId())) {
            throw new BadRequestException(
                    "Selected branch does not belong to selected university");
        }

        if (!collegeBranchRepository.existsByCollegeIdAndBranchId(
                college.getId(), branch.getId())) {
            throw new BadRequestException(
                    "Selected college does not offer selected branch");
        }

        if (!branch.getAcademicYear().getId().equals(academicYear.getId())) {
            throw new BadRequestException(
                    "Branch does not belong to selected academic year");
        }

        if (!semester.getAcademicYear().getId().equals(academicYear.getId())) {
            throw new BadRequestException(
                    "Semester does not belong to selected academic year");
        }
    }

    private void applyProfileData(
            UserAcademicProfile profile,
            University university,
            College college,
            Branch branch,
            AcademicYear academicYear,
            Semester semester,
            UserAcademicProfileRequest request) {

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
    }
}
