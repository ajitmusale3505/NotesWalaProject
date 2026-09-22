package com.edunest.backend.modules.resource.entity;

import com.edunest.backend.common.enums.FileType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resource_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resource_id")
    private Resource resource;

    private String fileUrl;

    private String originalFileName;

    private String storageKey;

    private String mimeType;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    private Long fileSizeBytes;

    private boolean previewAllowed;
}