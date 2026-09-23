package com.edunest.backend.modules.resource.entity;

import com.edunest.backend.common.enums.FileType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resource_files", indexes = {
        @Index(name = "idx_resource_files_resource", columnList = "resource_id"),
        @Index(name = "idx_resource_files_storage_key", columnList = "storage_key")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
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