package com.edunest.backend.modules.storage.service.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class PdfPreviewGeneratorTest {

    @Test
    void generatesPreviewFromValidPdf() throws Exception {
        byte[] pdf;
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            document.addPage(new PDPage());
            document.save(output);
            pdf = output.toByteArray();
        }

        PdfPreviewGenerator.GeneratedPreview preview =
                new PdfPreviewGenerator().generate(pdf);

        assertEquals(1, preview.getPageCount());
        assertEquals(1, preview.getPreviewPages());
        assertNotNull(preview.getContent());
        assertTrue(preview.getContent().length > 0);
    }
}