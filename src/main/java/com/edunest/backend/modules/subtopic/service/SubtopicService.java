package com.edunest.backend.modules.subtopic.service;

import com.edunest.backend.modules.subtopic.dto.SubtopicResponse;

import java.util.List;

public interface SubtopicService {
    List<SubtopicResponse> getByTopic(Long topicId);
    SubtopicResponse getById(String id);
}
