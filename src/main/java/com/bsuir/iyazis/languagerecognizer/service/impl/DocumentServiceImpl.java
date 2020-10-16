package com.bsuir.iyazis.languagerecognizer.service.impl;

import com.bsuir.iyazis.languagerecognizer.model.mdoel.Document;
import com.bsuir.iyazis.languagerecognizer.model.mdoel.Language;
import com.bsuir.iyazis.languagerecognizer.repository.DocumentRepository;
import com.bsuir.iyazis.languagerecognizer.service.DocumentService;
import com.bsuir.iyazis.languagerecognizer.service.RecognizerService;
import com.bsuir.iyazis.languagerecognizer.service.exception.ServiceException;
import com.bsuir.iyazis.languagerecognizer.service.parser.HTMLParser;
import com.bsuir.iyazis.languagerecognizer.utils.JsonHelper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final RecognizerService recognizerService;
    private final HTMLParser htmlParser;
    private final JsonHelper jsonHelper;
    private final DocumentRepository documentRepository;

    @SneakyThrows
    @Override
    @Transactional
    public void storeDocument(MultipartFile file, String language) {
        validateFile(file);
        String parsedHtmlText = htmlParser.parse(new String(file.getBytes()));
        Map<String, Integer> ngramFrequencyMap = recognizerService.buildNgramFrequencyMap(parsedHtmlText);
        String ngramFrequencyJson = jsonHelper.toJson(ngramFrequencyMap);

        Document document = Document.builder()
                .text(parsedHtmlText)
                .language(Language.valueOf(language))
                .compiledNgrams(ngramFrequencyJson)
                .build();
        documentRepository.save(document);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ServiceException("Uploaded file is empty.Please try again with another file");
        }
    }
}
