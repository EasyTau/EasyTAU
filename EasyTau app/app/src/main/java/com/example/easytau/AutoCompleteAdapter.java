
package com.example.easytau;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class AutoCompleteAdapter extends ArrayAdapter<String> implements
        Filterable {

    private ArrayList<String> fullList;
    private ArrayList<String> mOriginalValues;
    private ArrayFilter mFilter;
    LayoutInflater inflater;
    String text = "";
    private Boolean isEnglish = true;

    public AutoCompleteAdapter(Context context, int resource,
                               int textViewResourceId, List<String> objects) {

        super(context, resource, textViewResourceId, objects);
        fullList = (ArrayList<String>) objects;
        mOriginalValues = new ArrayList<>(fullList);
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return fullList.size();
    }

    @Override
    public String getItem(int position) {
        return fullList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        String item = getItem(position);
        Log.d("item", "" + item);

        if (convertView == null) {
            convertView = view = inflater.inflate(
                    android.R.layout.simple_list_item_1, null);
        }

        TextView myTv = (TextView) convertView.findViewById(android.R.id.text1);
        myTv.setText(highlight(text, item));
        return view;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {
        private Object lock;

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (prefix != null) {
                text = prefix.toString();
            }
            if (mOriginalValues == null) {
                synchronized (lock) {
                    mOriginalValues = new ArrayList<String>(fullList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    ArrayList<String> list = new ArrayList<String>(
                            mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                if (prefixString.charAt(0) >= 'א' &&prefixString.charAt(0)<= 'ת'){
                    isEnglish = false;
                } else if ((prefixString.charAt(0) >= 'a' && prefixString.charAt(0) <= 'z') || (prefixString.charAt(0) >= 'A' && prefixString.charAt(0) <= 'Z')){
                    isEnglish = true;
                }

                ArrayList<String> values = mOriginalValues;
                int count = values.size();

                ArrayList<String> newValues = new ArrayList<String>(count);

                for (int i = 0; i < count; i++) {
                    String item = values.get(i);
                    if (item.toLowerCase().contains(prefixString)) {

                        if(isEnglish && ((item.charAt(0) >= 'a' && item.charAt(0) <= 'z') || (item.charAt(0) >= 'A' && item.charAt(0) <= 'Z'))){
                            newValues.add(item);
                        }else if (!isEnglish && ((item.charAt(0) >= 'א' && item.charAt(0) <= 'ת'))){
                            newValues.add(item);
                        }

                    }

                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            if (results.values != null) {
                fullList = (ArrayList<String>) results.values;
            } else {
                fullList = new ArrayList<String>();
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }

    public static CharSequence highlight(String search, String originalText) {

        String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ENGLISH);

        int start = normalizedText.indexOf(search.toLowerCase(Locale.ENGLISH));
        if (start < 0) {
            return originalText;
        } else {
            // highlight each appearance in the original text

            Spannable highlighted = new SpannableString(originalText);
            while (start >= 0) {
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + search.length(),
                        originalText.length());

                highlighted.setSpan(new ForegroundColorSpan(Color.BLUE),
                        spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                highlighted.setSpan(new StyleSpan(Typeface.BOLD),
                        spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                start = normalizedText.indexOf(search, spanEnd);
            }

            return highlighted;
        }
    }
}