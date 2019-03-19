package com.example.ridesafedatacollection;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class main_Settings extends AppCompatActivity {


    Button deletebtn;
    Button exportbtn;
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__settings);
        mDatabaseHelper = new DatabaseHelper(this);
        exportinit();
        deleteinit();


    }


    public void deleteinit() {
        deletebtn = findViewById(R.id.Delete_button);
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDB();

            }
        });


    }


    public void exportinit() {
        exportbtn = findViewById(R.id.export_button);
        exportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCrashQ();


            }
        });


    }


    private void exportDB(String filename) {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;
        String currentDBPath = "/data/" + "com.example.nanou.ridesafe" + "/databases/" + mDatabaseHelper.getDBname();
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, filename);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            emailDatabase(filename);
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void emailDatabase(String filename) {


        File sd = Environment.getExternalStorageDirectory();
        File backupDB = new File(sd, filename);


        Uri path = Uri.fromFile(backupDB);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
// set the type to 'email'
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"monganai@tcd.ie"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
// the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
// the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        startActivityForResult(Intent.createChooser(emailIntent, "Send email..."), 101);


    }


    public void deleteDB() {
        mDatabaseHelper.deleteData("sensor_values");
        Toast.makeText(this, "DB Deleted", Toast.LENGTH_LONG).show();
    }


    public void showCrashQ() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(main_Settings.this);
// Setting Dialog Title
        alertDialog.setTitle("Crash Question");
// Setting Dialog Message
        alertDialog.setMessage("while using Ridesafe, did you have any crashes/falls?");
// On pressing Settings button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


                //add a line containing  1,1,1,1,1,1
                ContentValues values = new ContentValues();
                values.put("gforce", 1);
                values.put("gx", 1);
                values.put("gy", 1);
                values.put("gz", 1);
                values.put("speed", 1);
                mDatabaseHelper.addRow(values, "sensor_values");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                String fname = simpleDateFormat.format(new Date());
                String FILENAME = mDatabaseHelper.getDBname() + "_" + fname + ".db";
                exportDB(FILENAME);
                dialog.cancel();

            }
        });
// on pressing cancel button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //add a line containing  0,0,0,0,0,0,0
                ContentValues values = new ContentValues();
                values.put("gforce", 0);
                values.put("gx", 0);
                values.put("gy", 0);
                values.put("gz", 0);
                values.put("speed", 0);
                mDatabaseHelper.addRow(values, "sensor_values");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                String fname = simpleDateFormat.format(new Date());
                String FILENAME = mDatabaseHelper.getDBname() + "_" + fname + ".db";
                exportDB(FILENAME);
                dialog.cancel();

            }
        });
// Showing Alert Message
        alertDialog.show();
    }


}
