package com.cpsu.sportgamegenerator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cpsu.sportgamegenerator.Data.Repo.SchedRepo;
import com.cpsu.sportgamegenerator.Data.Schedules;
import com.cpsu.sportgamegenerator.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewSchedule extends AppCompatActivity {


    String schedId, title;
    TableLayout schedTable;
    TextView schedDetails;

    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);

        Intent intent = getIntent();
        schedId = intent.getStringExtra("schedId");
        title = intent.getStringExtra("title");

        getSupportActionBar().setTitle(title);
        init();

        SchedRepo schedRepo = new SchedRepo();
        Schedules schedule = schedRepo.getSchedDetailById(schedId);
        schedDetails.setText("Schedule ID: " + schedule.getSchedId() + "\n" +
                "Sport: " + schedule.getSport() + "\n" +
                "Type: " + schedule.getType() + "\n" +
                "Date Created: " + schedule.getDate());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        loadSchedule(schedId);
    }

    private void init() {
        mLayout = findViewById(R.id.mainLayout);
        schedTable = findViewById(R.id.schedule);
        schedDetails = findViewById(R.id.schedDetails);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the save_cancel; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                /*checkPermissions();*/
                shareSched();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void loadSchedule(String schedId) {
        ArrayList<HashMap<String, String>> schedule = new ArrayList<>();
        SchedRepo schedRepo = new SchedRepo();
        schedule.addAll(schedRepo.getScheduleById(schedId));
        schedRepo.populateSchedTable(schedTable, schedule, this);
    }

    private void shareSched() {
        View rootView = mLayout;
        Bitmap bitmap = getScreenShot(rootView);
        store(bitmap, schedId + ".jpg");
    }

    public static Bitmap getScreenShot(View view) {
        //View screenView = view.getRootView();
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void store(Bitmap bm, String fileName){
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Schedules";
        File dir = new File(dirPath);
        if(!dir.exists()) {
            dir.mkdirs();
            System.out.println("Directory created!");
        }
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        shareImage(file);
    }

    private void shareImage(File file){
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(intent, "Share Schedule"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

}