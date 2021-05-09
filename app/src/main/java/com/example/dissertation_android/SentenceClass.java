package com.example.dissertation_android;
//package out.production.dissertation_java;

import edu.stanford.nlp.trees.Tree;

import java.util.HashMap;

public class SentenceClass {
    public int page;
    private Tree pcfgTree;
    private Tree processingTree;
    private Tree questionTree;

    private HashMap<Integer, String> wordIndex; //tells you the position of a word in the sentence
    private HashMap<Integer, String> semanticTagIndex; //tells you the semantic tag of the word when given its index

    public SentenceClass(Tree parsedTree, int pageno) {
        pcfgTree = parsedTree;
        page = pageno;
    }

}

