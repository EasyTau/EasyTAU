package com.example.easytau;

import android.graphics.Typeface;
import android.net.Uri;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

/**
 * Created on 06/01/2017.
 */

public class PhoneActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] categories = {"בחר/י קטגוריה","מינהל כללי","פקולטות","שונות","רישום ושכר לימוד","הנהלה","מינהל אקדמי","ספריות"
    };
    static int counter ;

    private ImageView logo_tau;
    private TextView phoneUnit;
    private TextView phoneName;
    private TextView phoneNumber;
    private TextView faxNumber;
    private ImageButton dialButton;
    private Button home_button;
    List<String> categoryList = new ArrayList<>();
    List<String> unitAfterChosenCat =new ArrayList<>();
    List<String> namesAfterChosenUnit =new ArrayList<>();

    private Spinner spinnerCat;
    private Spinner spinnerUnit;
    private Spinner spinnerName;
    private PhoneCategory categoryGlobal;
    private PhoneUnit unitGlobal;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_info);

        Typeface BoldlFont=Typeface.createFromAsset(getAssets(), "fonts/Alef-Bold.ttf");
        TextView chooseCategory = (TextView)findViewById(R.id.chooseCategory) ;
        chooseCategory.setTypeface(BoldlFont);
        TextView chooseUnit = (TextView)findViewById(R.id.chooseUnit) ;
        chooseUnit.setTypeface(BoldlFont);
        TextView chooseName = (TextView)findViewById(R.id.chooseName) ;
        chooseName.setTypeface(BoldlFont);

        spinnerCat = (Spinner)findViewById(R.id.categorySpinner);
        for (int i = 0; i < categories.length; i++) {
            categoryList.add(categories[i]);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_text,categoryList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getView(position, convertView, parent);
                Typeface externalFont=Typeface.createFromAsset(getAssets(), "fonts/Alef-Bold.ttf");
                ((TextView) v).setTypeface(externalFont);
                return setCentered(v);
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View v =super.getDropDownView(position, convertView, parent);

                Typeface externalFont=Typeface.createFromAsset(getAssets(), "fonts/Alef-Bold.ttf");
                ((TextView) v).setTypeface(externalFont);
                return setCentered(v);
            }
            private View setCentered(View view)
            {
                TextView textView = (TextView)view.findViewById(android.R.id.text1);
                textView.setGravity(Gravity.RIGHT);
                return view;
            }
        };
        dataAdapter.setDropDownViewResource(R.layout.drop_down_list);
        dataAdapter.notifyDataSetChanged();
        spinnerCat.setAdapter(dataAdapter);

        spinnerCat.setOnItemSelectedListener(this);
        logo_tau = (ImageView)  findViewById(R.id.Logo);

        phoneUnit = (TextView) findViewById(R.id.phoneUnit);
        phoneName = (TextView) findViewById(R.id.phoneName);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        home_button = (Button) findViewById(R.id.home_button);
        faxNumber =(TextView)findViewById(R.id.faxNumber);
        dialButton = (ImageButton) findViewById(R.id.dialButton);


        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                didTapButton(view);
                Intent i = new Intent(view.getContext(), StartMenu.class);
                startActivity(i);
            }
        });

        counter = 0;

    }

    public void didTapButton(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(0.5, 50);
        myAnim.setInterpolator(interpolator);
        home_button.startAnimation(myAnim);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        unitAfterChosenCat.clear();

        Object o=spinnerCat.getSelectedItem();
        String str=(String)o;

        if(! str.equals("בחר/י קטגוריה")) {

            for (PhoneCategory category : TotalInfoFromDb.getPhoneCategories().values()) {
                if (str.equals("פקולטות")) {
                    str = "יחידות אקדמיות";
                }
                if (category.getName().equals(str)) {
                    this.categoryGlobal = category;

                    for (PhoneUnit unit : category.getUnits().values()) {

                        unitAfterChosenCat.add(unit.getName());
                    }

                    Collections.sort(unitAfterChosenCat);


                    break;
                }

            }
        }
        dialButton.setVisibility(View.INVISIBLE);
        logo_tau.setVisibility(View.INVISIBLE);
        phoneUnit.setVisibility(View.INVISIBLE);
        phoneName.setVisibility(View.INVISIBLE);
        phoneNumber.setVisibility(View.INVISIBLE);
        faxNumber.setVisibility(View.INVISIBLE);
        unitAfterChosenCat.add(0,"בחר/י יחידה");

        spinnerUnit=(Spinner)findViewById(R.id.unitSpinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_text, unitAfterChosenCat) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getView(position, convertView, parent);
                Typeface externalFont=Typeface.createFromAsset(getAssets(), "fonts/Alef-Bold.ttf");
                ((TextView) v).setTypeface(externalFont);
                return setCentered(v);
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View v =super.getDropDownView(position, convertView, parent);

                Typeface externalFont=Typeface.createFromAsset(getAssets(), "fonts/Alef-Bold.ttf");
                ((TextView) v).setTypeface(externalFont);
                return setCentered(v);
            }

            private View setCentered(View view)
            {
                TextView textView = (TextView)view.findViewById(android.R.id.text1);
                textView.setGravity(Gravity.RIGHT);
                return view;
            }
        };
        dataAdapter.setDropDownViewResource(R.layout.drop_down_list);
        dataAdapter.notifyDataSetChanged();
        spinnerUnit.setAdapter(dataAdapter);

        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                namesAfterChosenUnit.clear();

                Object o=spinnerUnit.getSelectedItem();
                String str=(String)o;

                if (! str.equals("בחר/י יחידה")) {



                    for (PhoneUnit unit : categoryGlobal.getUnits().values()) {
                        if (unit.getName().equals(str)) {
                            unitGlobal = unit;
                            Set<String> unitSet = new HashSet<String>();
                            for (PhoneNumber phoneNumber : unit.getPhoneNumbers()) {
                                if (phoneNumber.getName()!=null) {
                                    if((phoneNumber.getInnerPhone()!=null) ||phoneNumber.getFax() !=null ||phoneNumber.getOuterPhone() != null){
                                        unitSet.add(phoneNumber.getName());
                                    }

                                }
                            }
                            namesAfterChosenUnit.addAll(unitSet);
                            Collections.sort(namesAfterChosenUnit);

                            break;

                        }


                    }
                }
                dialButton.setVisibility(View.INVISIBLE);
                logo_tau.setVisibility(View.INVISIBLE);
                phoneUnit.setVisibility(View.INVISIBLE);
                phoneName.setVisibility(View.INVISIBLE);
                phoneNumber.setVisibility(View.INVISIBLE);
                faxNumber.setVisibility(View.INVISIBLE);
                namesAfterChosenUnit.add(0,"בחר/י שם");


                spinnerName=(Spinner)findViewById(R.id.nameSpinner);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PhoneActivity.this,
                        R.layout.spinner_text,namesAfterChosenUnit) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent)
                    {
                        View v = super.getView(position, convertView, parent);
                        Typeface externalFont=Typeface.createFromAsset(getAssets(), "fonts/Alef-Bold.ttf");
                        ((TextView) v).setTypeface(externalFont);
                        return setCentered(v);
                    }
                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent)
                    {
                        View v =super.getDropDownView(position, convertView, parent);

                        Typeface externalFont=Typeface.createFromAsset(getAssets(), "fonts/Alef-Bold.ttf");
                        ((TextView) v).setTypeface(externalFont);
                        return setCentered(v);
                    }

                    private View setCentered(View view)
                    {
                        TextView textView = (TextView)view.findViewById(android.R.id.text1);
                        textView.setGravity(Gravity.RIGHT);
                        return view;
                    }
                };
                dataAdapter.setDropDownViewResource(R.layout.drop_down_list);
                dataAdapter.notifyDataSetChanged();
                spinnerName.setAdapter(dataAdapter);
                spinnerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                          @Override
                                                          public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                                              Object o = spinnerName.getSelectedItem();
                                                              String str = (String) o;
                                                              if(!str.equals("בחר/י שם")){
                                                                  Typeface bold_font = Typeface.createFromAsset(getAssets(),  "fonts/Alef-Bold.ttf");
                                                                  Typeface reg_font = Typeface.createFromAsset(getAssets(),  "fonts/Alef-Bold.ttf");
                                                                  for (PhoneNumber phone: unitGlobal.getPhoneNumbers()){
                                                                      if (phone.getName().equals(str)) {

                                                                          phoneUnit.setText(phone.getUnit());
                                                                          phoneUnit.setTypeface(bold_font);

                                                                          phoneName.setText(phone.getName());
                                                                          phoneName.setTypeface(bold_font);

                                                                          if ((phone.getFax() != null) && !(phone.getFax().equals("")))
                                                                              faxNumber.setText("פקס: " + phone.getFax());
                                                                          else
                                                                              faxNumber.setText(phone.getFax());

                                                                          faxNumber.setTypeface(reg_font);

                                                                          final String udata;
                                                                          if ((phone.getInnerPhone() == null) || (phone.getInnerPhone().equals(""))) {

                                                                              udata = phone.getOuterPhone();

                                                                          } else {
                                                                              udata = phone.getInnerPhone();
                                                                          }

                                                                          if ((udata != null) && (!udata.equals(""))) {


                                                                              SpannableString content = new SpannableString(udata);
                                                                              content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);

                                                                              phoneNumber.setText("טלפון: " + content);
                                                                              phoneNumber.setTypeface(reg_font);

                                                                          }

                                                                          if(udata != null){

                                                                              dialButton.setOnClickListener(new View.OnClickListener() {
                                                                                  @Override
                                                                                  public void onClick(View view) {
                                                                                      Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                                                                      callIntent.setData(Uri.parse("tel:" + Uri.encode(udata.trim())));
                                                                                      callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                      startActivity(callIntent);

                                                                                  }


                                                                              });
                                                                              dialButton.setVisibility(View.VISIBLE);
                                                                              phoneNumber.setVisibility(View.VISIBLE);

                                                                          }else{
                                                                              dialButton.setVisibility(View.INVISIBLE);
                                                                              phoneNumber.setVisibility(View.INVISIBLE);

                                                                          }

                                                                          logo_tau.setVisibility(View.VISIBLE);
                                                                          phoneUnit.setVisibility(View.VISIBLE);
                                                                          phoneName.setVisibility(View.VISIBLE);
                                                                          faxNumber.setVisibility(View.VISIBLE);

                                                                          break;
                                                                      }
                                                                  }

                                                              }else{
                                                                  dialButton.setVisibility(View.INVISIBLE);
                                                                  logo_tau.setVisibility(View.INVISIBLE);
                                                                  phoneUnit.setVisibility(View.INVISIBLE);
                                                                  phoneName.setVisibility(View.INVISIBLE);
                                                                  phoneNumber.setVisibility(View.INVISIBLE);
                                                                  faxNumber.setVisibility(View.INVISIBLE);

                                                              }

                                                          }
                                                          @Override
                                                          public void onNothingSelected(AdapterView<?> parentView) {
                                                              // your code here

                                                          }
                                                      }
                );

            }
            //belongs to unit ilst
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

    }



    @Override
    public void onBackPressed() {
        if(counter!=0){
            int temp=counter--;
            counter= temp;
        }

        super.onBackPressed();

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected  void  onRestart(){
        super.onRestart();
    }
}
