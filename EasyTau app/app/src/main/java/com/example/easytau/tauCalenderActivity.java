package com.example.easytau;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;

import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import android.app.Activity;

import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;


/**
 * Created by shir on 02/03/2017.
 */



public class tauCalenderActivity extends AppCompatActivity {

    private List<EventCalendar> eventsList = new ArrayList<EventCalendar>();
    private Button home_button;
    private ListView listView;

    public class costume_calendar_list extends ArrayAdapter<String> {
        private final Activity context;

        public costume_calendar_list(Activity context,
                                     List<String> events) {
            super(context, R.layout.calendar_list_row, events);
            this.context = context;

        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();

            String lang = Locale.getDefault().getLanguage();
            View rowView;
            if(lang.equals("עברית") || lang.equals("iw") || lang.equals("he")|| lang.equals("heb")  || lang.equals("hebrew") ) {
                rowView = inflater.inflate(R.layout.calendar_list_row_heb, null, true);
            }
            else {
                rowView = inflater.inflate(R.layout.calendar_list_row, null, true);
            }


            Typeface alef_font = Typeface.createFromAsset(getAssets(),  "fonts/Alef-Regular.ttf");
            Typeface alef_bold_font = Typeface.createFromAsset(getAssets(),  "fonts/Alef-Bold.ttf");
            TextView EventName = (TextView) rowView.findViewById(R.id.EventName);
            final String eventNameStr = eventsList.get(position).getName();
            EventName.setText(eventsList.get(position).getName());
            EventName.setTypeface(alef_bold_font);

            if(eventsList.get(position).getFullDate() != null && eventsList.get(position).getJewish_date() != null ) {
                TextView EventDates = (TextView) rowView.findViewById(R.id.Dates);
                String hebD = eventsList.get(position).getJewish_date();
                String date = eventsList.get(position).getFullDate();

                ImageButton addToCal = (ImageButton) rowView.findViewById(R.id.add_to_cal);
                addToCal.setVisibility(View.VISIBLE);

                SpannableString totalDate = new SpannableString("תאריך: " + hebD + ", " + date + ".");
                date = date.replaceAll("\\s","");
                totalDate.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 6, 0);
                EventDates.setText(totalDate);
                EventDates.setTypeface(alef_font);
                EventDates.setVisibility(View.VISIBLE);

                /* for adding event to Calendar */
                final String[] dateArr1 = new String[3];
                final String[] dateArr2 = new String[3];

                if (eventsList.get(position).getTo_date() != null){ // a continuous event
                    String startDate = eventsList.get(position).getFrom_date();
                    startDate = startDate.replaceAll("\\s","");

                    String endDate = eventsList.get(position).getTo_date();
                    endDate = endDate.replaceAll("\\s","");

                    StringTokenizer st1 = new StringTokenizer(startDate);
                    StringTokenizer st2 = new StringTokenizer(endDate);

                    for (int i = 0; i<3 ; i++){
                        dateArr1[i] = st1.nextToken(".");
                        dateArr2[i] = st2.nextToken(".");
                    }


                } else {
                    StringTokenizer st = new StringTokenizer(date);

                    for (int i = 0; i<3 ; i++){
                        dateArr1[i] = st.nextToken(".");
                    }
                }

                addToCal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Calendar cal = new GregorianCalendar();
                        cal.setTime(new Date());


                        if (eventsList.get(position).getTo_date() == null){ // a 1 day event
                            int day = Integer.valueOf(dateArr1[0]);
                            int month = Integer.valueOf(dateArr1[1]) - 1;
                            int year = Integer.valueOf(dateArr1[2]);

                            if (year < 2000) {
                                year += 2000;
                            }

                            cal.set(Calendar.MONTH, month);
                            cal.set(Calendar.DAY_OF_MONTH, day);
                            cal.set(Calendar.YEAR, year);

                            Intent intent = new Intent(Intent.ACTION_INSERT);
                            intent.setData(CalendarContract.Events.CONTENT_URI);
                            intent.putExtra(CalendarContract.Events.TITLE, eventNameStr);
                            intent.putExtra(CalendarContract.Events.ALL_DAY, true);
                            intent.putExtra(
                                    CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                    cal.getTime().getTime());
                            intent.putExtra(
                                    CalendarContract.EXTRA_EVENT_END_TIME,
                                    cal.getTime().getTime() + 600000);

                            startActivity(intent);

                        } else {
                            int dayStart = Integer.valueOf(dateArr1[0]);
                            int monthStart = Integer.valueOf(dateArr1[1]) - 1;
                            int yearStart = Integer.valueOf(dateArr1[2]);

                            if (yearStart < 2000) {
                                yearStart += 2000;
                            }
                            int dayEnd = Integer.valueOf(dateArr2[0]);
                            int monthEnd = Integer.valueOf(dateArr2[1]) - 1;
                            int yearEnd = Integer.valueOf(dateArr2[2]);

                            if (yearEnd < 2000) {
                                yearEnd += 2000;
                            }

                            Calendar startCal = Calendar.getInstance();
                            startCal.set(yearStart, monthStart, dayStart);
                            //startTime = beginCal.getTimeInMillis();

                            Calendar endCal = Calendar.getInstance();
                            endCal.set(yearEnd, monthEnd, dayEnd);
                            // endTime = endCal.getTimeInMillis();


                            Intent intent = new Intent(Intent.ACTION_INSERT);
                            intent.setData(CalendarContract.Events.CONTENT_URI);
                            intent.putExtra(CalendarContract.Events.TITLE, eventNameStr);
                            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startCal.getTimeInMillis());
                            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endCal.getTimeInMillis());
                            intent.putExtra(CalendarContract.Events.ALL_DAY, true);

