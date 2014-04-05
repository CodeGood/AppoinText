package com.appointext.nertagger;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreLabel;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import android.content.Context;

import com.appointext.dataobjects.NaiveBayesKnowledgeBase;


public class NERecognizer {

    public static String NERTagger(Context cnt, String message) throws IOException {

      String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser";

      AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);

      //The output of the NER can be displayed in 3 ways and that is called in the following lines
      
      return(classifier.classifyToString(message));
      //return(classifier.classifyWithInlineXML(message));
      //return(classifier.classifyToString(message, "xml", true));
 
   }
 
}