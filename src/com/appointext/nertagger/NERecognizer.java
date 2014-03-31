package com.appointext.nertagger;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreLabel;
import java.io.IOException;


public class NERecognizer {

    public static String NERTagger(String message) throws IOException {

      String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";

      AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);

      //The output of the NER can be displayed in 3 ways and that is called in the following lines
      
      return(classifier.classifyToString(message));
      //return(classifier.classifyWithInlineXML(message));
      //return(classifier.classifyToString(message, "xml", true));
        
   }
 
}