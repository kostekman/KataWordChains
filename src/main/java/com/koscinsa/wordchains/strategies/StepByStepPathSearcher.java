package com.koscinsa.wordchains.strategies;

import fj.data.Either;

import java.util.*;
import java.util.stream.Collectors;

public class StepByStepPathSearcher implements PathSearcher {

    private final Set<String> words;
    private Set<String> wordsForCurrentUse;

    public StepByStepPathSearcher(Set<String> words){
        this.words = words;
    }

    public List<String> findPath(String startWord, String endWord) {
        wordsForCurrentUse = words.stream().filter(word -> word.length() == startWord.length()).collect(Collectors.toSet());
        wordsForCurrentUse.remove(startWord);
        return findPathWithList(startWord, endWord);
    }

    private List<String> findPathWithList(String startWord, String endWord) {

        Either<List<String>, List<List<String>>> paths = Either.right(List.of(new ArrayList<>(List.of(startWord))));

        while(paths.isRight() && paths.right().value().size() > 0){
            paths = findNextStepsForPaths(paths, endWord);
        }
        if(paths.isRight()){
            return List.of("Not found");
        }
        return paths.left().value();
    }

    private Either<List<String>, List<List<String>>> findNextStepsForPaths(Either<List<String>, List<List<String>>> listOfPaths, String endWord){
        List<List<String>> newListOfPaths = new ArrayList<>();

        for(List<String> list : listOfPaths.right().value()){
            List<String> candidates = findCandidateWords(list.get(list.size() - 1));
            if(candidates.contains(endWord)){
                list.add(endWord);
                return Either.left(list);
            }
            addPathsForCandidates(newListOfPaths, list, candidates);
        }
        return Either.right(newListOfPaths);
    }

    private void addPathsForCandidates(List<List<String>> listOfPaths, List<String> list, List<String> candidates){
        wordsForCurrentUse.removeAll(candidates);
        candidates.stream().forEach(candidate -> {
            List<String> newPath = new ArrayList<>(list);
            newPath.add(candidate);
            listOfPaths.add(newPath);
        });
    }

    private List<String> findCandidateWords(String startWord) {
        return wordsForCurrentUse.parallelStream().filter(word -> isWordDifferentByOneCharFromStartWord(word, startWord)
        ).collect(Collectors.toList());
    }

    private boolean isWordDifferentByOneCharFromStartWord(String word, String startWord){
        int count = 0;
        for(int i = 0; i < startWord.length(); i++){
            if(word.charAt(i) != startWord.charAt(i)){
                count++;
            }
            if(count > 1){
                return false;
            }
        }
        return true;
    }

    public Set<String> getWords() {
        return new HashSet<>(words);
    }
}
