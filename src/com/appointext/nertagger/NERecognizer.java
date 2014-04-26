package com.appointext.nertagger;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreLabel;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import android.content.Context;
import android.util.Log;

import com.appointext.dataobjects.NaiveBayesKnowledgeBase;


public class NERecognizer {

    public static String NERTagger(Context con, String message) throws IOException, ClassNotFoundException {

      String serializedClassifier = "english.all.3class.distsim.crf.ser";
      
      Log.i("NER Tagger", "I loaded the string with the address");
      
      InputStream is = con.getAssets().open(serializedClassifier);
      Log.i("NER Tagger", "I loaded Input stream");
      ObjectInputStream ois = new ObjectInputStream(is);
      Log.i("NER Tagger", "I got the output stream");
      
      Log.i("NER Tagger", "Trying to print the stream"+ ois.readObject().toString());
      
      //AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
   
      AbstractSequenceClassifier<CoreLabel> classifier  = (CRFClassifier<CoreLabel>) ois.readObject();
      Log.i("NER Tagger", "I tried to get the classifier with the cast : " + classifier.toString());
      
      return(classifier.classifyToString(message));
     
      
      //return "";
     // AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);

      //The output of the NER can be displayed in 3 ways and that is called in the following lines
      
      //return(classifier.classifyWithInlineXML(message));
      //return(classifier.classifyToString(message, "xml", true));
 
   }
 
}