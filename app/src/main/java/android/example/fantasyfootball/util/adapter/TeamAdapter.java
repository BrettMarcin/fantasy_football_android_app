package android.example.fantasyfootball.util.adapter;

import android.content.Context;
import android.content.Intent;
import android.example.fantasyfootball.R;
import android.example.fantasyfootball.draft.DraftInterceptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class TeamAdapter  extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> teamArray;
    private ArrayList<String> ptrToSelectList;
    ArrayAdapter<String> usersToInviteAdapter;
    private Button createDraftButton;

    public int getCount() {
        // return the number of records
        return teamArray.size();
    }

    public TeamAdapter(Context mContext, ArrayList arrDraftData, ArrayList ptrToSelectList, ArrayAdapter<String> usersToInviteAdapter, Button createDraftButton) {
        this.mContext = mContext;
        this.ptrToSelectList = ptrToSelectList;
        this.usersToInviteAdapter = usersToInviteAdapter;
        this.createDraftButton = createDraftButton;
        teamArray = arrDraftData;
    }

    // getView method is called for each item of ListView
    public View getView(final int position, View view, ViewGroup parent) {
        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.team_list_item, parent, false);


        // get the reference of textView and button
        final TextView txtSchoolTitle = (TextView) view.findViewById(R.id.teamName);
        Button btnAction = (Button) view.findViewById(R.id.btnAction);

        // Set the title and button name
        txtSchoolTitle.setText(teamArray.get(position));


        // Click listener of button
        btnAction.setOnClickListener(new View.OnClickListener() {
            private final String text = txtSchoolTitle.getText().toString();
            @Override
            public void onClick(View view) {
                ptrToSelectList.add(teamArray.get(position));
                teamArray.remove(position);
                usersToInviteAdapter.notifyDataSetChanged();
                notifyDataSetChanged();
                if (teamArray.size() == 0) {
                    createDraftButton.setEnabled(false);
                }
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
