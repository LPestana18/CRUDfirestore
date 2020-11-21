package br.com.lucaspestana.crudfirestore.ui;

import android.Manifest;
import android.app.ProgressDialog;
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
import androidx.appcompat.app.ActionBar;
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

import br.com.lucaspestana.crudfirestore.R;

public class AddPlaceActivity extends AppCompatActivity {

    EditText mName, mDescription;
    Button mSaveBtn, mCancelBtn;
    String date;
    ProgressDialog pd;
    // Firestore instance
    FirebaseFirestore db;

    String pId, pName, pDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        ActionBar actionBar = getSupportActionBar();

        mName = findViewById(R.id.input_name);
        mDescription = findViewById(R.id.input_description);
        mSaveBtn = findViewById(R.id.button_addplace);
        mCancelBtn = findViewById(R.id.button_cancel);

        //Firestore
        db = FirebaseFirestore.getInstance();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //Update data
            actionBar.setTitle("Atualização de lugares");
            mSaveBtn.setText("Atualizar");
            //get data
            pId = bundle.getString("pId");
            pName = bundle.getString("pName");
            pDescription = bundle.getString("pDescription");
            // set data
            mName.setText(pName);
            mDescription.setText(pDescription);
        } else {
            //New Data
            actionBar.setTitle("Cadastro de lugares");
            mSaveBtn.setText("Cadastrar");
        }

        //progress dialog
        pd = new ProgressDialog(this);

        /*if we came here after clicking Update option(from AlertDialog of ListActivity)
         * then get the data(name, description) from EditText, id from intent,
         * and update existing data on the basis of id*/
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = getIntent().getExtras();
                if (bundle1 != null) {
                    // updating
                    // input data
                    String id = pId;
                    String name = mName.getText().toString();
                    String descripton = mDescription.getText().toString();
                    // function call to update data
                    updateData(id, name, descripton);
                    clearFields(v);
                    hideKeyboard(v);
                } else {
                    //adding new
                    // input data
                    String name = mName.getText().toString();
                    String descripton = mDescription.getText().toString();
                    date = getDateTime();

                    if (name == "" || name.isEmpty() || descripton == "" || descripton.isEmpty()) {
                        Toast.makeText(AddPlaceActivity.this, "Nome e descrição devem ser preenchidos!", Toast.LENGTH_SHORT).show();
                    } else {
                        // function call to upload data
                        uploadData(name, descripton, date);
                        clearFields(v);
                        hideKeyboard(v);
                    }
                }
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields(v);
            }
        });
    }

    private void updateData(String id, String name, String descripton) {
        //set title of progress bar
        pd.setTitle("Atualizando dado...");

        pd.show();
        db.collection("Places").document(id)
                .update("Name", name, "Description", descripton)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // called when updated successfully
                        pd.dismiss();
                        Toast.makeText(AddPlaceActivity.this, "Atualização realizada com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(AddPlaceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadData(String name, String descripton, String date) {

        //random id for each data to be stored
        String id = UUID.randomUUID().toString();

        Map<String, Object> doc = new HashMap<>();
        doc.put("id", id); // id of data
        doc.put("Name", name);
        doc.put("Description", descripton);
        doc.put("Date", date);

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