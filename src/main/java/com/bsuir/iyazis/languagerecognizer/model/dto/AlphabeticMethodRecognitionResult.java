package com.bsuir.iyazis.languagerecognizer.model.dto;

import com.bsuir.iyazis.languagerecognizer.model.mdoel.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AlphabeticMethodRecognitionResult {

    private Language language;
    private Double languageAlphabetFrequencyRatio;


    public static AlphabeticMethodRecognitionResult of(Pair<Language, Double> languageToFrequencyRatio) {
        return new AlphabeticMethodRecognitionResult(languageToFrequencyRatio.getFirst(),
                languageToFrequencyRatio.getSecond());
    }
}
