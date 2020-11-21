package br.com.lucaspestana.crudfirestore.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

import br.com.lucaspestana.crudfirestore.Adapter.CustomAdapter;
import br.com.lucaspestana.crudfirestore.Model.Model;
import br.com.lucaspestana.crudfirestore.R;

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

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int REQUEST_CODE_GPS = 1001;

    private double latitudeAtual;
    private double longitudeAtual;


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

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double lat  = location.getLatitude();
                double lon = location.getLongitude();

                latitudeAtual = lat;
                longitudeAtual = lon;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_GPS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 , 0, locationListener);
                }
            }
            else {
                Toast.makeText(this, getString(R.string.no_gps_no_app), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    public double getLatitudeAtual() {
        return latitudeAtual;
    }

    public double getLongitudeAtual() {
        return longitudeAtual;
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