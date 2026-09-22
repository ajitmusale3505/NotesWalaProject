package com.edunest.backend.modules.feed.dto.response;

import java.util.List;

import com.edunest.backend.modules.resource.dto.response.ResourceResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedResponse {

    private Long userId;
    private String userName;
    private String branchName;
    private String semesterName;
    private Integer resourceCount;

    private List<ResourceResponse> resources;
}