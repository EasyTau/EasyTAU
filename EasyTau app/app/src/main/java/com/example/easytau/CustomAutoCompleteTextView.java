package com.example.easytau;

/**
 * Created by Chen on 1/13/2017.
 */

import android.content.Context;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;


public class CustomAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    public void init(){
        this.setOnTouchListener(new View.OnTouchListener() {  /* clear text view when touching x button */
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                CustomAutoCompleteTextView curr = CustomAutoCompleteTextView.this;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (curr.getRight() - curr.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        curr.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        this.setLongClickable(false); /* to avoid clipboard message */
        this.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross, 0); /* x button */
        this.setThreshold(0);


    }

    public CustomAutoCompleteTextView(Context context) {
        super(context);
        init();
    }

    public CustomAutoCompleteTextView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        init();
    }


    public CustomAutoCompleteTextView(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
        init();
    }

}
