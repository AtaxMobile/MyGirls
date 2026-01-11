package com.ataxmobile.mygirls;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.play.core.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import java.util.ArrayList;


public class InfoActivity extends AppCompatActivity {

    private ReviewManager reviewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        TextView iRate;
        ImageButton iBR;
        Button iBTDay;
        DialogFragment dlg;
        SqliteDatabase mDatabase;
        ArrayList<Girls> allContacts=new ArrayList<>();

        iRate = findViewById(R.id.iRate);
        iBR = findViewById(R.id.imgButtonRate);
        iBTDay = findViewById(R.id.bc1_0);
        iBTDay.setBackgroundColor(getResources().getColor(R.color.grey1));

        mDatabase = new SqliteDatabase(this);
        allContacts = mDatabase.listContacts();
        if(allContacts.size() == 0) {
            iRate.setVisibility(View.GONE); iBR.setVisibility(View.GONE);
        }

        reviewManager = ReviewManagerFactory.create(this);
    }

    public void onButBarClick(View view) {
        Intent intent;
        switch(view.getId()){
            case R.id.imgInfo:
                break;
            case R.id.imgCalendar:
                intent = new Intent(InfoActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.imgAdd:
                intent = new Intent(InfoActivity.this, AddActivity.class);
                intent.putExtra("activity","info");
                intent.putExtra("gid", "0");
                startActivity(intent);
                break;
            case R.id.imgEdit:
                intent = new Intent(InfoActivity.this, ListActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void onButShareClick(View view) {
/***
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.ShareSubj));
        String shareMessage=getString(R.string.ShareMsg) + " https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +" \n\n";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.ShareWay)));
***/
        throw new RuntimeException("Test Crash"); // Force a crash
    }

    public void onButRateClick(View view) {
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();

                Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                // There was some problem, continue regardless of the result.
                // show native rate app dialog on error
//                showRateAppFallbackDialog();
            }
        });
/*
        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
            } else {
                // There was some problem, log or handle the error code.
//                @ReviewErrorCode int reviewErrorCode = ( task.getException()).getErrorCode();
            }
        });

        Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
        flow.addOnCompleteListener(task -> {
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
        });
*/    }

}