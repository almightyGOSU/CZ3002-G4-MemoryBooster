package cz3002.g4.memoryBooster;

import java.util.ArrayList;
import java.util.Collections;

import android.graphics.Bitmap;
import android.util.Log;
import cz3002.g4.util.FacebookDataSource;
import cz3002.g4.util.GeneralDataSource;

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
	
	public static ArrayList<Question> generateGenQuestions(
			GeneralDataSource genDataSrc, int numQuestions) {
		
		ArrayList<Question> questionSet = new ArrayList<Question>(numQuestions);
		
		genDataSrc.open();
		String categoryTag = genDataSrc.getRandomTag();
		ArrayList<String> answersList =
				genDataSrc.getRandomAnswers(categoryTag, numQuestions);
		
		Question newQuestion = null;
		for(String answer : answersList) {
			
			Bitmap questionImage = genDataSrc.getImage(answer, categoryTag);
			String questionAnswer = answer;
			ArrayList<String> questionOptions = new ArrayList<String>(4);
			questionOptions.add(questionAnswer);
			questionOptions.addAll(genDataSrc.getRandomNameOptions(
					answer, categoryTag, 3));
			Collections.shuffle(questionOptions);
			
			newQuestion = new Question(questionImage, questionAnswer, questionOptions);
			questionSet.add(newQuestion);
		}
		
		genDataSrc.close();
		return questionSet;
	}
}
