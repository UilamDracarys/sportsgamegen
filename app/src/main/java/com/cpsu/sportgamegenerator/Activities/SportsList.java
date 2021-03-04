package com.cpsu.sportgamegenerator.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cpsu.sportgamegenerator.Data.Repo.SportRepo;
import com.cpsu.sportgamegenerator.R;
import com.cpsu.sportgamegenerator.Utils.SearchableAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class SportsList extends AppCompatActivity {

    SearchableAdapter adapter;
    TextView txtSportID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports_list);
        loadSports();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the save_cancel; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_sport:
                showSportDlg("Add");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSportDlg(final String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(action + " Sport")
                .setMessage("Please enter name of sport:");

        final EditText sportName = new EditText(this);
        builder.setView(sportName);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (sportName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Nothing was saved.", Toast.LENGTH_SHORT).show();
                } else {
                    String value = sportName.getText().toString();
                    SportRepo sportRepo = new SportRepo();
                    if (sportRepo.isSportExisting(value)) {
                        Toast.makeText(getApplicationContext(), "Sport already exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        save(value, action);
                    }
                }
                return;
            }
        });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        return;
                    }
                });
        builder.show();
    }

    private void save(String sportName, String action) {
        SportRepo sportRepo = new SportRepo();
        if (action.equalsIgnoreCase("Add")) {
            sportRepo.insert(sportName);
            loadSports();
        }
    }

    private void loadSports() {
        final SportRepo sportRepo = new SportRepo();
        ArrayList<HashMap<String, String>> sportList = sportRepo.getSportList();
        ListView lv = findViewById(R.id.sportList);
        lv.setFastScrollEnabled(true);
        if (sportList.size() != 0) {
            lv = findViewById(R.id.sportList);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    Intent intent = getIntent();
                    Bundle b = intent.getExtras();
                    System.out.println(intent.getExtras());

                    if (b != null) {

                        txtSportID = view.findViewById(R.id.sportID);
                        String sportID = txtSportID.getText().toString();

                    }
                }
            });
            adapter = new SearchableAdapter(SportsList.this,
                    sportList,
                    R.layout.activity_sport_list_item,
                    new String[]{"ID", "Name"},
                    new int[]{R.id.sportID, R.id.sportName});
            lv.setAdapter(adapter);
        } else {
            adapter = null;
            lv.setAdapter(adapter);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        loadSports();
    }

}