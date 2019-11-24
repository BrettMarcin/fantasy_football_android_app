package android.example.fantasyfootball.util.adapter;

import android.content.Context;
import android.example.fantasyfootball.R;
import android.example.fantasyfootball.util.Pick;
import android.example.fantasyfootball.util.Player;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

public class PickTableHistoryAdapter extends TableDataAdapter<Pick> {

    public PickTableHistoryAdapter(Context context, List<Pick> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Pick pick = getRowData(rowIndex);
        LayoutInflater inflater = getLayoutInflater();
        View theView = inflater.inflate(R.layout.view_for_draft, null);
        TextView textView = theView.findViewById(R.id.textView);
        switch (columnIndex) {
            case 0:
                textView.setText(String.valueOf(pick.getRound()));
                break;
            case 1:
                textView.setText(String.valueOf(pick.getPickNumber()));
                break;
            case 2:
                textView.setText(pick.getPlayer().getFirstName());
                break;
            case 3:
                textView.setText(pick.getPlayer().getLastName());
                break;
            case 4:
                textView.setText(pick.getUsername());
                break;
        }

        return theView;
    }
}
