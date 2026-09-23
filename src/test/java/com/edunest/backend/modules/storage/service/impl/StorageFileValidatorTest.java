package com.edunest.backend.modules.storage.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class StorageFileValidatorTest {

    @Test
    void acceptsPdfWithMatchingContentTypeAndSignature() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "notes.pdf", "application/pdf", "%PDF-1.7\n".getBytes());

        assertDoesNotThrow(() -> StorageFileValidator.validatePdf(file, 1024));
    }

    @Test
    void rejectsPdfWithWrongContentType() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "notes.pdf", "text/plain", "%PDF-1.7\n".getBytes());

        assertThrows(RuntimeException.class,
                () -> StorageFileValidator.validatePdf(file, 1024));
    }

    @Test
    void rejectsPdfWithSpoofedExtension() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "notes.pdf", "application/pdf", "not-a-pdf".getBytes());

        assertThrows(RuntimeException.class,
                () -> StorageFileValidator.validatePdf(file, 1024));
    }

    @Test
    void rejectsOversizedFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "notes.pdf", "application/pdf", "%PDF-1.7\n".getBytes());

        assertThrows(RuntimeException.class,
                () -> StorageFileValidator.validatePdf(file, 4));
    }

    @Test
    void acceptsPngOnlyWhenSignatureMatches() {
        byte[] pngHeader = new byte[]{
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
        };
        MockMultipartFile file = new MockMultipartFile(
                "file", "thumb.png", "image/png", pngHeader);

        assertDoesNotThrow(() -> StorageFileValidator.validateImage(file, 1024));
    }
}