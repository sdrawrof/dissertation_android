package com.example.dissertation_android;

//package out.production.dissertation_java;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.util.CoreMap;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import out.production.dissertation_java.SentenceClass;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class AQGClass {

    List<SentenceClass> sentenceObjects = new ArrayList<SentenceClass>(); // List of sentence objects
    List<Tree> treeList = new ArrayList<Tree>(); //List of trees
    List<String> preprocessingTregex = new ArrayList<String>();

    public AQGClass() {
    }


    public ArrayList<String> read_pdf(String pathstring) throws IOException {
        ArrayList<String> stringsList = new ArrayList<>();
        int noOfPages;

        PDDocument document = PDDocument.load(new File(pathstring));
        noOfPages = document.getNumberOfPages();
        //System.out.println("no of pages:" + pages);

        PDFTextStripper pdfStripper = new PDFTextStripper();

        for (int i = 0; i <= noOfPages; i++) {
            pdfStripper.setStartPage(i);
            pdfStripper.setEndPage(i);
            stringsList.add(pdfStripper.getText(document));

        }
        document.close();
        return stringsList;
    }

    public List<CoreMap> nlp_pipeline(String pagetext) {
        //set and run processing pipeline
        Properties properties = new Properties(); //set up a Properties object which we can configure and pass to a StanfordCoreNLP pipeline
        properties.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref"); //adds annotators to properties
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

        //Create an annotation object from sample text
        Annotation doc = new Annotation(pagetext);

        //pass text through annotator pipeline
        pipeline.annotate(doc);

        //Manipulating the output of the pipeline:
        //Create a CoreMap that gets all the sentences in document
        List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);


        for (CoreMap sentence : sentences) {
            //System.out.println("Applying coreNLP...");
            //go through every sentence in the CoreMap
            //a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                //get token text
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                //System.out.println(word);
                //get POS tag
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                //System.out.println(pos);
                //get NER label
                String namedent = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                //System.out.println(namedent);

                //add word and tag to lists
                //twList.add(new TaggedWord(word, pos));
            }

            //For each sentence you can use the included pcfg parser and depndency parser
            //create parse tree of the sentence
            //Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            //System.out.println(tree);

            //SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            //System.out.println(dependencies);

        }

        //Can also create Coreference link graph
        //Each chain stores a set of mentions that link tp each other along with a method for getting the most representative mention
        //Both sentence and token offsets start at 1
        //Map<Integer, CorefChain> graph = doc.get(CorefCoreAnnotations.CorefChainAnnotation.class);
        //System.out.println(graph)

        return sentences;
    }


    //This is the stanford dependency parser which we will also pass the raw text through
    public List<GrammaticalStructure> depParser(String pageString) {
        List<GrammaticalStructure> gsList = new ArrayList<GrammaticalStructure>();

        String modelPath = DependencyParser.DEFAULT_MODEL;
        String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words-distsim.tagger";

        //See args switch cases if you need them in old file

        MaxentTagger tagger = new MaxentTagger(taggerPath);
        DependencyParser parser = DependencyParser.loadFromModelFile(modelPath);

        DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(pageString));
        for (List<HasWord> sentence : tokenizer) {
            List<TaggedWord> tagged = tagger.tagSentence(sentence);
            gsList.add(parser.predict(tagged));

            // Print typed dependencies
            //System.out.println(gs);
        }

        return gsList;
    }

    public List<Tree> pcfgParserTREES(int page, String pageString, String storepath, String[] options) throws IOException {

        options = new String[]{"-maxLength", "80", "-retainTmpSubcategories"};
        LexicalizedParser lParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", options);
        TreebankLanguagePack tlp = lParser.getOp().langpack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();


        //Take the raw text and this time convert to HasWord and Tagword tokens and pcfg parse
        //Then add the tree to the list
        //Then creat a sentence object, put the tree inside and add it to the objects list
        DocumentPreprocessor tokenizer2 = new DocumentPreprocessor(new StringReader(pageString));
        int i = 0;
        for (List<HasWord> sentence2 : tokenizer2) {
            Tree aTree = lParser.parse(sentence2);
            treeList.add(aTree);
            i++;
            //Show all annotations
            //GrammaticalStructure gs2 = gsf.newGrammaticalStructure(pcfgParsed);
            //System.out.println(gs2);

            sentenceObjects.add(new SentenceClass(aTree, page)); //make new sentenceObject and add it to the list in out.production.dissertation_java.AQGClass
        }

        return treeList;
    }

    public List<File> pcfgParserFiles(int page, String pageString, String storepath, String[] options) throws IOException {
        List<File> treeFiles = new ArrayList<File>();

        options = new String[]{"-maxLength", "80", "-retainTmpSubcategories"};
        LexicalizedParser lParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", options);
        TreebankLanguagePack tlp = lParser.getOp().langpack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();


        //Take the raw text and this time convert to HasWord and Tagword tokens and pcfg parse!
        DocumentPreprocessor tokenizer2 = new DocumentPreprocessor(new StringReader(pageString));
        int i = 0;
        for (List<HasWord> sentence2 : tokenizer2) {
            Tree pcfgParsed = lParser.parse(sentence2);

            //create file, write tree to file, add file to list
            String count = String.valueOf(i);
            File file = new File("res/file_page" + page +"_no_" + count);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(pcfgParsed.toString());
            writer.close();
            treeFiles.add(file);

            pcfgParsed.pennPrint();
            i++;

            //Show all annotations
            //GrammaticalStructure gs2 = gsf.newGrammaticalStructure(pcfgParsed);
            //System.out.println(gs2);

        }

        return treeFiles;
    }

    //get Tree list, remove unusable phrases using Tregex matching and return the list
    public List<Tree> removeUndefinedPhrases() {
        preprocessingTregex.add("XX");
        preprocessingTregex.add("Next $+ week");
        preprocessingTregex.add("NEXT $+ WEEK");
        preprocessingTregex.add("Next $+ Lecture");
        preprocessingTregex.add("NEXT $+ LECTURE");
        preprocessingTregex.add("Preface >>, --");
        preprocessingTregex.add("PREFACE >>, __");
        preprocessingTregex.add("Bibliography >>, __");
        preprocessingTregex.add("BIBLIOGRAPHY >>, __");
        preprocessingTregex.add("Index >>, __");
        preprocessingTregex.add("INDEX >>, __");
        preprocessingTregex.add("Glossary >>, __");
        preprocessingTregex.add("GLOSSARY >>, __");
        preprocessingTregex.add("Contents >>, __");
        preprocessingTregex.add("CONTENTS >>, __");
        preprocessingTregex.add("Coursework >>, __");
        preprocessingTregex.add("COURSEWORK >>, __");
        preprocessingTregex.add("Exam >>, __");
        preprocessingTregex.add("EXAM >>, __");
        preprocessingTregex.add("Module $+ Overview >>, __");
        preprocessingTregex.add("MODULE $+ OVERVIEW >>, __");
        preprocessingTregex.add("Overview >>, __");
        preprocessingTregex.add("OVERVIEW >>, __");
        preprocessingTregex.add("Staff >>, __");
        preprocessingTregex.add("STAFF >>, __");
        preprocessingTregex.add("Timetable >>, __");
        preprocessingTregex.add("TIMETABLE >>, __");
        preprocessingTregex.add("CD >: ROOT"); //a sentence that only consists of a number
        preprocessingTregex.add("LST >: __"); //Lists
        preprocessingTregex.add("CD (.+ (CD) CD & !$ !CD)");

        for( String pattString : preprocessingTregex) {
            TregexPattern pattern = TregexPattern.compile(pattString);
            Iterator<Tree> myIter = treeList.iterator();

            while (myIter.hasNext()) {
                Tree tree = myIter.next();
                TregexMatcher matcher = pattern.matcher(tree); //create a pattern matcher for the tree
                if (matcher.find()) {
                    matcher.getMatch().pennPrint();
                    myIter.remove();
                }
            }
        }
        return treeList;
    }

    //
    public List<Tree> simplificationOnSemanticEntailment() {

        return treeList;
    }


    public List<Tree> getTrees() {
        return treeList;
    }

    public List<SentenceClass> getSentenceObjectList() {
        return sentenceObjects;
    }


}

