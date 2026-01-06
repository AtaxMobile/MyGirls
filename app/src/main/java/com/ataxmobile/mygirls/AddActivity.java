package com.ataxmobile.mygirls;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class AddActivity extends AppCompatActivity {
    int year, month, day;                // to setup date of first day
    long ms = 0L;
    private String aFrom = "";
    private int gid = 0;
    private SqliteDatabase mDatabase;
    private EditText gName;
    private TextView fd;
    private Girls newContact, editGirl;
    private Spinner spinnerD1, spinnerD2;
    String[] d1Vals = new String[]{"19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38"};
    String[] d2Vals = new String[]{"3", "4", "5", "6", "7", "8", "9"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        AdView mAdView = new AdView(this);
        spinnerD1 = (Spinner) findViewById(R.id.spinnerD1);
        spinnerD2 = (Spinner) findViewById(R.id.spinnerD2);
        // fill spinner with values
        final List<String> d1List = new ArrayList<>(Arrays.asList(d1Vals));
        final ArrayAdapter<String> spinner1ArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, d1List);
        spinner1ArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerD1.setAdapter(spinner1ArrayAdapter);
        final List<String> d2List = new ArrayList<>(Arrays.asList(d2Vals));
        final ArrayAdapter<String> spinner2ArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, d2List);
        spinner2ArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerD2.setAdapter(spinner2ArrayAdapter);

        // extract all outer vars
        Intent intent = getIntent();
        aFrom = intent.getStringExtra("activity");
        if( intent.getStringExtra("gid") != null ) gid = Integer.parseInt(intent.getStringExtra("gid"));
        else gid = 0;
        // determine all UI objects
        gName = findViewById(R.id.editTextName);
        fd = (TextView) findViewById(R.id.editTextFD);
        mDatabase = new SqliteDatabase(this);

        if( gid > 0 ) {
            // We are editing - lets find girl
            editGirl = mDatabase.findContacts( gid, "" );

            // lets set values for editing
            gName.setText( editGirl.getName() );
            spinnerD1.setSelection(editGirl.getD1() - 19);
            spinnerD2.setSelection(editGirl.getD2() - 3);

            // convert long to readable date
            Calendar locCl = new GregorianCalendar();
            locCl.setTimeInMillis(editGirl.getFD());
            fd.setText( getReadableDate(locCl) );
            // save ms, just in case it will not be edited
            ms = editGirl.getFD();
        } else {
            // set meaningful default value
            gName.setText( getString(R.string.defGirlName) );
            Calendar locCl=Calendar.getInstance();
            spinnerD1.setSelection(9); spinnerD2.setSelection(2);
            fd.setText( getReadableDate(locCl) );
            ms = locCl.getTimeInMillis();
        }
        // define listener for FD object
        fd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // set current date as default in dialog, even if we editing girl
                Calendar mcurrentDate=Calendar.getInstance();
                year=mcurrentDate.get(Calendar.YEAR);
                month=mcurrentDate.get(Calendar.MONTH);
                day=mcurrentDate.get(Calendar.DAY_OF_MONTH);
                final DatePickerDialog mDatePicker = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday)
                    {
                        Calendar locCl = new GregorianCalendar();
                        locCl.set(Calendar.YEAR, selectedyear); locCl.set(Calendar.MONTH, selectedmonth); locCl.set(Calendar.DAY_OF_MONTH, selectedday);
                        // essential thing - setting up ms. this var will be used in saving (inserting or updating)
                        ms = locCl.getTimeInMillis();
                        fd.setText( getReadableDate(locCl) );
                    }
                },year, month, day);
                // TODO use string here
                mDatePicker.setTitle(R.string.dateDialog);
                // TODO Hide Future Date Here
                //mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                // TODO Hide Past Date Here
                //mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
                mDatePicker.show();
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // lets monetize
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }


    public void onButBarClick(View view) {
        Intent intent;
        int retInsVal = 0;
        switch(view.getId()){
            case R.id.imgInfo:
                intent = new Intent(AddActivity.this, InfoActivity.class);
                startActivity(intent);
                break;
            case R.id.imgCalendar:
                intent = new Intent(AddActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.imgSave:
                newContact = new Girls(gName.getText().toString(),
                                        Integer.parseInt(spinnerD1.getSelectedItem().toString()) , Integer.parseInt(spinnerD2.getSelectedItem().toString()), ms );
                // check whether girlName is unique
                if( gid == 0 ) {
                    // new girl, need insert
                    retInsVal = mDatabase.addContacts(newContact);
                }
                else {
                    // existing girl, need update
                    newContact.setId( editGirl.getId() );
                    retInsVal = mDatabase.updateContacts(newContact);
                }

                if( retInsVal == 0 ) {
                    // insert was successful, so terminate this activity
                    if (aFrom.equals("list"))
                        intent = new Intent(AddActivity.this, ListActivity.class);
                    else if (aFrom.equals("info"))
                        intent = new Intent(AddActivity.this, InfoActivity.class);
                    else
                        intent = new Intent(AddActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // girls name is not unique
                    Toast.makeText(AddActivity.this, getString(R.string.noUniqueName1) + " " + gName.getText().toString() + " " + getString(R.string.noUniqueName2), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.imgDel:
                if(aFrom.equals("list"))
                    intent = new Intent(AddActivity.this, ListActivity.class);
                else if(aFrom.equals("info"))
                    intent = new Intent(AddActivity.this, InfoActivity.class);
                else
                    intent = new Intent(AddActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.imgEdit:
                intent = new Intent(AddActivity.this, ListActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private String getReadableDate(Calendar cl) {
        int y = cl.get(Calendar.YEAR);
        int m = cl.get(Calendar.MONTH) + 1; // little trick here...
        int d = cl.get(Calendar.DAY_OF_MONTH);
        String outStr = "";

        outStr = (d>9?String.valueOf(d):"0"+String.valueOf(d)) + "-" +
                 (m>9?String.valueOf(m):"0"+String.valueOf(m)) + "-" +
                 String.valueOf(y);

        return outStr;
    }

}