                            startActivity(intent);
                        }
                    }


                });

            }

            return rowView;
        }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tau_calendar);

        home_button = (Button)findViewById(R.id.home_button);

        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                didTapButton(view);
                Intent i = new Intent(view.getContext(), StartMenu.class);
                startActivity(i);
            }
        });


        for (TauEvent event : TotalInfoFromDb.getTauEvents()) {

            EventCalendar cal_event = new EventCalendar();

            String name = event.getEvent();
            cal_event.setName(" " + name + " ");

            if (event.getDate() != null && event.getHebrewDate() != null) {
                String full_date = event.getDate() + " ";
                cal_event.setFullDate(full_date);

                if (full_date.contains("-") && !full_date.contains("מ")) {
                    String[] dates = full_date.split("-");
                    String startDate = dates[0] + "." + dates[1].substring(dates[1].indexOf('.') + 1);
                    String endDate = dates[1];
                    cal_event.setFrom_date(startDate);
                    cal_event.setTo_date(endDate);
                } else if (full_date.contains("מ")) {
                    cal_event.setFrom_date(full_date.substring(2, full_date.indexOf(' ')));
                    String secDate = full_date.substring(full_date.indexOf(' ') + 4);
                    cal_event.setTo_date(secDate);

                } else {
                    cal_event.setFrom_date(full_date);
                }

                cal_event.setFullDate(full_date);
                cal_event.setJewish_date(event.getHebrewDate());

                if (name.contains("עצרת") || name.contains("טקס")) {
                    cal_event.setTo_h("13:00");
                    cal_event.setFrom_h("12:00");
                }

            }
            eventsList.add(cal_event);

        }

        List<String> events = new ArrayList<>();
        for (EventCalendar e: eventsList)
        {
            events.add(e.getName());
        }

        costume_calendar_list adapter = new
                costume_calendar_list(tauCalenderActivity.this,events);

        listView = (ListView) findViewById(R.id.eventListView);
        listView.setAdapter(adapter);
    }

    public void didTapButton(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(0.5, 50);
        myAnim.setInterpolator(interpolator);
        home_button.startAnimation(myAnim);
    }

    @Override
    protected  void  onRestart(){
        super.onRestart();

    }

}