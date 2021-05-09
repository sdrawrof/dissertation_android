package com.example.dissertation_android;
//package out.production.dissertation_java;

import edu.stanford.nlp.trees.Tree;
import org.apache.pdfbox.pdmodel.PDDocument;
import com.example.dissertation_android.AQGClass;
import com.example.dissertation_android.SentenceClass;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//This uses the java Apache PDFBox library for PDF Documents
public class Task_1 {

    private static ArrayList<String> pageStrings;

    //TREGEX COMMAND GUI LOADER
    //C:\Users\adia_prog\my_place\stanford-tregex-4.2.0\stanford-tregex-2020-11-17>java -mx300m -cp "stanford-tregex.jar;" edu.stanford.nlp.trees.tregex.gui.TregexGUI

    public static void main(String[] args) throws IOException {
        //remove this after
        PDDocument document = PDDocument.load(new File("res/SEPDF.pdf"));
        int temp = document.getNumberOfPages();
        //System.out.println(temp);
        for(int i = temp-1; i >= 11; i--) {
            //System.out.println(i);
            document.removePage(i);
        }
        document.save("new_PDF.pdf");

        //Create AQG class
        AQGClass AQGObject = new AQGClass();
        pageStrings = new ArrayList<>((AQGObject.read_pdf("new_PDF.pdf"))); //load all pdf into strings, one for each page

        //NLP Preprocessing
        /*for(String pageString : pageStrings) {
            AQGObject.nlp_pipeline(pageString);
        }*/


        //pcfg
        for(String pageString : pageStrings) {
            AQGObject.pcfgParserTREES(pageStrings.indexOf(pageString), pageString, "res/", new String[]{"-maxLength", "80", "-retainTmpSubcategories"});
        }

        //dependency parse
        /*for(String pageString : pageStrings) {
            AQGObject.depParser(pageString);
        }*/

        //Get the completed list of Trees, not in their objects
        List<Tree> treeList = AQGObject.getTrees();
        System.out.println("Before removing undefined phrases" + treeList.size());

        AQGObject.removeUndefinedPhrases();
        System.out.println("After removing undefined phrases" + treeList.size());


        //Get the completed list of sentences containing pcfgParsed trees
        List<SentenceClass> sentenceObjectList = AQGObject.getSentenceObjectList();
    }

}
