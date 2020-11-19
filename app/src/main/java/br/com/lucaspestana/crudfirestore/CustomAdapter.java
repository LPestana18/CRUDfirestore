package br.com.lucaspestana.crudfirestore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.lucaspestana.crudfirestore.Model.Model;

public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {

    ListActivity listActivity;
    List<Model> modelList;

    public CustomAdapter(ListActivity listActivity, List<Model> modelList) {
        this.listActivity = listActivity;
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

                Toast.makeText(listActivity, name+"\n"+ description,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onItemLongClick(View view, final int position) {
                //this will be called when user long click item

                //Creating AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(listActivity);
                //options to display in dialog
                String[] options = {"Update", "Delete"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // update is clicked
                            // get data
                            String id = modelList.get(position).getId();
                            String name = modelList.get(position).getName();
                            String description = modelList.get(position).getDescription();

                            // intent to start activity
                            Intent intent = new Intent(listActivity, AddPlaceActivity.class);
                            //put data in intent
                            intent.putExtra("pId", id);
                            intent.putExtra("pName", name);
                            intent.putExtra("pDescription", description);

                            //start activity
                            listActivity.startActivity(intent);
                        }
                        if (which == 1) {
                            // delete is clicked
                            listActivity.deleteData(position);
                        }
                    }
                }).create().show();
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
