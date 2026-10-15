package com.edunest.backend.modules.communitychat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCommunityGroupRequest {

    @NotBlank
    private String universityId;

    @NotBlank
    private String branchId;

    @NotBlank
    private String semesterId;

    @NotBlank
    @Size(max = 120)
    private String name;

    @Size(max = 500)
    private String description;

    private Boolean active;
}
