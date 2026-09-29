package com.edunest.backend.common.id;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Generates immutable, human-readable application identifiers.
 *
 * <p>The numeric portion is allocated by PostgreSQL under a transaction-safe
 * row lock. The frontend never participates in identifier generation.</p>
 */
@Component
public class BusinessIdGenerator {

    private static final Pattern PREFIX_PATTERN = Pattern.compile("[A-Z][A-Z0-9_]{1,15}");

    private final JdbcTemplate jdbcTemplate;

    public BusinessIdGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public String nextId(String prefix) {
        validatePrefix(prefix);

        jdbcTemplate.update("""
                INSERT INTO business_id_sequences (prefix, next_value)
                VALUES (?, 10001)
                ON CONFLICT (prefix) DO NOTHING
                """, prefix);

        Long value = jdbcTemplate.queryForObject("""
                UPDATE business_id_sequences
                   SET next_value = next_value + 1
                 WHERE prefix = ?
                 RETURNING next_value - 1
                """, Long.class, prefix);

        if (value == null || value < 10001) {
            throw new IllegalStateException("Unable to allocate business identifier for prefix: " + prefix);
        }

        return prefix + value;
    }

    private void validatePrefix(String prefix) {
        if (prefix == null || !PREFIX_PATTERN.matcher(prefix).matches()) {
            throw new IllegalArgumentException("Invalid business ID prefix");
        }
    }
}
