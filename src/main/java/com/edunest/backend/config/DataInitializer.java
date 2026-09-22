package com.edunest.backend.config;

import java.math.BigDecimal;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.edunest.backend.common.enums.ExamType;
import com.edunest.backend.common.enums.RoleType;
import com.edunest.backend.common.enums.SubjectCategory;
import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.branch.repository.BranchRepository;
import com.edunest.backend.modules.category.entity.Category;
import com.edunest.backend.modules.category.repository.CategoryRepository;
import com.edunest.backend.modules.role.entity.Role;
import com.edunest.backend.modules.role.repository.RoleRepository;
import com.edunest.backend.modules.semester.entity.Semester;
import com.edunest.backend.modules.semester.repository.SemesterRepository;
import com.edunest.backend.modules.subject.entity.Subject;
import com.edunest.backend.modules.subject.repository.SubjectRepository;
import com.edunest.backend.modules.subscription.entity.SubscriptionPlan;
import com.edunest.backend.modules.subscription.repository.SubscriptionPlanRepository;

import com.edunest.backend.modules.university.entity.University;
import com.edunest.backend.modules.university.repository.UniversityRepository;	

import com.edunest.backend.modules.college.entity.College;
import com.edunest.backend.modules.college.repository.CollegeRepository;
import com.edunest.backend.modules.collegebranch.entity.CollegeBranch;
import com.edunest.backend.modules.collegebranch.repository.CollegeBranchRepository;
import com.edunest.backend.modules.year.entity.AcademicYear;
import com.edunest.backend.modules.year.repository.AcademicYearRepository;


