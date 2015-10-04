package cz3002.g4.memoryBooster;

import cz3002.g4.util.Const;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class TutorialFragment extends FragmentActivity {
	
	// UI Elements
	private ImageSwitcher _is_tutorialImages = null;
	private Button _btn_previousImg = null;
	private Button _btn_nextImg = null;
	private TextView _tv_tutorialImgStatus = null;
	private Button _btn_tut_backToMain = null;

	// For switching between images
    private Integer [] _images = {R.drawable.timage_1,
    		R.drawable.timage_2, R.drawable.timage_3,
    		R.drawable.timage_4, R.drawable.timage_5,
    		R.drawable.timage_6, R.drawable.timage_7};
    private int _numImages = _images.length;
    private int _currImage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_frag);

        getUIElements();
        setUpInteractiveElements();
        
        initializeImageSwitcher();
        setInitialImage();
    }

    private void initializeImageSwitcher() {
    	
        _is_tutorialImages.setFactory(new ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(TutorialFragment.this);
                return imageView;
            }
        });

        _is_tutorialImages.setInAnimation(AnimationUtils.loadAnimation(
        		this, android.R.anim.fade_in));
        _is_tutorialImages.setOutAnimation(AnimationUtils.loadAnimation(
        		this, android.R.anim.fade_out));
    }
    
    /** Getting UI Elements from layout */
	private void getUIElements() {
		
		_is_tutorialImages = (ImageSwitcher) findViewById(R.id.is_tutorialImages);
		
		_btn_previousImg = (Button) findViewById(R.id.btn_previousImg);
		_btn_nextImg = (Button) findViewById(R.id.btn_nextImg);
		
		_tv_tutorialImgStatus = (TextView) findViewById(R.id.tv_tutorialImgStatus);
		_btn_tut_backToMain = (Button) findViewById(R.id.btn_tut_backToMain);
	}
	
	/** Set up interactive UI Elements */
	private void setUpInteractiveElements() {
		
		_btn_previousImg.setEnabled(false);
		_btn_previousImg.setBackgroundResource(
				R.drawable.btn_prev_img_disabled);
		
		// For going to previous image
		_btn_previousImg.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				
				_currImage--;
				setCurrentImage();
				
				_btn_nextImg.setEnabled(true);
				_btn_nextImg.setBackgroundResource(
						R.drawable.btn_next_img);
				
				if(_currImage == 0) {
					_btn_previousImg.setEnabled(false);
					_btn_previousImg.setBackgroundResource(
							R.drawable.btn_prev_img_disabled);
				}
			}
		});
		
		// For going to next image
		_btn_nextImg.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				_currImage++;
				setCurrentImage();

				_btn_previousImg.setEnabled(true);
				_btn_previousImg.setBackgroundResource(
						R.drawable.btn_prev_img);

				if (_currImage == (_numImages - 1)) {
					_btn_nextImg.setEnabled(false);
					_btn_nextImg.setBackgroundResource(
							R.drawable.btn_next_img_disabled);
				}
			}
		});
		
		// For returning to main menu
		_btn_tut_backToMain.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				// Go back main menu
				Intent intent = new Intent(getApplicationContext(),
						MainFragment.class);
				intent.putExtra(Const.BACK_TO_MAIN, true);
				startActivity(intent);
			}
		});
	}

    private void setInitialImage() {
    	
        setCurrentImage();
    }

    private void setCurrentImage() {
    	
        _is_tutorialImages.setImageResource(_images[_currImage]);
        _tv_tutorialImgStatus.setText("Page " + (_currImage + 1) + "/" + _numImages);
    }
}
