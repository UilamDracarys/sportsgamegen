package com.cpsu.sportgamegenerator.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cpsu.sportgamegenerator.Data.Repo.SchedRepo;
import com.cpsu.sportgamegenerator.R;
import com.cpsu.sportgamegenerator.Utils.SearchableAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ScheduleList extends AppCompatActivity {

    ArrayList<HashMap<String, String>> schedList;
    SearchableAdapter adapter;
    TextView mSchedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        getSupportActionBar().setTitle(title);
        loadSchedules();
    }

    private void loadSchedules() {

        SchedRepo schedRepo = new SchedRepo();
        schedList = schedRepo.getSchedules();
        ListView lv = findViewById(R.id.schedList);
        lv.setFastScrollEnabled(true);

        if (schedList.size() != 0) {
            lv = findViewById(R.id.schedList);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = getIntent();
                    Bundle b = intent.getExtras();

                    System.out.println("B=" + b);

                    if (b != null) {

                        mSchedId = view.findViewById(R.id.schedID);
                        String schedId = mSchedId.getText().toString();
                        Intent mIntent;
                        mIntent = new Intent(getApplicationContext(), ViewSchedule.class);
                        mIntent.putExtra("schedId", schedId);
                        mIntent.putExtra("title", "View Schedule");
                        startActivity(mIntent);

                    }
                }
            });
            adapter = new SearchableAdapter(ScheduleList.this,
                    schedList,
                    R.layout.sched_list_item,
                    new String[]{"SchedId", "SchedSport", "SchedDateType"},
                    new int[]{R.id.schedID, R.id.sport, R.id.dateType});
            lv.setAdapter(adapter);
        } else {
            adapter = null;
            lv.setAdapter(adapter);
        }

    }
}