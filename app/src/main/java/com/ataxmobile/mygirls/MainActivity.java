package com.ataxmobile.mygirls;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private Girls girl;
    private int gID = -1;
    private Calendar calendar, calendarForUI; // up till now only Today is implied in the first var
    private SimpleDateFormat formatLong, formatM, formatY;
    private boolean isDBEmpty = true;

    private FirebaseAnalytics mFirebaseAnalytics;

    // UI obects - probabilities
    TextView pv1, pv2, pv3;
    // UI obects - dates
    TextView TextViewCurrDate, TextViewCMonth, TextViewCYear;
    DialogFragment dlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        calendar = new GregorianCalendar(); calendarForUI = (Calendar) calendar.clone();
        formatLong = new SimpleDateFormat("dd.MM.yyyy");
        formatM = new SimpleDateFormat("LLLL" );
        formatY = new SimpleDateFormat("yyyy" );

        TextViewCurrDate = findViewById(R.id.textViewCurrDate);
        TextViewCurrDate.setText(formatLong.format( calendar.getTime() ).toString());

        TextViewCMonth = findViewById(R.id.textViewCMonth);
        TextViewCMonth.setText(formatM.format( calendarForUI.getTime() ).toString());
        TextViewCYear = findViewById(R.id.textViewCYear);
        TextViewCYear.setText(formatY.format( calendarForUI.getTime() ).toString());

        // init of UI - probs
        pv1 = findViewById(R.id.textViewP1Val);
        pv2 = findViewById(R.id.textViewP2Val);
        pv3 = findViewById(R.id.textViewP3Val);

        spinner = (Spinner)findViewById(R.id.spinner);
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // load list of girls and set the flag isDbEmpty
        loadSpinnerData();

        if( isDBEmpty ) {
            dlg = new EmptyDBDialog();
            dlg.show(getFragmentManager(), "dlg");
            initProbabilities(); loadSpreadsheet();
        }

    }

    // populate UI calendar
    private void loadSpreadsheet() {
        int dayNum = 0, dOfW, tailDay = 1, prevMd = 31, todayDay = 1;
        int m, y;
        boolean isInsideMonth;
        GregorianCalendar locC, locCPrev, locC2;

        // obtain first day of month
        m = calendarForUI.get(Calendar.MONTH); y = calendarForUI.get(Calendar.YEAR);
        locC = (GregorianCalendar) Calendar.getInstance(); todayDay = locC.get(Calendar.DAY_OF_MONTH);
        locC.set( y, m, 1);
        dOfW = locC.get(Calendar.DAY_OF_WEEK);

        // obtain numDays in previous month
        locCPrev = (GregorianCalendar) Calendar.getInstance();
        locCPrev.set(y, m, 1); locCPrev.add( Calendar.MONTH, -1);
        prevMd = locCPrev.getActualMaximum( Calendar.DATE );

        // create locC2
        locC2 = (GregorianCalendar) Calendar.getInstance();

        for(int i = 1; i<7; i++) {
            for (int j = 1; j < 8; j++) {
                // find the button
                String buttonID = "bc" + i + "_" + j;
                Button dm1_1 = (Button) findViewById(getResources().getIdentifier(buttonID, "id", getPackageName()));

                // we are inside the month
                isInsideMonth = true;
                if( i == 1 ) {
                    // first week of month, padding may be needed
                    if(dOfW == 1) {
                        // first day of month is Sunday, no padding
                        dayNum = 7*(i-1) + j - (dOfW-1);
                    } else {
                        // little trick here
                        if(j<dOfW) { dayNum = prevMd - dOfW + j + 1; isInsideMonth = false; }
                        else dayNum = j-dOfW+1;
                    }
                } else {
                    // ordinar and the last weeks
                    dayNum = 7*(i-1) + j - (dOfW-1);
                    if( dayNum > locC.getActualMaximum( Calendar.DATE ) ) {
                        isInsideMonth = false; dayNum = tailDay++;
                    }
                }

                // padding wih 0
                dm1_1.setText( (dayNum<10?"0" + String.valueOf(dayNum):String.valueOf(dayNum)) );
                dm1_1.setTextColor((isInsideMonth?getResources().getColor(R.color.black):getResources().getColor(R.color.grey1)));
                if( isInsideMonth && (!isDBEmpty) ) {
                    // lets construct the date
                    locC2.set( y, m, dayNum );
                    dm1_1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getSub(locC2, girl));
                } else {
                    dm1_1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getApplicationContext().getResources().getDrawable(R.drawable.shape_null_mark));
                }
                dm1_1.setCompoundDrawablePadding(0);
                if((isInsideMonth) && (dayNum==todayDay) && ((y == calendar.get(Calendar.YEAR)) && (m == calendar.get(Calendar.MONTH))) ) dm1_1.setBackgroundColor(getResources().getColor(R.color.grey1));
                else dm1_1.setBackgroundColor(getResources().getColor(R.color.white));
                // TODO remove it in xml
                dm1_1.setClickable(false);
            }
        }

    }

    protected Drawable getSub(Calendar c, Girls g) {
        Drawable img;
        int dayType = g.getSub(c);

        if(dayType == 1) img = getApplicationContext().getResources().getDrawable(R.drawable.shape_md_mark);
        else if(dayType == 2) img = getApplicationContext().getResources().getDrawable(R.drawable.shape_hm_mark);
        else if(dayType == 3) img = getApplicationContext().getResources().getDrawable(R.drawable.shape_fert_mark);
        else img = getApplicationContext().getResources().getDrawable(R.drawable.shape_null_mark);

        return img;
    }

    public void loadSpinnerData(){
        SqliteDatabase db = new SqliteDatabase(getApplicationContext());
        List<String> names = db.getAllNames();

        if(names.size() == 0) isDBEmpty = true;
        else isDBEmpty = false;

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SqliteDatabase db = new SqliteDatabase(parent.getContext());
        // On selecting a spinner item
        String label = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        girl = db.findContacts(0, label);
        gID = girl.getId();
        initProbabilities();
        loadSpreadsheet();

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    public void initProbabilities(){
        // lets calculate everything
        if( !isDBEmpty ) setProbs(calendar, girl);
    }

    public void setProbs(Calendar c, Girls g) {
        int n;
        int d1 = g.getD1(); int d2 = g.getD2(); long c2 = g.getFD();
        n = g.getCycleDate(c);
        // day of cycle is calculated, so lets determine the probabilities
        // prob of monthlies
        if( n <= d2 ) { pv3.setText(R.string.probVal4); pv3.setTextColor(getResources().getColor(R.color.very_high));}
        else if( n == d1 ) { pv3.setText(R.string.probVal3); pv3.setTextColor(getResources().getColor(R.color.high));}
        else if( (n == (d1-1)) || (n == (d2+1)) ) { pv3.setText(R.string.probVal2); pv3.setTextColor(getResources().getColor(R.color.low));}
        else {pv3.setText(R.string.probVal1); pv3.setTextColor(getResources().getColor(R.color.very_low));}

        // prob of PMS
        if( (n > (d1-5)) && (n <= d1) ) { pv1.setText(R.string.probVal4); pv1.setTextColor(getResources().getColor(R.color.very_high)); }
        else if( n == 1) {pv1.setText(R.string.probVal3); pv1.setTextColor(getResources().getColor(R.color.high));}
        else {pv1.setText(R.string.probVal1); pv1.setTextColor(getResources().getColor(R.color.very_low));}

        // probs of fertility
        int ovd = (int) d1/2;
        if( n <= d2 ) {pv2.setText(R.string.probVal1); pv2.setTextColor(getResources().getColor(R.color.very_low));}
        else if ( n == ovd) {pv2.setText(R.string.probVal4); pv2.setTextColor(getResources().getColor(R.color.very_high));}
        else if ( (n > (ovd-4)) && (n < (ovd+3)) ) {pv2.setText(R.string.probVal3); pv2.setTextColor(getResources().getColor(R.color.high));}
        else {pv2.setText(R.string.probVal2); pv2.setTextColor(getResources().getColor(R.color.low));}
    }

    public void onButBarClick(View view){
        Intent intent;
        switch (view.getId()) {
            case R.id.imgInfo:
                intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
                break;
            case R.id.imgAdd:
                intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtra("activity","main");
                intent.putExtra("gid", "0");
                startActivity(intent);
                break;
            case R.id.imgCalendar:
                break;
            case R.id.imgEdit:
                intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void onButCRClick(View view) {
        calendarForUI.add(Calendar.MONTH , 1);
        TextViewCMonth.setText(formatM.format( calendarForUI.getTime() ).toString());
        TextViewCYear.setText(formatY.format( calendarForUI.getTime() ).toString());

        loadSpreadsheet();
    }

    public void onButCLClick(View view) {
        calendarForUI.add(Calendar.MONTH , -1);
        TextViewCMonth.setText(formatM.format( calendarForUI.getTime() ).toString());
        TextViewCYear.setText(formatY.format( calendarForUI.getTime() ).toString());

        loadSpreadsheet();
    }
}