package android.example.fantasyfootball.util;

import android.content.Context;
import android.content.Intent;
import android.example.fantasyfootball.R;
import android.example.fantasyfootball.draft.DraftInterceptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageAdapter  extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mArrDraftData;

    public int getCount() {
        // return the number of records
        return mArrDraftData.size();
    }

    public MessageAdapter(Context mContext, ArrayList arrDraftData) {
        this.mContext = mContext;
        mArrDraftData = arrDraftData;
    }

    // getView method is called for each item of ListView
    public View getView(int position, View view, ViewGroup parent) {
        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.list_view_for_messages, parent, false);


        // get the reference of textView and button
        final TextView txtSchoolTitle = (TextView) view.findViewById(R.id.txtDraftTitle);

        // Set the title and button name
        txtSchoolTitle.setText(mArrDraftData.get(position));

        return view;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
}
