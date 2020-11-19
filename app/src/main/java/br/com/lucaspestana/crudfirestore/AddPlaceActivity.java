package br.com.lucaspestana.crudfirestore;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddPlaceActivity extends AppCompatActivity {

    EditText mName, mDescription;
    Button mSaveBtn, mCancelBtn;
    String date;

    // Firestore instance
    FirebaseFirestore db;

    // Gps
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int REQUEST_CODE_GPS = 1001;
    private Double latitudeCurrent;
    private Double longitudeCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mName = findViewById(R.id.input_name);
        mDescription = findViewById(R.id.input_description);
        mSaveBtn = findViewById(R.id.button_addplace);
        mCancelBtn = findViewById(R.id.button_cancel);

        //Firestore
        db = FirebaseFirestore.getInstance();

        // click button upload data
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // input data
                String name = mName.getText().toString();
                String descripton = mDescription.getText().toString();
                date = getDateTime();

                if (name == "" || name.isEmpty() || descripton == "" || descripton.isEmpty()) {
                    Toast.makeText(AddPlaceActivity.this, "Nome e descrição devem ser preenchidos!", Toast.LENGTH_SHORT).show();
                } else {
                    // function call to upload data
                    uploadData(name, descripton, date, latitudeCurrent, longitudeCurrent);
                    clearFields(v);
                    hideKeyboard(v);
                }
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields(v);
            }
        });

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Double lat  = location.getLatitude();
                Double lon = location.getLongitude();

                latitudeCurrent = lat;
                longitudeCurrent = lon;
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_GPS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
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

    private void uploadData(String name, String descripton, String date, Double lat , Double lon) {

        //random id for each data to be stored
        String id = UUID.randomUUID().toString();

        Map<String, Object> doc = new HashMap<>();
        doc.put("id", id); // id of data
        doc.put("Name", name);
        doc.put("Description", descripton);
        doc.put("Date", date);
        doc.put("Latitude", lat);
        doc.put("Longitude", lon);

        // add this data firestore
        db.collection("Places").document(id).set(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // this will be called when data is added successfully
                        Toast.makeText(AddPlaceActivity.this, "Adicionando...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // this will be called if there is any error while uploding
                        Toast.makeText(AddPlaceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields(View v) {
        mName.setText("");
        mDescription.setText("");
        getCurrentFocus().clearFocus();
    }
    private void hideKeyboard(View view) {
        InputMethodManager ims = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ims.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}