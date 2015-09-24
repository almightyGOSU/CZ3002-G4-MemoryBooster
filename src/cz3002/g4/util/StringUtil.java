package cz3002.g4.util;

import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

public class StringUtil {

	/** Default implementation: Doubles the size of the string */
	public static SpannableString enlargeString(String origString) {
		
		SpannableString ss =  new SpannableString(origString);
		ss.setSpan(new RelativeSizeSpan(2.0f), 0, ss.length(), 0);
		
		return ss;
	}
	
	/** Allows user to specify factor of string enlargement */
	public static SpannableString enlargeString(String origString,
			float enlargeFactor) {
		
		SpannableString ss =  new SpannableString(origString);
		ss.setSpan(new RelativeSizeSpan(enlargeFactor), 0, ss.length(), 0);
		
		return ss;
	}
}
