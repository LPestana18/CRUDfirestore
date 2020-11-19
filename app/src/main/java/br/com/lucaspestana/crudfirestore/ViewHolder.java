package br.com.lucaspestana.crudfirestore;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView mTextName, mTextDescription, mTextDate, mTextLat, mTextLng;
    View mView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        //Item click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });

        // Item long click listener
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
        });

        //initialize views with model_layout.xml
        mTextName = itemView.findViewById(R.id.text_places_name);
        mTextDescription = itemView.findViewById(R.id.text_places_description);
        mTextDate = itemView.findViewById(R.id.text_places_date);
        mTextLat = itemView.findViewById(R.id.text_places_latitude);
        mTextLng = itemView.findViewById(R.id.text_places_longitude);
    }

    private ViewHolder.ClickListener mClickListener;
    // iterface for click listener
    public interface ClickListener {
        void onItemClick(View view , int position);
        void onItemLongClick(View view, int position);
    }
    public void setOnClickListener(ViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }
}