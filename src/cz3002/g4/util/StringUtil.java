package cz3002.g4.util;

import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

public class StringUtil {

	public static SpannableString enlargeString(String origString) {
		
		SpannableString ss =  new SpannableString(origString);
		ss.setSpan(new RelativeSizeSpan(2.0f), 0, ss.length(), 0);
		
		return ss;
	}
}
