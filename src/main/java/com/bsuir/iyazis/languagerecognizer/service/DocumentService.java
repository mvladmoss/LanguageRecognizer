package com.bsuir.iyazis.languagerecognizer.service;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    public void storeDocument(MultipartFile file, String language);
}
