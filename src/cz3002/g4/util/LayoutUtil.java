package cz3002.g4.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
	
	/**
	 * Performs cross-fading of 2 views
	 * 
	 * @param displayedView			View to be displayed
	 * @param hiddenView			View to be hidden
	 * @param animationDuration		Animation duration
	 */
	public static void crossfade(final View displayedView, final View hiddenView,
			int animationDuration) {

		// Set the view to be displayed to 0% opacity but visible,
		// so that it is visible (but fully transparent) during the animation
		displayedView.setAlpha(0f);
		displayedView.setVisibility(View.VISIBLE);

		// Animate the view to be displayed to 100% opacity,
		// and clear any animation listener set on the view
		displayedView.animate().alpha(1f).setDuration(animationDuration)
				.setListener(null);

		// Animate the view to be hidden to 0% opacity
		hiddenView.animate().alpha(0f).setDuration(animationDuration)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						hiddenView.setVisibility(View.INVISIBLE);
					}
				});
	}
}
