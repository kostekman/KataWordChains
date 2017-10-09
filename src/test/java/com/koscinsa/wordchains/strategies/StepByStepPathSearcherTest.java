package com.koscinsa.wordchains.strategies;

import fj.data.Either;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StepByStepPathSearcherTest {


    PathSearcherFactory pathSearcherFactory = new PathSearcherFactory("src/main/resources/katawordlist.txt");

    StepByStepPathSearcherTest() throws IOException {
    }

    @Test
    void findPathTestLead() {
        final String startWord = "lead";
        final String endWord = "gold";
        List<String> wordPath = pathSearcherFactory.getPathSearcher(PathSearcherFactory.PathSearcherType.STEP_BY_STEP)
                .findPath(startWord, endWord);

        assertTrue(wordPath.size() > 0, "There should be no error for those words");
        assertTrue(wordPath.size() <= 4, "Path is too long.");
    }

    @Test
    void findPathTestRuby() {
        final String startWord = "ruby";
        final String endWord = "code";
        List<String> wordPath = pathSearcherFactory.getPathSearcher(PathSearcherFactory.PathSearcherType.STEP_BY_STEP)
                .findPath(startWord, endWord);

        assertTrue(wordPath.size() > 0, "There should be no error for those words");
        assertTrue(wordPath.size() <= 5, "Path is too long.");
    }

    @Test
    void shouldRemoveWordsWithDifferentLengthFromUsedWordsAndStartWord() throws Exception {
        PathSearcher stepByStepPathSearcher = pathSearcherFactory.getPathSearcher(PathSearcherFactory.PathSearcherType.STEP_BY_STEP);

        Field words = StepByStepPathSearcher.class.getDeclaredField("words");
        words.setAccessible(true);

        List<String> wordsToUse = List.of("and", "tw", "onr", "brbaf", "bub");
        words.set(stepByStepPathSearcher, new HashSet<>(wordsToUse));

        stepByStepPathSearcher.findPath("bub", "and");

        Field wordsForCurrentUse = StepByStepPathSearcher.class.getDeclaredField("wordsForCurrentUse");
        wordsForCurrentUse.setAccessible(true);

        Set<String> wordsForCurrentUseValue = (Set<String>) wordsForCurrentUse.get(stepByStepPathSearcher);

        assertEquals(Set.of("and", "onr"), wordsForCurrentUseValue);
    }

    @Test
    void shouldReturnNotFoundWhenResultCannotBeFound() throws Exception {
        Method method = StepByStepPathSearcher.class.getDeclaredMethod("findPathWithList", String.class, String.class);
        method.setAccessible(true);

        PathSearcher stepByStepPathSearcher = pathSearcherFactory.getPathSearcher(PathSearcherFactory.PathSearcherType.STEP_BY_STEP);

        Field words = StepByStepPathSearcher.class.getDeclaredField("wordsForCurrentUse");
        words.setAccessible(true);

        List<String> wordsToUse = List.of("and", "twe", "onr", "brb");
        words.set(stepByStepPathSearcher, new HashSet<>(wordsToUse));

        List<String> result = (List<String>) method.invoke(stepByStepPathSearcher, "one", "brb");

        assertEquals(List.of("Not found"), result);
    }

    @Test
    void shouldFindPathWhenResultIsFound() throws Exception {
        Method method = StepByStepPathSearcher.class.getDeclaredMethod("findPathWithList", String.class, String.class);
        method.setAccessible(true);

        PathSearcher stepByStepPathSearcher = pathSearcherFactory.getPathSearcher(PathSearcherFactory.PathSearcherType.STEP_BY_STEP);

        Field words = StepByStepPathSearcher.class.getDeclaredField("wordsForCurrentUse");
        words.setAccessible(true);

        List<String> wordsToUse = List.of("and", "twe", "onr");
        words.set(stepByStepPathSearcher, new HashSet<>(wordsToUse));

        List<String> result = (List<String>) method.invoke(stepByStepPathSearcher, "one", "onr");

        assertEquals(List.of("one", "onr"), result);
    }

    @Test
    void shouldReturnNextPathsWhenEndWordNotFound() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Method method = StepByStepPathSearcher.class.getDeclaredMethod("findNextStepsForPaths", Either.class, String.class);
        method.setAccessible(true);

        Field words = StepByStepPathSearcher.class.getDeclaredField("wordsForCurrentUse");
        words.setAccessible(true);

        StepByStepPathSearcher stepByStepPathSearcher = (StepByStepPathSearcher) pathSearcherFactory.getPathSearcher(PathSearcherFactory.PathSearcherType.STEP_BY_STEP);


        Either<List<String>, List<List<String>>> listOfPaths = Either.right(new ArrayList<>(new ArrayList(List.of(new ArrayList(List.of("one", "two")), List.of("one", "the")))));
        List<String> wordsToUse = List.of("and", "twe", "thu");

        words.set(stepByStepPathSearcher, new HashSet<>(wordsToUse));

        Either<String, List<String>> result = (Either<String, List<String>>) method.invoke(stepByStepPathSearcher, listOfPaths, "man");

        assertTrue(result.isRight());
        assertTrue((result.right().value().containsAll(List.of(List.of("one", "two", "twe"), List.of("one", "the", "thu")))));
    }

    @Test
    void shouldReturnFinalPathWhenEndWordFound() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Method method = StepByStepPathSearcher.class.getDeclaredMethod("findNextStepsForPaths", Either.class, String.class);
        method.setAccessible(true);

        Field words = StepByStepPathSearcher.class.getDeclaredField("wordsForCurrentUse");
        words.setAccessible(true);

        StepByStepPathSearcher stepByStepPathSearcher = (StepByStepPathSearcher) pathSearcherFactory.getPathSearcher(PathSearcherFactory.PathSearcherType.STEP_BY_STEP);


        Either<List<String>, List<List<String>>> listOfPaths = Either.right(new ArrayList<>(new ArrayList(List.of(new ArrayList(List.of("one", "two")), List.of("one", "the")))));
        List<String> wordsToUse = List.of("and", "twe");

        words.set(stepByStepPathSearcher, new HashSet<>(wordsToUse));

        Either<String, List<String>> result = (Either<String, List<String>>) method.invoke(stepByStepPathSearcher, listOfPaths, "twe");

        assertTrue(result.isLeft());
        assertEquals(List.of("one", "two", "twe"), result.left().value());
    }

    @Test
    void addPathsForCandidatesTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Method method = StepByStepPathSearcher.class.getDeclaredMethod("addPathsForCandidates", List.class, List.class, List.class);
        method.setAccessible(true);

        StepByStepPathSearcher stepByStepPathSearcher = (StepByStepPathSearcher) pathSearcherFactory.getPathSearcher(PathSearcherFactory.PathSearcherType.STEP_BY_STEP);

        Field words = StepByStepPathSearcher.class.getDeclaredField("wordsForCurrentUse");
        words.setAccessible(true);

        List<List<String>> listOfPaths = new ArrayList<>();
        List<String> list = List.of("one", "two");
        List<String> candidates = List.of("three", "four");

        words.set(stepByStepPathSearcher, new HashSet<>(candidates));

        method.invoke(stepByStepPathSearcher, listOfPaths, list, candidates);

        assertEquals(0, ((Set<String>)words.get(stepByStepPathSearcher)).size());
        assertEquals(2, listOfPaths.size());
        assertTrue(listOfPaths.contains(List.of("one", "two", "three")));
        assertTrue(listOfPaths.contains(List.of("one", "two", "four")));
    }

    @Test
    void findCandidateWordsTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Method method = StepByStepPathSearcher.class.getDeclaredMethod("findCandidateWords", String.class);
        method.setAccessible(true);

        StepByStepPathSearcher stepByStepPathSearcher = (StepByStepPathSearcher) pathSearcherFactory.getPathSearcher(PathSearcherFactory.PathSearcherType.STEP_BY_STEP);

        Field words = StepByStepPathSearcher.class.getDeclaredField("wordsForCurrentUse");
        words.setAccessible(true);

        Set<String> wordsTestSet = Set.of("teat", "buba", "fest", "evil", "tert", "temd");
        words.set(stepByStepPathSearcher, wordsTestSet);

        List<String> results = (List<String>) method.invoke(stepByStepPathSearcher, "test");

        assertTrue(results.containsAll(List.of("teat", "fest", "tert")) && results.size() == 3);
    }

    @Test
    void isWordDifferentByOneCharFromStartWordTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = StepByStepPathSearcher.class.getDeclaredMethod("isWordDifferentByOneCharFromStartWord", String.class, String.class);
        method.setAccessible(true);
        StepByStepPathSearcher stepByStepPathSearcher = (StepByStepPathSearcher) pathSearcherFactory.getPathSearcher(PathSearcherFactory.PathSearcherType.STEP_BY_STEP);
        assertTrue((Boolean) method.invoke(stepByStepPathSearcher, "ruby", "buby"));
        assertFalse((Boolean) method.invoke(stepByStepPathSearcher, "Test", "Tnbt"));
        assertFalse((Boolean) method.invoke(stepByStepPathSearcher, "Test", "xnbt"));
    }
}