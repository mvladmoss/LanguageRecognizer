package com.bsuir.iyazis.languagerecognizer.service.impl;

import com.bsuir.iyazis.languagerecognizer.model.dto.FrequencyMethodRecognitionResult;
import com.bsuir.iyazis.languagerecognizer.model.mdoel.Language;
import com.bsuir.iyazis.languagerecognizer.service.FrequencyRecognizerService;
import com.bsuir.iyazis.languagerecognizer.service.parser.HTMLParser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FrequencyRecognizerServiceImpl implements FrequencyRecognizerService {

    private static final int NUM_OF_WORDS = 3;
    private static final String WORDS_SPLITTER = "[^а-яa-z]+";
    private static final String ENGLISH_PATH = System.getProperty("user.dir") + "/examples/english.txt";
    private static final String SPANISH_PATH = System.getProperty("user.dir") + "/examples/spanish.txt";

    private final Map<String, Integer> englishWords = getBaseWords(new File(ENGLISH_PATH));
    private final Map<String, Integer> spanishWords = getBaseWords(new File(SPANISH_PATH));
    private final double englishTotal = totalWordsInMap(englishWords);
    private final double spanishTotal = totalWordsInMap(spanishWords);

    private final HTMLParser htmlParser;

    @SneakyThrows
    @Override
    public FrequencyMethodRecognitionResult recognize(MultipartFile file) {
        String dirtyText = htmlParser.parse(new String(file.getBytes()));
        String[] words = Arrays.stream(dirtyText.split(WORDS_SPLITTER))
                .filter(word -> !word.equals(""))
                .toArray(String[]::new);

        Set<String> englishWordsInText = new HashSet<>();
        Set<String> spanishWordsInText = new HashSet<>();

        for(String word : words) {
            if(englishWords.containsKey(word)) {
                englishWordsInText.add(word);
            }
            if(spanishWords.containsKey(word)) {
                spanishWordsInText.add(word);
            }
        }

        double englishFrequency = englishWordsInText.size()/ englishTotal;
        double spanishFrequency = spanishWordsInText.size()/spanishTotal;

        FrequencyMethodRecognitionResult recognitionData = new FrequencyMethodRecognitionResult();
        recognitionData.setLanguage(englishFrequency > spanishFrequency ? Language.ENGLISH : Language.SPANISH);
        recognitionData.setEnglishWordsFrequency(englishFrequency);
        recognitionData.setSpanishWordsFrequency(spanishFrequency);

        return recognitionData;
    }

    private Map<String, Integer> getBaseWords(File file) {
        String text = getFileContent(file);
        String[] words = text.split("\n");
        Map<String, Integer> wordsMap = new HashMap<>();
        for (String word : words) {
            if(wordsMap.containsKey(word)) {
                wordsMap.put(word, wordsMap.get(word) + 1);
            } else {
                wordsMap.put(word, 1);
            }
        }

        return wordsMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private String getFileContent(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e){
            log.error("Can't read file content: {}", file.getName());
        }
        return "";
    }

    private double totalWordsInMap(Map<String, Integer> map) {
        double res = 0;
        for(int number : map.values()) {
            res += number;
        }

        return res;
    }
}
