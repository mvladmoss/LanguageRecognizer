package com.bsuir.iyazis.languagerecognizer.service;

import com.bsuir.iyazis.languagerecognizer.model.dto.AlphabeticMethodRecognitionResult;
import com.bsuir.iyazis.languagerecognizer.model.dto.NgramMethodRecognitionResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface RecognizerService {

    NgramMethodRecognitionResult recognizeByNgramMethod(MultipartFile file);

    AlphabeticMethodRecognitionResult recognizeByAlphabeticMethod(MultipartFile file);

    Map<String, Integer> buildNgramFrequencyMap(String text);
}
