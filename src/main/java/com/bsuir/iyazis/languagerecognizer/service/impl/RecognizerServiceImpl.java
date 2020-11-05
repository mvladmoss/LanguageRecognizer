package com.bsuir.iyazis.languagerecognizer.service.impl;

import com.bsuir.iyazis.languagerecognizer.configuration.ApplicationConfiguration;
import com.bsuir.iyazis.languagerecognizer.model.dto.AlphabeticMethodRecognitionResult;
import com.bsuir.iyazis.languagerecognizer.model.dto.NgramMethodRecognitionResult;
import com.bsuir.iyazis.languagerecognizer.model.mdoel.Document;
import com.bsuir.iyazis.languagerecognizer.model.mdoel.Language;
import com.bsuir.iyazis.languagerecognizer.repository.DocumentRepository;
import com.bsuir.iyazis.languagerecognizer.service.RecognizerService;
import com.bsuir.iyazis.languagerecognizer.service.exception.ServiceException;
import com.bsuir.iyazis.languagerecognizer.service.parser.HTMLParser;
import com.bsuir.iyazis.languagerecognizer.utils.JsonHelper;
import com.bsuir.iyazis.languagerecognizer.utils.TextUtils;
import lombok.SneakyThrows;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.bsuir.iyazis.languagerecognizer.model.mdoel.Language.ENGLISH;
import static com.bsuir.iyazis.languagerecognizer.model.mdoel.Language.SPANISH;

@Service
public class RecognizerServiceImpl implements RecognizerService {

    private final Integer nGramSize;
    private final Integer maxNGramSize;

    private final DocumentRepository documentRepository;
    private final JsonHelper jsonHelper;
    private final HTMLParser htmlParser;

    public RecognizerServiceImpl(ApplicationConfiguration applicationConfiguration,
                                 DocumentRepository documentRepository,
                                 JsonHelper jsonHelper,
                                 HTMLParser htmlParser) {
        this.nGramSize = applicationConfiguration.getNGramSize();
        this.maxNGramSize = applicationConfiguration.getMaxNGramSize();
        this.documentRepository = documentRepository;
        this.jsonHelper = jsonHelper;
        this.htmlParser = htmlParser;
    }

    @SneakyThrows
    @Override
    @Transactional(readOnly = true)
    public NgramMethodRecognitionResult recognizeByNgramMethod(MultipartFile file) {
        String dirtyText = htmlParser.parse(new String(file.getBytes()));
        Map<String, Integer> targetTextNgramFrequencyMap = this.buildNgramFrequencyMap(dirtyText);
        List<Document> documents = documentRepository.findAll();
        List<NgramMethodRecognitionResult> results = new ArrayList<>();
        for (Document doc : documents) {
            int docRank = 0;
            Map<String, Integer> gramWeights = jsonHelper.parseMap(doc.getCompiledNgrams(), Integer.class);
            for (Map.Entry<String, Integer> entry : targetTextNgramFrequencyMap.entrySet()) {
                String gram = entry.getKey();
                Integer gramWeight = entry.getValue();
                Optional<Integer> docGramRank = Optional.ofNullable(gramWeights.get(gram));
                docRank += Math.abs(docGramRank.orElse(maxNGramSize) - gramWeight);
            }
            results.add(createRecognizeResult(docRank, dirtyText, doc));
        }
        return results.stream()
                .min(Comparator.comparingInt(NgramMethodRecognitionResult::getRank))
                .orElseThrow(ServiceException::new);
    }

    @Override
    @SneakyThrows
    public AlphabeticMethodRecognitionResult recognizeByAlphabeticMethod(MultipartFile file) {
        String dirtyText = htmlParser.parse(new String(file.getBytes()));
        Double englishSymbols = 0d;
        Double spanishSymbols = 0d;
        for (Character symbol : dirtyText.toCharArray()) {
            if (ENGLISH.getAlphabet().contains(symbol.toString().toUpperCase())) {
                englishSymbols++;
            }

            if (SPANISH.getAlphabet().contains(symbol.toString().toUpperCase())) {
                spanishSymbols++;
            }
        }

        Integer textTotalSize = dirtyText.length();
        Double englishAlphabetFrequencyRatio = englishSymbols / textTotalSize;
        Double spanishAlphabetFrequencyRatio = spanishSymbols / textTotalSize;
        Pair<Language, Double> languageToFrequencyRation = englishAlphabetFrequencyRatio >= spanishAlphabetFrequencyRatio
                ? Pair.of(ENGLISH, englishAlphabetFrequencyRatio)
                : Pair.of(SPANISH, spanishAlphabetFrequencyRatio);
        return AlphabeticMethodRecognitionResult.of(languageToFrequencyRation);

    }

    private NgramMethodRecognitionResult createRecognizeResult(Integer rank, String testedDoc, Document foundedDoc) {
        return NgramMethodRecognitionResult.builder()
                .rank(rank)
                .testedDocument(testedDoc)
                .foundedDocument(foundedDoc)
                .language(foundedDoc.getLanguage())
                .build();
    }

    @Override
    public Map<String, Integer> buildNgramFrequencyMap(String text) {
        List<String> distinctFilteredWords = this.receiveClearedDistinctWords(text);

        Map<String, Integer> nGramFrequencyMap = new HashMap<>();
        for (String word : distinctFilteredWords) {
            if (word.length() > nGramSize) {
                int firstSymbol = 0;
                int lastSymbol = nGramSize;
                while (lastSymbol <= word.length()) {
                    String gram = word.substring(firstSymbol, lastSymbol);
                    int gramWeight = gramWeight(distinctFilteredWords, gram);
                    nGramFrequencyMap.put(gram, gramWeight);
                    firstSymbol++;
                    lastSymbol++;
                }
            } else {
                int gramWeight = gramWeight(distinctFilteredWords, word);
                nGramFrequencyMap.put(word, gramWeight);
            }
        }
        return buildNgramLimitedAndSortedFrequencyMap(nGramFrequencyMap, maxNGramSize);
    }

    private List<String> receiveClearedDistinctWords(String dirtyText) {
        String clearedText = TextUtils.clearTextFromAuxiliarySymbols(dirtyText);
        return TextUtils.createStreamOfDistinctWords(clearedText)
                .filter(word -> word.length() >= nGramSize)
                .collect(Collectors.toList());
    }

    private Map<String, Integer> buildNgramLimitedAndSortedFrequencyMap(Map<String, Integer> nGramStructure, Integer maxSize) {
        List<Map.Entry<String, Integer>> sortedAndLimitedGramStructure = nGramStructure.entrySet().stream()
                .sorted((firstEntry, secondEntry) -> Integer.compare(secondEntry.getValue(), firstEntry.getValue()))
                .limit(maxSize)
                .collect(Collectors.toList());

        Map<String, Integer> result = new LinkedHashMap<>();
        int index = sortedAndLimitedGramStructure.size();
        for (Map.Entry<String, Integer> entry : sortedAndLimitedGramStructure) {
            result.put(entry.getKey(), index);
            index--;
        }
        return result;
    }

    private int gramWeight(List<String> words, String gram) {
        return (int) words.stream()
                .filter(checkWord -> checkWord.contains(gram))
                .count();
    }
}
