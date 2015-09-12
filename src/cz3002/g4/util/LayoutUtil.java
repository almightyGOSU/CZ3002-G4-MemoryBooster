package cz3002.g4.util;

import android.view.View;
import android.view.ViewGroup;

public class LayoutUtil {

	/** Enables a layout and all its children */
	public static void enable(ViewGroup layout) {
	    layout.setEnabled(true);
	    for (int i = 0; i < layout.getChildCount(); i++) {
	        View child = layout.getChildAt(i);
	        if (child instanceof ViewGroup) {
	        	enable((ViewGroup) child);
	        } else {
	            child.setEnabled(true);
	        }
	    }
	}
	
	/** Disables a layout and all its children */
	public static void disable(ViewGroup layout) {
	    layout.setEnabled(false);
	    for (int i = 0; i < layout.getChildCount(); i++) {
	        View child = layout.getChildAt(i);
	        if (child instanceof ViewGroup) {
	            disable((ViewGroup) child);
	        } else {
	            child.setEnabled(false);
	        }
	    }
	}
	
	/** Enable/Disable clicking for UI elements */
	public static void setClickable(ViewGroup layout, boolean bClickable) {
		
	    layout.setClickable(bClickable);
	    
	    for (int i = 0; i < layout.getChildCount(); i++) {
	        View child = layout.getChildAt(i);
	        if (child instanceof ViewGroup) {
	        	setClickable((ViewGroup) child, bClickable);
	        } else {
	            child.setEnabled(bClickable);
	        }
	    }
	}
}
