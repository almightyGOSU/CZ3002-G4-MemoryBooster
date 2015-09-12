package cz3002.g4.memoryBooster;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class Question {

	private Bitmap _questionImage;
	private String _questionAnswer;
	private ArrayList<String> _questionOptions;
	
	public Question(Bitmap questionImage, String questionAnswer,
			ArrayList<String> questionOptions) {
		
		_questionImage = questionImage;
		_questionAnswer = questionAnswer;
		_questionOptions = questionOptions;
	}
	
	public Bitmap getQuestionImage() {
		
		return _questionImage;
	}
	
	public String getQuestionAnswer() {
		
		return _questionAnswer;
	}
	
	public ArrayList<String> getQuestionOptions() {
		
		return _questionOptions;
	}
	
	public boolean checkAnswer(String userChoice) {
		
		return _questionAnswer.equals(userChoice);
	}
}
