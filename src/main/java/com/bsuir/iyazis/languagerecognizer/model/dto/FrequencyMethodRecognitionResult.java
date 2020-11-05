package com.bsuir.iyazis.languagerecognizer.model.dto;

import com.bsuir.iyazis.languagerecognizer.model.mdoel.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FrequencyMethodRecognitionResult {

    private Language language;
    private Double englishWordsFrequency;
    private Double spanishWordsFrequency;

}
