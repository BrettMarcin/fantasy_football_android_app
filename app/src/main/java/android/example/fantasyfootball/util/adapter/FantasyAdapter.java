package android.example.fantasyfootball.util.adapter;

import android.content.Context;
import android.content.Intent;
import android.example.fantasyfootball.R;
import android.example.fantasyfootball.draft.BeforeDraft;
import android.example.fantasyfootball.draft.DraftInterceptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class FantasyAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mArrDraftData;

    public int getCount() {
        // return the number of records
        return mArrDraftData.size();
    }

    public FantasyAdapter(Context mContext, ArrayList arrDraftData) {
        this.mContext = mContext;
        mArrDraftData = arrDraftData;
    }

    // getView method is called for each item of ListView
    public View getView(int position, View view, ViewGroup parent) {
        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.draft_listview_row, parent, false);


        // get the reference of textView and button
        final TextView txtSchoolTitle = (TextView) view.findViewById(R.id.txtDraftTitle);
        Button btnAction = (Button) view.findViewById(R.id.btnAction);

        txtSchoolTitle.setText(mArrDraftData.get(position));
//        btnAction.setText("Action " + position);


        // Click listener of button
        btnAction.setOnClickListener(new View.OnClickListener() {
            private final String text = txtSchoolTitle.getText().toString();
            @Override
            public void onClick(View view) {
                Intent activityIntent;
                activityIntent = new Intent(mContext, DraftInterceptor.class);
                activityIntent.putExtra("id", text);
                mContext.startActivity(activityIntent);
            }
        });

        return view;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
}
