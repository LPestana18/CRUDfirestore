package br.com.lucaspestana.crudfirestore;

import android.app.ListActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.lucaspestana.crudfirestore.Model.Model;

public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {

    MainActivity mainActivity;
    List<Model> modelList;
    Context context;

    public CustomAdapter(MainActivity mainActivity, List<Model> modelList) {
        this.mainActivity = mainActivity;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lugares_item, parent,false);

        ViewHolder viewHolder = new ViewHolder(itemView);
        //handle item clicks here
        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //this will be called when user click item

                String name = modelList.get(position).getName();
                String description = modelList.get(position).getDescription();
                String date = modelList.get(position).getDate();

                Toast.makeText(mainActivity, name+"\n"+ description,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //this will be called when user long click item
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //bind views / set data
        holder.mTextName.setText(modelList.get(position).getName());
        holder.mTextDescription.setText(modelList.get(position).getDescription());
        holder.mTextDate.setText(modelList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
