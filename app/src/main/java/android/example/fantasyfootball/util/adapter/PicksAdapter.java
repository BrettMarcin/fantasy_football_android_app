package android.example.fantasyfootball.util.adapter;

import android.content.Context;
import android.example.fantasyfootball.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PicksAdapter extends RecyclerView.Adapter<PicksAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<String> draftNames;
    private ArrayList<String> draftNumbers;
    private ItemClickListener mClickListener;

    public int getCount() {
        // return the number of records
        return draftNames.size();
    }

    public PicksAdapter(Context mContext, ArrayList<String> draftNames,ArrayList<String> draftNumbers ) {
        this.mInflater = LayoutInflater.from(mContext);
        this.draftNames = draftNames;
        this.draftNumbers = draftNumbers;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.picks_list_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = draftNames.get(position);
        String num = draftNumbers.get(position);
        holder.myTextView.setText(name + "  Pick #" + num);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return draftNames.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.team_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return draftNames.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
