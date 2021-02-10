package com.cpsu.sportgamegenerator.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cpsu.sportgamegenerator.R;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button mBtnSingle, mBtnDouble;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private void init() {
        mBtnSingle = findViewById(R.id.btnSingle);
        mBtnDouble = findViewById(R.id.btnDouble);
        mBtnSingle.setOnClickListener(this);
        mBtnDouble.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the save_cancel; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_scheds:
                showSchedList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnSingle) {
            type = "Single Elimination";
            showDialog();
        } else if (v == mBtnDouble) {
            type = "Double Elimination";
            showDialog();
        }
    }

    private void showDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View popUpDlg = layoutInflater.inflate(R.layout.pop_up_dlg, null);

        final Spinner sport = popUpDlg.findViewById(R.id.spnSport);
        final TextInputEditText teams = popUpDlg.findViewById(R.id.txtTeams);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Sport & Team Size")
                .setMessage("Please select the sport and input the number of teams:")
                .setView(popUpDlg)
                .setPositiveButton("Create Schedule", null)
                .setNegativeButton("Cancel", null)
                .show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sport.getSelectedItemPosition() == 0) {
                    Toast.makeText(getApplicationContext(), "Please select the sport.", Toast.LENGTH_SHORT).show();
                } else if (teams.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Please enter the number of teams", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(teams.getText().toString()) < 4 || Integer.parseInt(teams.getText().toString()) > 10) {
                    Toast.makeText(getApplicationContext(), "Number of teams should be from 4 to 10 only.", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    showSchedule(Integer.parseInt(teams.getText().toString()), sport.getSelectedItem().toString());
                }
            }
        });
    }

    private void showSchedule(int teams, String sport) {
        String title = teams + "-Team " + sport + " " + type + " Schedule";

        Intent intent = new Intent(this, CreateSchedule.class);
        intent.putExtra("title", title);
        intent.putExtra("noOfTeams", teams);
        intent.putExtra("type", type);
        intent.putExtra("sport", sport);
        startActivity(intent);
    }

    private void showSchedList() {
        Intent intent = new Intent(this, ScheduleList.class);
        intent.putExtra("title", "Schedules");
        startActivity(intent);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


}
