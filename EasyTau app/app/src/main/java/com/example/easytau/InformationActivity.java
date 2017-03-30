package com.example.easytau;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.text.method.LinkMovementMethod;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by RONI on 26/03/2017.
 */

public class InformationActivity extends AppCompatActivity {

    private Button home_button;
    private TextView linkToWeb;
    private TextView mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);

        TextView tx1 = (TextView)findViewById(R.id.first);
        TextView tx2 = (TextView)findViewById(R.id.second);
        TextView tx4 = (TextView)findViewById(R.id.linkToWeb);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Alef-Bold.ttf");
        tx1.setTypeface(custom_font);
        tx2.setTypeface(custom_font);
        tx4.setTypeface(custom_font);

        linkToWeb =((TextView) findViewById(R.id.linkToWeb));
        linkToWeb.setMovementMethod(LinkMovementMethod.getInstance());
        linkToWeb.setText(Html.fromHtml("<a href=\"http://easytauteam.wixsite.com/easytau\">לביקור באתר</a>"));

        mail = (TextView) findViewById(R.id.second);
        mail.setText(Html.fromHtml("<a href=\"mailto:easytauteam@gmail.com\">ליצירת קשר</a>"));
        mail.setMovementMethod(LinkMovementMethod.getInstance());

        home_button = (Button) findViewById(R.id.home_button);

        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                didTapButton(view);
                Intent i = new Intent(view.getContext(), StartMenu.class);
                startActivity(i);
            }
        });
    }

    public void didTapButton(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(0.5, 50);
        myAnim.setInterpolator(interpolator);
        home_button.startAnimation(myAnim);
    }

}
