package com.bsuir.iyazis.languagerecognizer.model.dto;

import com.bsuir.iyazis.languagerecognizer.model.mdoel.Document;
import com.bsuir.iyazis.languagerecognizer.model.mdoel.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NgramMethodRecognitionResult {

    private Integer rank;
    private Language language;
    private String testedDocument;
    private Document foundedDocument;

}
