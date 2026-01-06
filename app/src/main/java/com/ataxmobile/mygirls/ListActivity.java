package com.ataxmobile.mygirls;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private static final String TAG = ListActivity.class.getSimpleName();

    private SqliteDatabase mDatabase;
    private ArrayList<Girls> allContacts=new ArrayList<>();
    private GirlAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        AdView mAdView = new AdView(this);
        DialogFragment dlg;

        // not clear why we need this...
        ConstraintLayout cLayout = (ConstraintLayout) findViewById(R.id.activity_list);

        RecyclerView contactView = (RecyclerView)findViewById(R.id.gList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        contactView.setLayoutManager(linearLayoutManager);
        contactView.setHasFixedSize(true);
        mDatabase = new SqliteDatabase(this);
        allContacts = mDatabase.listContacts();
        if(allContacts.size() > 0){
            contactView.setVisibility(View.VISIBLE);
            mAdapter = new GirlAdapter(this, allContacts);
            contactView.setAdapter(mAdapter);
        } else {
            contactView.setVisibility(View.GONE);
            dlg = new EmptyDBDialog();
            dlg.show(getFragmentManager(), "dlg");
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDatabase != null){
            mDatabase.close();
        }
    }

    public void onButBarClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.imgInfo:
                intent = new Intent(ListActivity.this, InfoActivity.class);
                startActivity(intent);
                break;
            case R.id.imgAdd:
                intent = new Intent(ListActivity.this, AddActivity.class);
                intent.putExtra("activity","list");
                startActivity(intent);
                break;
            case R.id.imgCalendar:
                intent = new Intent(ListActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.imgEdit:
                break;
            default:
                break;
        }

    }

    // this method is invoked from GirlAdapter when clicked on Edit icon on item
    public void editPostion(int gid) {
        Intent intent = new Intent(ListActivity.this, AddActivity.class);
        intent.putExtra("activity","list");
        intent.putExtra("gid", String.valueOf(gid));
        startActivity(intent);
    }

    // filtering methods
    // TODO : add star icon for sorting items in list
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_list, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        search(searchView);

        return true;
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mAdapter!=null)
                    mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}