package com.edunest.backend.modules.subtopic.controller;

import com.edunest.backend.modules.subtopic.dto.SubtopicResponse;
import com.edunest.backend.modules.subtopic.service.SubtopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subtopics")
@RequiredArgsConstructor
public class SubtopicController {

    private final SubtopicService service;

    @GetMapping("/topic/{topicId}")
    public List<SubtopicResponse> getByTopic(@PathVariable Long topicId) {
        return service.getByTopic(topicId);
    }

    @GetMapping("/{id}")
    public SubtopicResponse getById(@PathVariable String id) {
        return service.getById(id);
    }
}
