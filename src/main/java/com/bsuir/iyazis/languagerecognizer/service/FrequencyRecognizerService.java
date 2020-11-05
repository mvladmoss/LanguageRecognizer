package com.bsuir.iyazis.languagerecognizer.service;

import com.bsuir.iyazis.languagerecognizer.model.dto.FrequencyMethodRecognitionResult;
import org.springframework.web.multipart.MultipartFile;

public interface FrequencyRecognizerService {

    FrequencyMethodRecognitionResult recognize(MultipartFile file);

}
