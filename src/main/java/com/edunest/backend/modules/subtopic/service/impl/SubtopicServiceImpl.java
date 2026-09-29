package com.edunest.backend.modules.subtopic.service.impl;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.subtopic.dto.SubtopicResponse;
import com.edunest.backend.modules.subtopic.entity.Subtopic;
import com.edunest.backend.modules.subtopic.repository.SubtopicRepository;
import com.edunest.backend.modules.subtopic.service.SubtopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubtopicServiceImpl implements SubtopicService {

    private final SubtopicRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<SubtopicResponse> getByTopic(Long topicId) {
        return repository.findByTopicIdAndActiveTrueOrderBySubtopicNumberAsc(topicId)
                .stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SubtopicResponse getById(String id) {
        return repository.findByIdAndActiveTrue(id)
                .map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Subtopic not found"));
    }

    private SubtopicResponse map(Subtopic s) {
        return SubtopicResponse.builder()
                .id(s.getId())
                .subtopicNumber(s.getSubtopicNumber())
                .name(s.getName())
                .description(s.getDescription())
                .active(s.isActive())
                .topicId(PublicIdUtils.topicId(s.getTopic().getId()))
                .topicName(s.getTopic().getName())
                .unitId(PublicIdUtils.unitId(s.getTopic().getUnit().getId()))
                .subjectId(s.getTopic().getUnit().getSubject().getBusinessId())
                .build();
    }
}
