# NotesWala Academic Engine — Part 0 Audit

## Scope

This document records the baseline audit performed before implementing the curriculum-aware academic engine.

Target architecture:

University -> Program -> Branch -> Exam Pattern -> Curriculum -> Semester -> Subject Offering -> Assessment -> Syllabus -> Unit -> Topic -> Subtopic -> Resource

Student resolution:

User -> Student Academic Profile -> Curriculum -> Semester -> Subject Offerings -> Resources

## Audit Result

Status: READY FOR CONTROLLED REFACTOR

The repository already contains several academic modules. They must be evolved and consolidated rather than duplicated.

## Existing Relevant Modules

### Already present

- university
- branch
- college
- collegebranch
- year / AcademicYear
- semester
- subject
- syllabus
- unit
- topic
- userprofile / UserAcademicProfile
- resource
- category

### Existing syllabus depth

The current implementation already has:

Subject -> Unit -> Topic

Subtopic is not currently implemented.

The target model must become:

Subject Offering -> Unit -> Topic -> Subtopic

The syllabus must be attached to the curriculum-specific subject offering, not to a globally reusable Subject record, so different curriculum/pattern versions can coexist safely.

## Critical Findings

### 1. IDs are currently generated Long values

Current entities such as Subject, Branch, Semester, University, AcademicYear, Unit, Topic, UserAcademicProfile and Resource use:

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

This conflicts with the agreed requirement that application identifiers are stored as custom business IDs.

Target examples:

    UNI10001
    PRG10001
    BRN10001
    PAT10001
    CUR10001
    SEM10001
    SUB10001
    SOF10001
    CAT10001
    ELG10001
    ASM10001
    UNT10001
    TOP10001
    STP10001
    RES10001

The existing PublicIdUtils only converts database Long IDs into display strings. It does NOT store the custom IDs as database primary keys.

This must be replaced as part of the controlled migration.

### 2. Subject currently owns curriculum context directly

Current Subject contains:

- branch
- semester
- academicYear
- subjectCategory
- examType
- inSemMarks
- endSemMarks
- practicalMarks
- oralMarks
- termWorkMarks
- credits
- electiveGroup

This mixes reusable subject identity with curriculum-specific offering data.

Target:

    Subject
        |
        +-- SubjectOffering
              |
              +-- CurriculumSemester
              +-- Category
              +-- Credits
              +-- Assessment Components
              +-- Elective Group
              +-- Syllabus

### 3. Assessment is currently denormalized

Current Subject stores individual mark columns:

    inSemMarks
    endSemMarks
    practicalMarks
    oralMarks
    termWorkMarks

This is not sufficient for a curriculum engine.

Target:

    AssessmentComponentType
        |
        +-- CCE
        +-- END_SEM
        +-- TERM_WORK
        +-- PRACTICAL
        +-- ORAL
        +-- VIVA
        +-- PROJECT
        +-- INTERNSHIP

    AssessmentComponent
        |
        +-- SubjectOffering

This supports different assessment structures without schema changes.

### 4. Practical assessment needs Manual + Code

This requirement is now part of the architecture.

A practical subject may contain:

    Practical
        |
        +-- Manual
        +-- Code

Therefore practical assessment/components must not be reduced to one generic practical mark.

The assessment model must be capable of representing:

    TERM_WORK
    PRACTICAL
        - MANUAL
        - CODE
    ORAL / VIVA

The exact marks and component relationships will come from the verified curriculum data for each subject.

### 5. Semester is currently tied to AcademicYear

Current Semester contains:

    academicYear_id

The target model uses reusable semester definitions and connects them to a curriculum through:

    CurriculumSemester

This avoids treating "academic year" as equivalent to "exam pattern/curriculum version".

### 6. AcademicYear is currently overloaded

Current AcademicYear contains fields such as:

    name = BE 2024 Pattern
    code = SPPU-2024
    university_id

This is actually carrying curriculum/pattern semantics.

The target separates:

    ExamPattern
    Curriculum
    AcademicYear

Academic year can still be stored on the student's academic profile or curriculum enrollment where required, but it must not be used as the identity of an exam pattern.

### 7. Branch is currently tied to AcademicYear

Current Branch contains:

    university
    academicYear

Branch should be a master academic branch/program concept.

The relationship:

    Branch -> Curriculum

should determine when and under which pattern the branch is offered.

### 8. Resource currently duplicates academic hierarchy

Resource currently stores:

    university
    branch
    academicYear
    semester
    subject
    college

This creates a second academic hierarchy and makes incorrect combinations possible.

Target resource resolution should be based primarily on:

    Resource
        |
        +-- ResourceAcademicMapping
                |
                +-- SubjectOffering
                +-- optional Unit
                +-- optional Topic
                +-- optional Subtopic

Resource metadata may retain necessary legacy/scope fields during migration, but the new curriculum relationship must become the authoritative academic mapping.

