package cz3002.g4.util;

public class AnswerTagPair {

	private String _answer;
	private String _tag;
	
	public AnswerTagPair(String answer, String tag) {
		_answer = answer;
		_tag = tag;
	}
	
	public String getAnswer() {
		return _answer;
	}
	
	public String getTag() {
		return _tag;
	}
}
