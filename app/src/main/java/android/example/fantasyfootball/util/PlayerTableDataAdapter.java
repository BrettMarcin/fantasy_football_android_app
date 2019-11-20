package android.example.fantasyfootball.util;

import android.content.Context;
import android.example.fantasyfootball.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

public class PlayerTableDataAdapter extends TableDataAdapter<Player> {

    public PlayerTableDataAdapter(Context context, List<Player> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Player player = getRowData(rowIndex);
        LayoutInflater inflater = getLayoutInflater();
        View theView = inflater.inflate(R.layout.view_for_draft, null);
        TextView textView = theView.findViewById(R.id.textView);
        switch (columnIndex) {
            case 0:
                textView.setText(String.valueOf(player.getRank_player()));
                break;
            case 1:
                textView.setText(player.getFirstName());
                break;
            case 2:
                textView.setText(player.getLastName());
                break;
            case 3:
                textView.setText(player.getPostion());
                break;
        }

        return theView;
    }

}