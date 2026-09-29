CREATE TABLE assessment_unit_mappings (
    id VARCHAR(20) PRIMARY KEY,
    assessment_component_id VARCHAR(20) NOT NULL,
    unit_id BIGINT NOT NULL,
    coverage_weight INTEGER NOT NULL DEFAULT 100,
    display_order INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,

    CONSTRAINT uk_assessment_unit_mapping UNIQUE (assessment_component_id, unit_id),
    CONSTRAINT ck_assessment_mapping_weight CHECK (coverage_weight >= 0 AND coverage_weight <= 100),
    CONSTRAINT ck_assessment_mapping_order CHECK (display_order > 0),
    CONSTRAINT fk_assessment_mapping_component FOREIGN KEY (assessment_component_id) REFERENCES assessment_components(id),
    CONSTRAINT fk_assessment_mapping_unit FOREIGN KEY (unit_id) REFERENCES syllabus_units(id)
);

CREATE INDEX idx_assessment_mapping_component ON assessment_unit_mappings(assessment_component_id, active);
CREATE INDEX idx_assessment_mapping_unit ON assessment_unit_mappings(unit_id, active);
