package com.bsuir.iyazis.languagerecognizer.service.parser;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

@Component
public class HTMLParserImpl implements HTMLParser {

    @Override
    public String parse(String text) {
        return Jsoup.parse(text).text();
    }
}
