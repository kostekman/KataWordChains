package com.koscinsa.wordchains.strategies;

import java.util.List;
import java.util.Set;

public interface PathSearcher {

    List<String> findPath(String startWord, String endWord);

    Set<String> getWords();
}
