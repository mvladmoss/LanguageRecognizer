package com.bsuir.iyazis.languagerecognizer.controller;

import com.bsuir.iyazis.languagerecognizer.model.dto.AlphabeticMethodRecognitionResult;
import com.bsuir.iyazis.languagerecognizer.model.dto.FrequencyMethodRecognitionResult;
import com.bsuir.iyazis.languagerecognizer.model.dto.NgramMethodRecognitionResult;
import com.bsuir.iyazis.languagerecognizer.service.FrequencyRecognizerService;
import com.bsuir.iyazis.languagerecognizer.service.RecognizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/language/recognition")
@RequiredArgsConstructor
public class RecognizeController {

    private final RecognizerService recognizerService;
    private final FrequencyRecognizerService frequencyRecognizerService;

    @GetMapping("/ngram")
    public NgramMethodRecognitionResult recognizeByNgramMethod(@RequestParam("file") MultipartFile file) {
        return recognizerService.recognizeByNgramMethod(file);
    }

    @GetMapping("/alphabet")
    public AlphabeticMethodRecognitionResult recognizeByAlphabeticMethod(@RequestParam("file") MultipartFile file) {
        return recognizerService.recognizeByAlphabeticMethod(file);
    }

    @GetMapping("/frequency")
    public FrequencyMethodRecognitionResult recognizeByFrequencyMethod(@RequestParam("file") MultipartFile file) {
        return frequencyRecognizerService.recognize(file);
    }
}
