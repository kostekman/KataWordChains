package com.koscinsa.wordchains;


import com.koscinsa.wordchains.strategies.PathSearcher;
import com.koscinsa.wordchains.strategies.PathSearcherFactory;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("Please provide one argument -> path to dictionary");
        }

        PathSearcherFactory pathSearcherFactory;

        try {
            pathSearcherFactory = new PathSearcherFactory(args[0]);
        } catch (IOException e) {
            System.out.println("Wrong path provided or file is corrupted");
            return;
        }

        PathSearcher pathSearcher = pathSearcherFactory.getPathSearcher(PathSearcherFactory.PathSearcherType.STEP_BY_STEP);

        String firstWord, secondWord;

        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("Enter words for which you want to find a path, they must be different, exist in dictionary and have the same length");
            firstWord = scanner.nextLine().replaceAll(" ", "");
            secondWord = scanner.nextLine().replaceAll(" ", "");
        } while(!(pathSearcher.getWords().containsAll(List.of(firstWord, secondWord))
                && !firstWord.equals(secondWord)
                && firstWord.length() == secondWord.length()));

        scanner.close();

        System.out.println(pathSearcher.findPath(firstWord, secondWord));
    }


}
