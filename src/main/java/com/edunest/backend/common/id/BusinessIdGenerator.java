package com.edunest.backend.common.id;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

/**
 * Allocates immutable, human-readable application identifiers.
 *
 * <p>Allocation is serialized per prefix by a database row lock. The
 * frontend never participates in identifier generation.</p>
 */
@Component
public class BusinessIdGenerator {

    private static final long FIRST_VALUE = 10001L;
    private static final Pattern PREFIX_PATTERN =
            Pattern.compile("[A-Z][A-Z0-9_]{1,15}");

    private final JdbcTemplate jdbcTemplate;

    public BusinessIdGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public String nextId(String prefix) {
        validatePrefix(prefix);

        jdbcTemplate.update("""
                INSERT INTO business_id_sequences (prefix, next_value)
                SELECT ?, ?
                WHERE NOT EXISTS (
                    SELECT 1 FROM business_id_sequences WHERE prefix = ?
                )
                """, prefix, FIRST_VALUE, prefix);

        Long currentValue = jdbcTemplate.queryForObject("""
                SELECT next_value
                  FROM business_id_sequences
                 WHERE prefix = ?
                 FOR UPDATE
                """, Long.class, prefix);

        if (currentValue == null || currentValue < FIRST_VALUE) {
            throw new IllegalStateException(
                    "Unable to allocate business identifier for prefix: " + prefix);
        }

        jdbcTemplate.update("""
                UPDATE business_id_sequences
                   SET next_value = ?
                 WHERE prefix = ?
                """, currentValue + 1, prefix);

        return prefix + currentValue;
    }

    private void validatePrefix(String prefix) {
        if (prefix == null || !PREFIX_PATTERN.matcher(prefix).matches()) {
            throw new IllegalArgumentException("Invalid business ID prefix");
        }
    }
}
