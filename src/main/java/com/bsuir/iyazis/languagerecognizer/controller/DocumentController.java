package com.bsuir.iyazis.languagerecognizer.controller;

import com.bsuir.iyazis.languagerecognizer.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping
    public void uploadDocument(@RequestParam("file") MultipartFile file,
                               @RequestParam("language") String language) {
        documentService.storeDocument(file, language);

    }
}
