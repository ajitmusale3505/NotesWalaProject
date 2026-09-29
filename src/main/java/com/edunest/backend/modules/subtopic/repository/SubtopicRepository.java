package com.edunest.backend.modules.subtopic.repository;

import com.edunest.backend.modules.subtopic.entity.Subtopic;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubtopicRepository extends JpaRepository<Subtopic, String> {

    @EntityGraph(attributePaths = {"topic", "topic.unit", "topic.unit.subject"})
    List<Subtopic> findByTopicIdAndActiveTrueOrderBySubtopicNumberAsc(Long topicId);

    @EntityGraph(attributePaths = {"topic", "topic.unit", "topic.unit.subject"})
    Optional<Subtopic> findByIdAndActiveTrue(String id);

    boolean existsByTopicIdAndSubtopicNumber(Long topicId, Integer subtopicNumber);
}
