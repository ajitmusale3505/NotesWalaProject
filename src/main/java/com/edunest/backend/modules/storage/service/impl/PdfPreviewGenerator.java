package com.edunest.backend.modules.storage.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import lombok.Builder;
import lombok.Getter;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
public class PdfPreviewGenerator {
    public GeneratedPreview generate(byte[] pdfBytes){
        if(pdfBytes==null||pdfBytes.length<5||!Arrays.equals(Arrays.copyOf(pdfBytes,5),"%PDF-".getBytes(StandardCharsets.US_ASCII))) throw new BadRequestException("Uploaded file is not a PDF");
        try(PDDocument document=Loader.loadPDF(pdfBytes)){
            int pageCount=document.getNumberOfPages(); if(pageCount==0)throw new BadRequestException("PDF contains no pages");
            BufferedImage image=new PDFRenderer(document).renderImageWithDPI(0,120,ImageType.RGB);
            ByteArrayOutputStream output=new ByteArrayOutputStream(); if(!ImageIO.write(image,"png",output))throw new IllegalStateException("Unable to generate PDF preview");
            return GeneratedPreview.builder().content(output.toByteArray()).pageCount(pageCount).previewPages(1).build();
        }catch(BadRequestException ex){throw ex;}catch(IOException|RuntimeException ex){throw new BadRequestException("Uploaded PDF could not be parsed");}
    }
    @Getter @Builder public static class GeneratedPreview{private byte[] content;private int pageCount;private int previewPages;}
}