### 9. Current subject filtering is not curriculum-safe

Current APIs filter subjects by:

    branchId
    semesterId

This is insufficient.

A correct lookup must resolve:

    authenticated user
        -> academic profile
        -> curriculum
        -> curriculum semester
        -> subject offerings

The frontend must not be trusted to supply its own branch/semester combination for authorization.

### 10. Current PublicIdUtils is a compatibility/display layer

PublicIdUtils currently maps generated database IDs to strings such as SUB10001.

This means:

    database id = 1
    API id = SUB10001

The agreed architecture requires:

    database primary key = SUB10001

PublicIdUtils should therefore not remain the source of identity after migration. A centralized server-side custom ID generator will be introduced.

### 11. Syllabus currently stops at Topic

Current:

    Subject -> Unit -> Topic

Target:

    SubjectOffering -> Unit -> Topic -> Subtopic

Unit/topic IDs must also become stored custom IDs.

### 12. No Flyway migration system is currently present

The repository currently has no Flyway dependency or db/migration tree.

application.yml currently uses:

    spring.jpa.hibernate.ddl-auto: update

For a production-oriented academic master system, schema evolution should be explicit and versioned.

Planned direction:

    Flyway migrations
    +
    Hibernate ddl-auto=validate

Migration work must be introduced carefully because the existing database may already contain data.

### 13. Existing tests and CI are present

The repository contains service/entity tests and GitHub Actions build configuration.

CI currently runs:

    mvn -B test

The academic refactor must preserve existing tests and add targeted integration tests for:

- custom IDs
- curriculum resolution
- branch/semester isolation
- elective selection
- assessment components
- unit/topic/subtopic hierarchy
- practical Manual/Code components
- unauthorized subject/resource access

## Agreed Target Model

### Master

    universities
    programs
    branches
    exam_patterns
    semesters
    subject_categories
    assessment_component_types

### Curriculum

    curriculums
    curriculum_semesters
    subject_offerings

### Subject

    subjects
    elective_groups
    elective_group_subjects

### Assessment

    assessment_components

### Syllabus

    syllabus_units
    syllabus_topics
    syllabus_subtopics
    syllabus_assessment_coverage

### User

    user_academic_profiles

### Resources

    resources
    resource_academic_mappings

## Custom ID Rules

Business/application IDs will be stored directly in the database.

Examples:

| Entity | Prefix | Example |
|---|---|---|
| University | UNI | UNI10001 |
| Program | PRG | PRG10001 |
| Branch | BRN | BRN10001 |
| Exam Pattern | PAT | PAT10001 |
| Curriculum | CUR | CUR10001 |
| Semester | SEM | SEM10001 |
| Category | CAT | CAT10001 |
| Subject | SUB | SUB10001 |
| Subject Offering | SOF | SOF10001 |
| Elective Group | ELG | ELG10001 |
| Assessment Component | ASM | ASM10001 |
| Unit | UNT | UNT10001 |
| Topic | TOP | TOP10001 |
| Subtopic | STP | STP10001 |
| Resource | RES | RES10001 |

IDs will be generated server-side and must be concurrency-safe.

IDs will not be generated by the frontend.

## Important Identity Separation

Academic subject code and application ID remain separate.

Example:

    id           = SUB10001
    subject_code = PCC-201-COM
    name         = Data Structures

The application ID is immutable.

The university's subject code is stored as academic metadata.

## Practical Assessment Rule

Practical subjects must support component-level structure such as:

    TERM_WORK
    PRACTICAL
        MANUAL
        CODE
    ORAL / VIVA

The exact marks are curriculum data and must not be hardcoded globally.

## Migration Strategy

No destructive rewrite will be performed blindly.

The implementation will proceed in controlled parts:

    Part 0  Existing backend audit                         COMPLETE
    Part 1  Academic master + custom ID foundation
    Part 2  Curriculum + pattern versioning
    Part 3  Subject + SubjectOffering
    Part 4  Assessment architecture
    Part 5  Elective groups
    Part 6  Unit -> Topic -> Subtopic
    Part 7  Assessment -> Unit coverage
    Part 8  User academic subject resolution
    Part 9  Resource academic integration
    Part 10 Seed data + integration tests + hardening

Each part must compile and pass tests before moving to the next.

## Part 0 Decision

Do not duplicate the existing subject/unit/topic modules.

Refactor and migrate them into the target architecture.

Do not change unrelated modules during the academic refactor unless a dependency requires a minimal compatibility change.

## Part 0 Exit Criteria

- Existing academic implementation identified.
- Existing ID strategy identified.
- Existing syllabus depth identified.
- Existing resource coupling identified.
- Existing user academic profile identified.
- Assessment limitations identified.
- Practical Manual/Code requirement recorded.
- Migration/versioning gap identified.
- Target entities and relationships frozen.
- Implementation order frozen.

Part 0 is complete. No production behavior is intentionally changed by this audit document.
