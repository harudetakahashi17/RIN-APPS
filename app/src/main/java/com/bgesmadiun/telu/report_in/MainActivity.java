package com.bgesmadiun.telu.report_in;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    Button btnCal, btnSave;
    int years,months, days;
    static final int DIALOGCAL_ID = 0;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        Spinner SPsalpen = findViewById(R.id.SPsalpen);
        Spinner SPketerangan = findViewById(R.id.SPketerangan);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Salpen));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SPsalpen.setAdapter(myAdapter);

        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Keterangan));
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SPketerangan.setAdapter(myAdapter2);

        final Calendar cal = Calendar.getInstance();
        years = cal.get(Calendar.YEAR);
        months = cal.get(Calendar.MONTH);
        days = cal.get(Calendar.DAY_OF_MONTH);
        showDialogOnButtonClick();
    }

    public void showDialogOnButtonClick(){
        btnCal = findViewById(R.id.calendar);

        btnCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOGCAL_ID);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if (id == DIALOGCAL_ID)
            return new DatePickerDialog(this, dplistener, years, months, days);
        return null;
    }

    private DatePickerDialog.OnDateSetListener dplistener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            years = year;
            months = month + 1;
            days = dayOfMonth;

            TextView d = findViewById(R.id.TVdate);
            TextView m = findViewById(R.id.TVmonth);
            TextView y = findViewById(R.id.TVyears);
            d.setText(new StringBuilder().append(days));
            m.setText(new StringBuilder().append(months));
            y.setText(new StringBuilder().append(years));
        }
    };

    public void dialogSave (View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Konfirmasi");
        alertDialogBuilder.setMessage("Apakah anda sudah yakin ?");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nmTempat = "",alamat = "",nmTeknisi = "",cp = "",odp,salpen,keterangan,detail;
                int c = 0;

                final EditText etNamaTempat = findViewById(R.id.ETnama_tempat);
                if (TextUtils.isEmpty(etNamaTempat.getText())){
                    etNamaTempat.setError("Nama Tempat Kosong");
                }else {
                    nmTempat = etNamaTempat.getText().toString();
                    c += 1;
                }

                final EditText etAlamat = findViewById(R.id.ETalamat);
                if (TextUtils.isEmpty(etAlamat.getText())){
                    etAlamat.setError("Alamat Kosong");
                }else {
                    alamat = etAlamat.getText().toString();
                    c += 1;
                }

                final EditText etNamaTeknisi = findViewById(R.id.ETnama_teknisi);
                if (TextUtils.isEmpty(etNamaTeknisi.getText())){
                    etNamaTeknisi.setError("Nama Tempat Kosong");
                }else {
                    nmTeknisi = etNamaTeknisi.getText().toString();
                    c += 1;
                }

                final EditText etCP = findViewById(R.id.ETcp);
                if (TextUtils.isEmpty(etCP.getText())){
                    etCP.setError("CP");
                } else {
                    cp = etCP.getText().toString();
                    c += 1;
                }

                final EditText etODP = findViewById(R.id.ETodp_ondesk);
                odp = etODP.getText().toString();

                String tglLaporan = String.valueOf(new StringBuilder().append(days).append(" / ")
                        .append(months).append(" / ").append(years));

                final Spinner etSalpen = findViewById(R.id.SPsalpen);
                salpen = etSalpen.getSelectedItem().toString();

                final Spinner etKeterangan = findViewById(R.id.SPketerangan);
                keterangan = etKeterangan.getSelectedItem().toString();

                final EditText etDetail = findViewById(R.id.ETdetails);
                detail = etDetail.getText().toString();


                System.out.println(nmTempat + " " + alamat + " " + nmTeknisi + " " + cp + " " + odp + " " +
                        tglLaporan + " " + salpen + " " + keterangan + " " + detail);



                // Resetting Form
                if (c == 4){
                    Map<String, String> datas = new HashMap<>();
                    datas.put("Nama Tempat", nmTempat);
                    datas.put("Alamat", alamat);
                    datas.put("Nama Teknisi", nmTeknisi);
                    datas.put("CP", cp);
                    datas.put("ODP On-Desk", odp);
                    datas.put("Tanggal", tglLaporan);
                    datas.put("Salpen", salpen);
                    datas.put("Keterangan", keterangan);
                    datas.put("Detail", detail);

                    db.collection("input_kendala").add(datas).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(MainActivity.this,"Input Berhasil",Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String err = e.getMessage();
                            Toast.makeText(MainActivity.this,"Error : "+err ,Toast.LENGTH_LONG).show();
                        }
                    });

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finish();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(MainActivity.this,"Lengkapi Form Kosong",Toast.LENGTH_LONG).show();
                }


            }

        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this,"Canceled",Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
