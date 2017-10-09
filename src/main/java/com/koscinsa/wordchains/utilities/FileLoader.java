package com.koscinsa.wordchains.utilities;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public final class FileLoader {

    public static Set<String> readFromInputStream(String fileName) throws IOException {

        Set<String> setOfWords = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.lines().forEach(word -> setOfWords.add(word));
        }
        return setOfWords;
    }
}
