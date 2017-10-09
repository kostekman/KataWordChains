package com.koscinsa.wordchains.strategies;

import com.koscinsa.wordchains.utilities.FileLoader;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PathSearcherFactory {
    private final Set<String> words;

    public PathSearcherFactory(String dictionaryPath) throws IOException {
        words = initializeWordsSet(dictionaryPath);
    }

    public enum PathSearcherType {
        STEP_BY_STEP
    }

    public PathSearcher getPathSearcher(PathSearcherType type){
        if(PathSearcherType.STEP_BY_STEP.equals(type)){
            return new StepByStepPathSearcher(new HashSet<>(words));
        }

        return null;
    }

    public static Set<String> initializeWordsSet(String dictionaryPath) throws IOException {
        return FileLoader.readFromInputStream(dictionaryPath);
    }
}
