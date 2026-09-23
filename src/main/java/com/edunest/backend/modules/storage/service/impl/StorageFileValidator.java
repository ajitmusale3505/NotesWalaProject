package com.edunest.backend.modules.storage.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Locale;

public final class StorageFileValidator {
    private StorageFileValidator() {}
    public static void validatePdf(MultipartFile file,long maxBytes){
        validateRequired(file,maxBytes);
        String type=normalize(file.getContentType());
        if(!"application/pdf".equals(type)) throw new BadRequestException("Only PDF files are allowed");
        if(!extension(file.getOriginalFilename()).equals(".pdf")) throw new BadRequestException("PDF file must use the .pdf extension");
        if(!hasPdfSignature(file)) throw new BadRequestException("File content is not a valid PDF");
    }
    public static void validateImage(MultipartFile file,long maxBytes){
        validateRequired(file,maxBytes); String type=normalize(file.getContentType());
        if(!isImage(type)) throw new BadRequestException("Only JPEG, PNG and WebP images are allowed");
        byte[] h=readHeader(file,16); boolean valid=switch(type){
            case "image/jpeg" -> h.length>=3&&(h[0]&255)==0xff&&(h[1]&255)==0xd8&&(h[2]&255)==0xff;
            case "image/png" -> h.length>=8&&h[0]==(byte)0x89&&h[1]==0x50&&h[2]==0x4e&&h[3]==0x47&&h[4]==0x0d&&h[5]==0x0a&&h[6]==0x1a&&h[7]==0x0a;
            case "image/webp" -> h.length>=12&&h[0]=='R'&&h[1]=='I'&&h[2]=='F'&&h[3]=='F'&&h[8]=='W'&&h[9]=='E'&&h[10]=='B'&&h[11]=='P';
            default -> false;};
        if(!valid) throw new BadRequestException("File content does not match the declared image type");
    }
    public static void validateGeneric(MultipartFile file,long maxBytes){
        validateRequired(file,maxBytes); String type=normalize(file.getContentType());
        if("application/pdf".equals(type)) validatePdf(file,maxBytes); else if(isImage(type)) validateImage(file,maxBytes); else throw new BadRequestException("Unsupported file type");
    }
    private static void validateRequired(MultipartFile file,long maxBytes){if(file==null||file.isEmpty())throw new BadRequestException("File is required");if(file.getSize()>maxBytes)throw new BadRequestException("File exceeds the maximum allowed size");}
    private static boolean hasPdfSignature(MultipartFile file){byte[] h=readHeader(file,5);return h.length==5&&h[0]=='%'&&h[1]=='P'&&h[2]=='D'&&h[3]=='F'&&h[4]=='-';}
    private static byte[] readHeader(MultipartFile file,int size){try{return file.getInputStream().readNBytes(size);}catch(IOException ex){throw new BadRequestException("Unable to read uploaded file");}}
    private static String normalize(String value){return value==null?"":value.toLowerCase(Locale.ROOT).trim();}
    private static String extension(String name){if(name==null)return "";String n=name.toLowerCase(Locale.ROOT);int dot=n.lastIndexOf('.');return dot<0?"":n.substring(dot);}
    private static boolean isImage(String type){return "image/jpeg".equals(type)||"image/png".equals(type)||"image/webp".equals(type);}
}