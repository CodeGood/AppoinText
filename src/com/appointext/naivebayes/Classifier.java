package com.appointext.naivebayes;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Map;

import android.content.Context;

import com.appointext.classifiers.NaiveBayes;
import com.appointext.dataobjects.NaiveBayesKnowledgeBase;

public class Classifier {

	NaiveBayes nb;
	
	public Classifier(Context con, String filename) throws IOException, ClassNotFoundException {
		
		
		InputStream is = con.getAssets().open(filename);
    	ObjectInputStream ois = new ObjectInputStream(is);
    	NaiveBayesKnowledgeBase knowledgeBase = (NaiveBayesKnowledgeBase)ois.readObject();
    	ois.close();
    	nb = new NaiveBayes(knowledgeBase);
		
	}
	
	public Map<String, Double> getConfidence(String message) {
		return nb.predict(message);
	}
	
}
