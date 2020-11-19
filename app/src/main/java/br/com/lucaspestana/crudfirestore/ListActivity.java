package br.com.lucaspestana.crudfirestore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import br.com.lucaspestana.crudfirestore.Model.Model;

public class ListActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;
    List<Model> modelList = new ArrayList<>();
    RecyclerView mRecyclerView;
    // layout manager for recyclerview
    RecyclerView.LayoutManager layoutManager;

    //firestore instance
    FirebaseFirestore db;

    // Progress Dialog
    ProgressDialog pd;


    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init firestore
        db = FirebaseFirestore.getInstance();

        fabAdd = findViewById(R.id.fabAddLugares);
        // initialize views
        mRecyclerView = findViewById(R.id.recycler_places);

        // set recycler view properties
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        // show data in recyclerview
        showData();

        //progress dialog
        pd = new ProgressDialog(this);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAddPlace(v);
            }
        });
    }

    private void showData() {
        db.collection("Places")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // called when data is retrieved
                        modelList.clear();
                        //show data
                        for (DocumentSnapshot doc : task.getResult()) {
                            Model model = new Model(doc.getString("id"),
                                    doc.getString("Name"),
                                    doc.getString("Description"),
                                    doc.getString("Date"));

                            modelList.add(model);
                        }
                        //adapter
                        adapter = new CustomAdapter(ListActivity.this, modelList);

                        //set adapter to recyclerview
                        mRecyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // called when there is any error while retireving

                        Toast.makeText(ListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteData(int index) {
        //set title of progress dialog
        pd.setTitle("Deletando dado...");
        //show progress dialog
        pd.show();

        db.collection("Places").document(modelList.get(index).getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // called when deleted successfuly
                        Toast.makeText(ListActivity.this, "Deletando...", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        //update data
                        showData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // called when there is any error
                        pd.dismiss();
                        //get and show error message
                        Toast.makeText(ListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goAddPlace(View view) {
        startActivity(new Intent(ListActivity.this, AddPlaceActivity.class));
    }
}