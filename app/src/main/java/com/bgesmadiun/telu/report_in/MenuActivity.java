package com.bgesmadiun.telu.report_in;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class MenuActivity extends AppCompatActivity {
    FirebaseFirestore db;
    final static private String TAG = "MenuActivity";
    Date date = new Date();
    // Initiate CSV Filename with present date-month-year
    private String file = "/Rekap_Kendala_"+date.getDate()+"_"+ date.getMonth()
            +"_"+ date.getYear()+".csv";
    // Getting external storage directory
    public String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/RiN-Apps";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        // Initiate database variable
        db = FirebaseFirestore.getInstance();
        Button bInput = findViewById(R.id.bInput);
        // Initiate a file variable for output
        File f = new File(path);
        // Make a directory if not exist yet
        f.mkdirs();

        // Button for Input View
        bInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
            }
        });

    }
    // Confirmation Method to download all data from database
    public void dialogExport(View v){
        final File f = new File(path + file);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Konfirmasi");
        alertDialogBuilder.setMessage("Download data terbaru ke format CSV?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Convertion function Database Firestore -> CSV
                saveFile(f);
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MenuActivity.this,"Canceled",Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void saveFile(final File file) {
        // Get Collection from FireStore
        db.collection("input_kendala")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Initialize to write file
                            FileOutputStream fos = null;
                            try {
                                // Declare fos as output file
                                fos = new FileOutputStream(file);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            for (DocumentSnapshot document : task.getResult()) {
                                // Logging catch data
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                // Make string to CSV format
                                String s = document.get("Tanggal")+","+document.get("Nama Tempat")+","+
                                        document.get("Alamat")+","+document.get("Nama Teknisi")
                                        +","+document.get("CP")+","+document.get("ODP On-Desk")
                                        +","+document.get("Salpen")+","+document.get("Keterangan")
                                        +","+document.get("Detail")+"\n";
                                try{
                                    // Writing to file using charset UTF-8
                                    fos.write(s.getBytes("UTF-8"));
                                    System.out.println(s);
                                }
                                catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // Give feedback if Successfully Downloaded
                            Toast.makeText(getApplicationContext(),"File Downloaded",Toast.LENGTH_SHORT).show();
                        } else {
                            // Give feedback if Failed Getting Documents
                            Log.w(TAG, "Error getting documents.", task.getException());
                            Toast.makeText(getApplicationContext(),"Error getting documents.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
