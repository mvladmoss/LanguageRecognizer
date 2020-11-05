package com.bsuir.iyazis.languagerecognizer.model.mdoel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Language {

    ENGLISH("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    SPANISH("ABCDEFGHIJKLMNÑOPQRSTUVWXYZ");

    @Getter
    private String alphabet;
}