import org.springframework.security.crypto.password.PasswordEncoder;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final BranchRepository branchRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UniversityRepository universityRepository;
    private final CollegeRepository collegeRepository;
    private final AcademicYearRepository academicYearRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CollegeBranchRepository collegeBranchRepository;	

    @Value("${app.bootstrap.seed-demo:false}")
    private boolean seedDemoUsers;

    @Value("${app.bootstrap.admin-password:}")
    private String adminPassword;

    @Value("${app.bootstrap.demo-user-password:}")
    private String demoUserPassword;

    @Value("${app.bootstrap.demo-contributor-password:}")
    private String demoContributorPassword;

    public DataInitializer(
            RoleRepository roleRepository,
            CategoryRepository categoryRepository,
            BranchRepository branchRepository,
            SemesterRepository semesterRepository,
            SubjectRepository subjectRepository,
            SubscriptionPlanRepository subscriptionPlanRepository,
            CollegeRepository collegeRepository,
            AcademicYearRepository academicYearRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            CollegeBranchRepository collegeBranchRepository,
            UniversityRepository universityRepository) {

        this.roleRepository = roleRepository;
        this.categoryRepository = categoryRepository;
        this.branchRepository = branchRepository;
        this.semesterRepository = semesterRepository;
        this.subjectRepository = subjectRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.universityRepository = universityRepository;
        this.academicYearRepository = academicYearRepository;
        this.collegeRepository = collegeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.collegeBranchRepository = collegeBranchRepository;
    }

    @Override
    public void run(String... args) {

        // Roles
        seedRole(RoleType.ADMIN);
        seedRole(RoleType.USER);
        seedRole(RoleType.CONTRIBUTOR);

        // Core Catalog
        seedCategories();
        seedUniversities();
        seedAcademicYears();
        seedColleges();	
        seedBranches();
        seedCollegeBranches();
        seedSemesters();
        seedSubjects();
        seedUsers();

        // Commerce
        seedSubscriptionPlans();
    }

    private void seedRole(RoleType roleType) {
        roleRepository.findByName(roleType)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleType);
                    return roleRepository.save(role);
                });
    }

    private void seedCategories() {

        if (categoryRepository.count() > 0) {
            return;
        }

        categoryRepository.saveAll(List.of(

                Category.builder()
                        .name("Raw Previous Year Papers")
                        .slug("raw-pyq")
                        .description("Original university previous year question papers.")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Class Notes")
                        .slug("raw-notes")
                        .description("Faculty and handwritten class notes.")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Lab Manual")
                        .slug("lab-manual")
                        .description("Official laboratory manuals.")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Solved Previous Year Papers")
                        .slug("solved-pyq")
                        .description("Solved previous year question papers.")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Premium Notes")
                        .slug("premium-notes")
                        .description("Premium curated study notes.")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Lab Manual with Codes")
                        .slug("lab-manual-codes")
                        .description("Lab manuals with complete source code.")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Viva Questions")
                        .slug("viva-questions")
                        .description("Frequently asked viva questions and answers.")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Reference Books")
                        .slug("book-pdf")
                        .description("Reference books in PDF format.")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Formula Sheets")
                        .slug("formula-sheet")
                        .description("Quick revision formula sheets.")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("MCQ Bank")
                        .slug("mcq-bank")
                        .description("Practice multiple choice questions.")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Very Important Questions")
                        .slug("vimp-questions")
                        .description("Frequently repeated important questions.")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Mock Tests")
                        .slug("mock-test")
                        .description("Full-length mock examinations.")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Placement Pack")
                        .slug("placement-pack")
                        .description("Placement preparation resources.")
                        .active(true)
                        .build()

        ));
    }

    private void seedBranches() {

        if (branchRepository.count() > 0) {
            return;
        }

        University sppu = universityRepository
                .findByShortCode("SPPU")
                .orElseThrow(() ->
                        new RuntimeException("SPPU not found"));

        AcademicYear year2019 = academicYearRepository
                .findByCode("SPPU-2019")
                .orElseThrow(() ->
                        new RuntimeException("SPPU-2019 not found"));

        // SPPU 2019 Pattern Branches
        branchRepository.save(
                Branch.builder()
                        .name("Computer Engineering")
                        .code("COMP")
                        .active(true)
                        .university(sppu)
                        .academicYear(year2019)
                        .build()
        );

        branchRepository.save(
                Branch.builder()
                        .name("Information Technology")
                        .code("IT")
                        .active(true)
                        .university(sppu)
                        .academicYear(year2019)
                        .build()
        );

        branchRepository.save(
                Branch.builder()
                        .name("Artificial Intelligence and Data Science")
                        .code("AIDS")
                        .active(true)
                        .university(sppu)
                        .academicYear(year2019)
                        .build()
        );

        branchRepository.save(
                Branch.builder()
                        .name("Artificial Intelligence and Machine Learning")
                        .code("AIML")
                        .active(true)
                        .university(sppu)
                        .academicYear(year2019)
                        .build()
        );

        branchRepository.save(
                Branch.builder()
                        .name("Electronics and Telecommunication")
                        .code("ENTC")
                        .active(true)
                        .university(sppu)
                        .academicYear(year2019)
                        .build()
        );

        branchRepository.save(
                Branch.builder()
                        .name("Mechanical Engineering")
                        .code("MECH")
                        .active(true)
                        .university(sppu)
                        .academicYear(year2019)
                        .build()
        );

        branchRepository.save(
                Branch.builder()
                        .name("Civil Engineering")
                        .code("CIVIL")
                        .active(true)
                        .university(sppu)
                        .academicYear(year2019)
                        .build()
        );

        branchRepository.save(
                Branch.builder()
                        .name("Electrical Engineering")
                        .code("ELEC")
                        .active(true)
                        .university(sppu)
                        .academicYear(year2019)
                        .build()
        );
    }
    
    
    private void seedCollegeBranches() {

        if (collegeBranchRepository.count() > 0) {
            return;
        }

        College svcet = collegeRepository.findByCode("SVCET").orElseThrow();
        College coep = collegeRepository.findByCode("COEP").orElseThrow();
        College pccoe = collegeRepository.findByCode("PCCOE").orElseThrow();

        Branch comp = branchRepository.findByCode("COMP").orElseThrow();
        Branch it = branchRepository.findByCode("IT").orElseThrow();
        Branch aids = branchRepository.findByCode("AIDS").orElseThrow();
        Branch aiml = branchRepository.findByCode("AIML").orElseThrow();
        Branch entc = branchRepository.findByCode("ENTC").orElseThrow();
        Branch mech = branchRepository.findByCode("MECH").orElseThrow();
        Branch civil = branchRepository.findByCode("CIVIL").orElseThrow();
        Branch elec = branchRepository.findByCode("ELEC").orElseThrow();

        collegeBranchRepository.save(
                CollegeBranch.builder()
                        .college(svcet)
                        .branch(comp)
                        .active(true)
                        .build());

        collegeBranchRepository.save(
                CollegeBranch.builder()
                        .college(svcet)
                        .branch(it)
                        .active(true)
                        .build());

        collegeBranchRepository.save(
                CollegeBranch.builder()
                        .college(svcet)
                        .branch(aids)
                        .active(true)
                        .build());

        collegeBranchRepository.save(
                CollegeBranch.builder()
                        .college(coep)
                        .branch(comp)
                        .active(true)
                        .build());

        collegeBranchRepository.save(
                CollegeBranch.builder()
                        .college(coep)
                        .branch(mech)
                        .active(true)
                        .build());

        collegeBranchRepository.save(
                CollegeBranch.builder()
                        .college(coep)
                        .branch(civil)
                        .active(true)
                        .build());

        collegeBranchRepository.save(
                CollegeBranch.builder()
                        .college(coep)
                        .branch(elec)
                        .active(true)
                        .build());

        collegeBranchRepository.save(
                CollegeBranch.builder()
                        .college(pccoe)
                        .branch(comp)
                        .active(true)
                        .build());

        collegeBranchRepository.save(
                CollegeBranch.builder()
                        .college(pccoe)
                        .branch(it)
                        .active(true)
                        .build());

        collegeBranchRepository.save(
                CollegeBranch.builder()
                        .college(pccoe)
                        .branch(aiml)
                        .active(true)
                        .build());

        collegeBranchRepository.save(
                CollegeBranch.builder()
                        .college(pccoe)
                        .branch(entc)
                        .active(true)
                        .build());
    }
    
    private void seedUsers() {
        if (!seedDemoUsers || userRepository.count() > 0) {
            return;
        }

        if (adminPassword == null || adminPassword.length() < 12
                || demoUserPassword == null || demoUserPassword.length() < 12
                || demoContributorPassword == null || demoContributorPassword.length() < 12) {
            throw new IllegalStateException(
                    "Demo user seeding is enabled but bootstrap passwords are missing or too short");
        }

        Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role missing"));
        Role userRole = roleRepository.findByName(RoleType.USER)
                .orElseThrow(() -> new IllegalStateException("USER role missing"));
        Role contributorRole = roleRepository.findByName(RoleType.CONTRIBUTOR)
                .orElseThrow(() -> new IllegalStateException("CONTRIBUTOR role missing"));

        userRepository.save(User.builder()
                .fullName("System Admin")
                .email("admin@edunest.com")
                .passwordHash(passwordEncoder.encode(adminPassword))
                .enabled(true).role(adminRole).build());

        userRepository.save(User.builder()
                .fullName("Demo User")
                .email("user@edunest.com")
                .passwordHash(passwordEncoder.encode(demoUserPassword))
                .enabled(true).role(userRole).build());

        userRepository.save(User.builder()
                .fullName("Demo Contributor")
                .email("contributor@edunest.com")
                .passwordHash(passwordEncoder.encode(demoContributorPassword))
                .enabled(true).role(contributorRole).build());
    }

    private void seedSemesters() {
        if (semesterRepository.count() == 0) {

            AcademicYear year2019 = academicYearRepository
                    .findByCode("SPPU-2019")
                    .orElseThrow(() ->
                            new RuntimeException("SPPU-2019 not found"));

            for (int i = 1; i <= 8; i++) {
                semesterRepository.save(
                        Semester.builder()
                                .number(i)
                                .name("Semester " + i)
                                .active(true)
                                .academicYear(year2019)
                                .build()
                );
            }
        }
    }

    private void seedSubjects() {

        if (subjectRepository.count() > 0) {
            return;
        }

        Branch comp = branchRepository.findByCode("COMP")
                .orElseThrow(() -> new IllegalStateException("COMP branch not found"));

        Semester sem5 = semesterRepository.findByNumberAndAcademicYear_Code(5, "SPPU-2019")
                .orElseThrow(() -> new IllegalStateException("Semester 5 not found"));

        AcademicYear year = academicYearRepository
                .findByCode("SPPU-2019")
                .orElseThrow();

        // DBMS
        subjectRepository.save(
                Subject.builder()
                        .name("Database Management Systems")
                        .code("410241")
                        .active(true)
                        .subjectCategory(SubjectCategory.CORE)
                        .examType(ExamType.THEORY_ONLY)
                        .lectureHours(3)
                        .tutorialHours(1)
                        .practicalHours(0)
                        .inSemMarks(30)
                        .endSemMarks(70)
                        .practicalMarks(0)
                        .oralMarks(0)
                        .termWorkMarks(0)
                        .credits(4)
                        .elective(false)
                        .honors(false)
                        .minor(false)
                        .branch(comp)
                        .semester(sem5)
                        .academicYear(year)
                        .build()
        );

        // TOC
        subjectRepository.save(
                Subject.builder()
                        .name("Theory of Computation")
                        .code("410242")
                        .active(true)
                        .subjectCategory(SubjectCategory.CORE)
                        .examType(ExamType.THEORY_ONLY)
                        .lectureHours(3)
                        .tutorialHours(1)
                        .practicalHours(0)
                        .inSemMarks(30)
                        .endSemMarks(70)
                        .credits(4)
                        .elective(false)
                        .honors(false)
                        .minor(false)
                        .branch(comp)
                        .semester(sem5)
                        .academicYear(year)
                        .build()
        );

        // SPOS
        subjectRepository.save(
                Subject.builder()
                        .name("System Programming and Operating System")
                        .code("410243")
                        .active(true)
                        .subjectCategory(SubjectCategory.CORE)
                        .examType(ExamType.THEORY_PRACTICAL_ORAL)
                        .lectureHours(3)
                        .tutorialHours(0)
                        .practicalHours(2)
                        .inSemMarks(30)
                        .endSemMarks(70)
                        .practicalMarks(50)
                        .oralMarks(50)
                        .credits(5)
                        .elective(false)
                        .honors(false)
                        .minor(false)
                        .branch(comp)
                        .semester(sem5)
                        .academicYear(year)
                        .build()
        );

        // CNS
        subjectRepository.save(
                Subject.builder()
                        .name("Computer Networks and Security")
                        .code("410244")
                        .active(true)
                        .subjectCategory(SubjectCategory.CORE)
                        .examType(ExamType.THEORY_PRACTICAL)
                        .lectureHours(3)
                        .tutorialHours(0)
                        .practicalHours(2)
                        .inSemMarks(30)
                        .endSemMarks(70)
                        .practicalMarks(50)
                        .credits(4)
                        .elective(false)
                        .honors(false)
                        .minor(false)
                        .branch(comp)
                        .semester(sem5)
                        .academicYear(year)
                        .build()
        );
    }

    private void seedSubscriptionPlans() {

        if (subscriptionPlanRepository.count() > 0) {
            return;
        }

        // FREE
        subscriptionPlanRepository.save(
                SubscriptionPlan.builder()
                        .name("Free")
                        .slug("free")
                        .description("Basic free access")

                        .monthlyPrice(BigDecimal.ZERO)
                        .yearlyPrice(null)

                        .validityDays(null)
                        .maxSelectableSubjects(0)
                        .maxSelectableSubjectsForFinalSemester(0)

                        .solvedPyqAccess(false)
                        .premiumNotesAccess(false)
                        .labManualCodesAccess(false)
                        .vivaQuestionsAccess(false)
                        .bookPdfAccess(false)
                        .formulaSheetAccess(false)
                        .mcqBankAccess(false)
                        .vimpQuestionsAccess(false)
                        .mockTestAccess(false)

                        .aiAccess(false)
                        .chatAccess(false)
                        .placementPackAccess(false)

                        .allSubjectsAccess(false)
                        .allSemestersAccess(false)
                        .allBranchesAccess(false)

                        .active(true)
                        .build()
        );

        // TURBO
        subscriptionPlanRepository.save(
                SubscriptionPlan.builder()
                        .name("Turbo")
                        .slug("turbo")
                        .description("Short-term premium access")

                        .monthlyPrice(new BigDecimal("49"))
                        .yearlyPrice(null)

                        .validityDays(25)

                        .maxSelectableSubjects(2)
                        .maxSelectableSubjectsForFinalSemester(1)

                        .solvedPyqAccess(true)
                        .premiumNotesAccess(true)
                        .labManualCodesAccess(false)
                        .vivaQuestionsAccess(true)
                        .bookPdfAccess(true)
                        .formulaSheetAccess(false)
                        .mcqBankAccess(false)
                        .vimpQuestionsAccess(false)
                        .mockTestAccess(false)

                        .aiAccess(false)
                        .chatAccess(false)
                        .placementPackAccess(false)

                        .allSubjectsAccess(false)
                        .allSemestersAccess(false)
                        .allBranchesAccess(false)

                        .active(true)
                        .build()
        );

        // PRO MAX
        subscriptionPlanRepository.save(
                SubscriptionPlan.builder()
                        .name("Pro Max")
                        .slug("pro-max")
                        .description("Full premium access")

                        .monthlyPrice(new BigDecimal("99"))
                        .yearlyPrice(null)

                        .validityDays(30)

                        .maxSelectableSubjects(null)
                        .maxSelectableSubjectsForFinalSemester(null)

                        .solvedPyqAccess(true)
                        .premiumNotesAccess(true)
                        .labManualCodesAccess(true)
                        .vivaQuestionsAccess(true)
                        .bookPdfAccess(true)
                        .formulaSheetAccess(true)
                        .mcqBankAccess(true)
                        .vimpQuestionsAccess(true)
                        .mockTestAccess(true)

                        .aiAccess(true)
                        .aiCreditsPerPeriod(100)
                        .chatAccess(true)
                        .placementPackAccess(true)

                        .allSubjectsAccess(true)
                        .allSemestersAccess(false)
                        .allBranchesAccess(false)

                        .active(true)
                        .build()
        );

        // YEARLY
        subscriptionPlanRepository.save(
                SubscriptionPlan.builder()
                        .name("Yearly")
                        .slug("yearly")
                        .description("Unlimited yearly access")

                        .monthlyPrice(null)
                        .yearlyPrice(new BigDecimal("380"))

                        .validityDays(365)

                        .maxSelectableSubjects(null)
                        .maxSelectableSubjectsForFinalSemester(null)

                        .solvedPyqAccess(true)
                        .premiumNotesAccess(true)
                        .labManualCodesAccess(true)
                        .vivaQuestionsAccess(true)
                        .bookPdfAccess(true)
                        .formulaSheetAccess(true)
                        .mcqBankAccess(true)
                        .vimpQuestionsAccess(true)
                        .mockTestAccess(true)

                        .aiAccess(true)
                        .aiCreditsPerPeriod(100)
                        .chatAccess(true)
                        .placementPackAccess(true)

                        .allSubjectsAccess(true)
                        .allSemestersAccess(true)
                        .allBranchesAccess(true)

                        .active(true)
                        .build()
        );
    }
    
    private void seedUniversities() {

        if (universityRepository.count() > 0) {
            return;
        }

        universityRepository.save(
                University.builder()
                        .name("Savitribai Phule Pune University")
                        .shortCode("SPPU")
                        .city("Pune")
                        .state("Maharashtra")
                        .country("India")
                        .active(true)
                        .build()
        );

        universityRepository.save(
                University.builder()
                        .name("University of Mumbai")
                        .shortCode("MU")
                        .city("Mumbai")
                        .state("Maharashtra")
                        .country("India")
                        .active(true)
                        .build()
        );

        universityRepository.save(
                University.builder()
                        .name("Shivaji University")
                        .shortCode("SU")
                        .city("Kolhapur")
                        .state("Maharashtra")
                        .country("India")
                        .active(true)
                        .build()
        );
    }
    
    private void seedColleges() {

        if (collegeRepository.count() > 0) {
            return;
        }

        University sppu = universityRepository
                .findAll()
                .stream()
                .filter(u -> "SPPU".equals(u.getShortCode()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("SPPU not found"));

        collegeRepository.save(
                College.builder()
                        .name("Sahyadri Valley College of Engineering and Technology")
                        .code("SVCET")
                        .city("Pune")
                        .state("Maharashtra")
                        .active(true)
                        .university(sppu)
                        .build()
        );

        collegeRepository.save(
                College.builder()
                        .name("College of Engineering Pune")
                        .code("COEP")
                        .city("Pune")
                        .state("Maharashtra")
                        .active(true)
                        .university(sppu)
                        .build()
        );

        collegeRepository.save(
                College.builder()
                        .name("Pimpri Chinchwad College of Engineering")
                        .code("PCCOE")
                        .city("Pune")
                        .state("Maharashtra")
                        .active(true)
                        .university(sppu)
                        .build()
        );
    }
    
    private void seedAcademicYears() {

        if (academicYearRepository.count() > 0) {
            return;
        }

        // Fetch universities
        University sppu = universityRepository.findByShortCode("SPPU")
                .orElseThrow(() -> new RuntimeException("SPPU not found"));

        University mu = universityRepository.findByShortCode("MU")
                .orElseThrow(() -> new RuntimeException("MU not found"));

        academicYearRepository.save(
                AcademicYear.builder()
                        .name("SPPU 2019 Pattern")
                        .code("SPPU-2019")
                        .startYear(2019)
                        .endYear(2024)
                        .active(true)
                        .university(sppu)
                        .build()
        );

        academicYearRepository.save(
                AcademicYear.builder()
                        .name("SPPU 2024 NEP Pattern")
                        .code("SPPU-2024")
                        .startYear(2024)
                        .endYear(2028)
                        .active(true)
                        .university(sppu)
                        .build()
        );

        academicYearRepository.save(
                AcademicYear.builder()
                        .name("Mumbai University 2024 Pattern")
                        .code("MU-2024")
                        .startYear(2024)
                        .endYear(2028)
                        .active(true)
                        .university(mu)
                        .build()
        );
    }
}