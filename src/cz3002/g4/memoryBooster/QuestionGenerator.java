package cz3002.g4.memoryBooster;

import java.util.ArrayList;
import java.util.Collections;

import android.graphics.Bitmap;

import cz3002.g4.util.FacebookDataSource;

public class QuestionGenerator {

	public static ArrayList<Question> generateFbQuestions(
			FacebookDataSource fbDataSrc, int numQuestions) {
		
		ArrayList<Question> questionSet = new ArrayList<Question>(numQuestions);
		
		fbDataSrc.open();
		ArrayList<String> randomIDs = fbDataSrc.getRandomProfileIDs(numQuestions);
		
		Question newQuestion = null;
		for(String randomID : randomIDs) {
			
			Bitmap questionImage = fbDataSrc.getProfilePic(randomID);
			String questionAnswer = fbDataSrc.getProfileName(randomID);
			ArrayList<String> questionOptions = new ArrayList<String>(4);
			questionOptions.add(questionAnswer);
			questionOptions.addAll(fbDataSrc.getRandomNameOptions(randomID, 3));
			Collections.shuffle(questionOptions);
			
			newQuestion = new Question(questionImage, questionAnswer, questionOptions);
			questionSet.add(newQuestion);
		}
		
		fbDataSrc.close();
		return questionSet;
	}
}
