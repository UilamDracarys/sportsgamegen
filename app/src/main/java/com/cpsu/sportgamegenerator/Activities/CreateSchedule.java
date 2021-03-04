package com.cpsu.sportgamegenerator.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cpsu.sportgamegenerator.Data.Repo.SchedRepo;
import com.cpsu.sportgamegenerator.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.apache.commons.lang3.RandomStringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CreateSchedule extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    TextView txtTeams;
    int noOfTeams;
    List<String> teams = new ArrayList<>();
    List<String> origTeams = new ArrayList<>();
    ArrayList<HashMap<String, String>> schedule, gameSchedule;
    LinearLayout teamNamesLayout, gamesSchedLayout;
    String type, sport;
    TextView mUpdate;
    TableLayout schedTable;
    String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);
        schedule = new ArrayList<>();
        gameSchedule = new ArrayList<>();

        init();
        Intent intent = getIntent();
        noOfTeams = intent.getIntExtra("noOfTeams", 0);
        teams = generateTeams(noOfTeams);
        System.out.println("TeamList: " + teams);

        origTeams.addAll(teams);

        type = intent.getStringExtra("type");
        sport = intent.getStringExtra("sport");

        addTeamNames(teams);

        if (type.equalsIgnoreCase("Single Elimination")) {
            Collections.shuffle(teams);
            schedule = genSingleElim(teams);
        } else {
            Collections.shuffle(teams);
            schedule = genDoubleElim(teams);
        }

        addGameScheds(schedule);

        getSupportActionBar().setTitle(intent.getStringExtra("title"));
    }

    private void init() {
        //TODO
        txtTeams = findViewById(R.id.txtTeams);
        mUpdate = findViewById(R.id.btnUpdateTeams);
        mUpdate.setOnClickListener(this);
        teamNamesLayout = findViewById(R.id.teamNameLayout);
        gamesSchedLayout = findViewById(R.id.gameSchedLayout);
        schedTable = findViewById(R.id.schedule);
    }

    private List<String> generateTeams(int teamSize) {
        List<String> teamList = new ArrayList<>();
        for (int i = 0; i < teamSize; i++) {
            teamList.add("Team " + (i+1));
        }
        return teamList;
    }

    private void addTeamNames(List<String> teams) {
        for (int i=0; i<teams.size();i++) {
            TextInputLayout textInputLayout = new TextInputLayout(this);
            TextInputEditText teamName = new TextInputEditText(this);
            textInputLayout.setHintTextAppearance(R.style.TextInputHint);
            teamName.setText(teams.get(i));
            teamName.setHint(teams.get(i));
            teamName.setTextSize(14);
            textInputLayout.addView(teamName);
            teamNamesLayout.addView(textInputLayout);
        }
    }

    private void addGameScheds(ArrayList<HashMap<String, String>> schedule) {
        for (int i=0; i<schedule.size();i++) {
            TextInputLayout textInputLayout = new TextInputLayout(this);
            final TextInputEditText gameSched = new TextInputEditText(this);
            textInputLayout.setHintTextAppearance(R.style.TextInputHint);
            gameSched.setText("Set Date");
            gameSched.setHint("Game " + (schedule.get(i).get("Game")));
            gameSched.setTextSize(14);
            gameSched.setTag((schedule.get(i).get("Game")));
            gameSched.setKeyListener(null);
            textInputLayout.addView(gameSched);
            gameSched.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    TAG = gameSched.getTag().toString();

                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            CreateSchedule.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(getFragmentManager(), "Schedule Date  ");
                }
            });

            gamesSchedLayout.addView(textInputLayout);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the save_cancel; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sched_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                try {
                    save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.action_check:
                try {
                    isScheduleComplete();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSchedule() {

        List<String> newTeamList = new ArrayList<>();
        for (int i=0;i<teams.size(); i++) {

            TextInputLayout textInputLayout = (TextInputLayout) teamNamesLayout.getChildAt(i);
            FrameLayout frameLayout = (FrameLayout) textInputLayout.getChildAt(0);
            TextInputEditText textInputEditText = (TextInputEditText) frameLayout.getChildAt(0);
            teams.set(teams.indexOf(origTeams.get(i)), textInputEditText.getText().toString());
            newTeamList.add(textInputEditText.getText().toString());

        }

        origTeams.clear();
        origTeams = newTeamList;
        genSingleElim(teams);
    }

    private ArrayList<HashMap<String, String>> genSingleElim(List<String> list) {

        SchedRepo schedRepo = new SchedRepo();
        schedule = new ArrayList<>();

        switch (noOfTeams) {
            case 4:
                schedule.add(schedRow(list.get(0),  list.get(1),  "1",  "1"));
                schedule.add(schedRow(list.get(2),  list.get(3),  "1",  "2"));
                schedule.add(schedRow("Winner Game 1",  "Winner Game 2",  "2",  "3"));
                break;
            case 5:
                schedule.add(schedRow(list.get(0),  list.get(1),  "1",  "1"));
                schedule.add(schedRow(list.get(2),  "Winner Game 1",  "2",  "2"));
                schedule.add(schedRow(list.get(3),  list.get(4),  "2",  "3"));
                schedule.add(schedRow("Winner Game 2",  "Winner Game 3",  "3",  "4"));
                break;
            case 6:
                schedule.add(schedRow(list.get(0),  list.get(1),  "1",  "1"));
                schedule.add(schedRow(list.get(2),  list.get(3),  "1",  "2"));
                schedule.add(schedRow(list.get(4),  "Winner Game 1",  "2",  "3"));
                schedule.add(schedRow(list.get(5),  "Winner Game 2",  "2",  "4"));
                schedule.add(schedRow("Winner Game 3",  "Winner Game 4",  "3",  "5"));
                break;
            case 7:
                schedule.add(schedRow(list.get(0),  list.get(1),  "1",  "1"));
                schedule.add(schedRow(list.get(2),  list.get(3),  "1",  "2"));
                schedule.add(schedRow(list.get(4),  list.get(5),  "1",  "3"));
                schedule.add(schedRow(list.get(6),  "Winner Game 1",  "2",  "4"));
                schedule.add(schedRow("Winner Game 2",  "Winner Game 3",  "2",  "5"));
                schedule.add(schedRow("Winner Game 4",  "Winner Game 5",  "3",  "6"));
                break;

            case 8:
                schedule.add(schedRow(list.get(0),  list.get(1),  "1",  "1"));
                schedule.add(schedRow(list.get(2),  list.get(3),  "1",  "2"));
                schedule.add(schedRow(list.get(4),  list.get(5),  "1",  "3"));
                schedule.add(schedRow(list.get(6),  list.get(7),  "1",  "4"));
                schedule.add(schedRow("Winner Game 1",  "Winner Game 2",  "2",  "5"));
                schedule.add(schedRow("Winner Game 3",  "Winner Game 4",  "2",  "6"));
                schedule.add(schedRow("Winner Game 5",  "Winner Game 6",  "3",  "7"));
                break;

            case 9:
                schedule.add(schedRow(list.get(0),  list.get(1),  "1",  "1"));
                schedule.add(schedRow(list.get(2),  "Winner Game 1",  "2",  "2"));
                schedule.add(schedRow(list.get(3),  list.get(4),  "2",  "3"));
                schedule.add(schedRow(list.get(5),  list.get(6),  "2",  "4"));
                schedule.add(schedRow(list.get(7),  list.get(8),  "2",  "5"));
                schedule.add(schedRow("Winner Game 2",  "Winner Game 3",  "3",  "6"));
                schedule.add(schedRow("Winner Game 4",  "Winner Game 5",  "3",  "7"));
                schedule.add(schedRow("Winner Game 6",  "Winner Game 7",  "4",  "8"));
                break;

            case 10:
                schedule.add(schedRow(list.get(0),  list.get(1),  "1",  "1"));
                schedule.add(schedRow(list.get(2),  list.get(3),  "1",  "2"));
                schedule.add(schedRow(list.get(4),  "Winner Game 1",  "2",  "3"));
                schedule.add(schedRow(list.get(5),  list.get(6),  "2",  "4"));
                schedule.add(schedRow(list.get(7),  "Winner Game 2",  "2",  "5"));
                schedule.add(schedRow(list.get(8),  list.get(9),  "2",  "6"));
                schedule.add(schedRow("Winner Game 3",  "Winner Game 4",  "3",  "7"));
                schedule.add(schedRow("Winner Game 5",  "Winner Game 6",  "3",  "8"));
                schedule.add(schedRow("Winner Game 7",  "Winner Game 8",  "4",  "9"));
                break;
            default:
                break;

        }

        schedRepo.populateSchedTable(schedTable, schedule, this);
        System.out.println("Schedule:\n\n");
        for (HashMap<String, String> map: schedule){
            System.out.println(map);
        }
        return schedule;
    }

    private ArrayList<HashMap<String, String>> genDoubleElim(List<String> list) {

        SchedRepo schedRepo = new SchedRepo();
        schedule = new ArrayList<>();

        switch (noOfTeams) {
            case 4:
                schedule.add(schedRow(list.get(0), list.get(1), "1", "1"));
                schedule.add(schedRow(list.get(2), list.get(3), "1", "2"));
                schedule.add(schedRow("Winner Game 1", "Winner Game 2", "2", "3"));
                schedule.add(schedRow("Loser Game 1", "Loser Game 2", "1", "4"));
                schedule.add(schedRow("Loser Game 3", "Winner Game 4", "2", "5"));
                schedule.add(schedRow("Winner Game 3", "Winner Game 5", "3", "6"));
                schedule.add(schedRow("Winner Game 6", "Loser Game 6", "4", "7"));
                break;
            case 5:
                schedule.add(schedRow(list.get(0), list.get(1), "1", "1"));
                schedule.add(schedRow(list.get(2), "Winner Game 1", "2", "2"));
                schedule.add(schedRow(list.get(3), list.get(4), "2", "3"));
                schedule.add(schedRow("Loser Game 1", "Loser Game 2", "2", "4"));
                schedule.add(schedRow("Winner Game 2", "Winner Game 3", "3", "5"));
                schedule.add(schedRow("Winner Game 4", "Loser Game 3", "3", "6"));
                schedule.add(schedRow("Loser Game 5", "Winner Game 6", "4", "7"));
                schedule.add(schedRow("Winner Game 5", "Winner Game 7", "5", "8"));
                schedule.add(schedRow("Winner Game 8", "Loser Game 8", "6", "9"));
                break;
            case 6:
                schedule.add(schedRow(list.get(0), list.get(1), "1", "1"));
                schedule.add(schedRow(list.get(2), "Winner Game 1", "2", "2"));
                schedule.add(schedRow(list.get(3), list.get(4), "2", "3"));
                schedule.add(schedRow("Loser Game 1", "Loser Game 2", "2", "4"));
                schedule.add(schedRow("Winner Game 2", "Winner Game 3", "3", "5"));
                schedule.add(schedRow("Winner Game 4", "Loser Game 3", "3", "6"));
                schedule.add(schedRow(list.get(5), "Winner Game 6", "4", "7"));
                schedule.add(schedRow("Winner Game 5", "Winner Game 7", "5", "8"));
                schedule.add(schedRow("Winner Game 8", "Loser Game 8", "6", "9"));
                break;
            case 7:
                schedule.add(schedRow(list.get(0), list.get(1), "1", "1"));
                schedule.add(schedRow(list.get(2), list.get(3), "1", "2"));
                schedule.add(schedRow(list.get(4), list.get(5), "1", "3"));
                schedule.add(schedRow(list.get(6), "Winner Game 1", "2", "4"));
                schedule.add(schedRow("Winner Game 2", "Winner Game 3", "2", "5"));
                schedule.add(schedRow("Loser Game 2", "Loser Game 3", "2", "6"));
                schedule.add(schedRow("Loser Game 1", "Loser Game 5", "3", "7"));
                schedule.add(schedRow("Loser Game 4", "Winner Game 6", "3", "8"));
                schedule.add(schedRow("Winner Game 4", "Winner Game 5", "4", "9"));
                schedule.add(schedRow("Winner Game 8", "Winner Game 7", "4", "10"));
                schedule.add(schedRow("Loser Game 9", "Winner Game 10", "5", "11"));
                schedule.add(schedRow("Winner Game 9", "Winner Game 11", "6", "12"));
                schedule.add(schedRow("Winner Game 12", "Loser Game 12", "7", "13"));
                break;
            case 8:
                schedule.add(schedRow(list.get(0), list.get(1), "1", "1"));
                schedule.add(schedRow(list.get(2), list.get(3), "1", "2"));
                schedule.add(schedRow(list.get(4), list.get(5), "1", "3"));
                schedule.add(schedRow(list.get(6), list.get(7), "1", "4"));
                schedule.add(schedRow("Loser Game 1", "Loser Game 2", "2", "5"));
                schedule.add(schedRow("Loser Game 3", "Loser Game 4", "2", "6"));
                schedule.add(schedRow("Winner Game 1", "Winner Game 2", "2", "7"));
                schedule.add(schedRow("Winner Game 3", "Winner Game 4", "2", "8"));
                schedule.add(schedRow("Loser Game 8", "Winner Game 5", "3", "9"));
                schedule.add(schedRow("Loser Game 7", "Winner Game 6", "3", "10"));
                schedule.add(schedRow("Winner Game 7", "Winner Game 8", "4", "11"));
                schedule.add(schedRow("Winner Game 9", "Winner Game 10", "4", "12"));
                schedule.add(schedRow("Loser Game 11", "Winner Game 12", "5", "13"));
                schedule.add(schedRow("Winner Game 11", "Winner Game 13", "6", "14"));
                schedule.add(schedRow("Winner Game 14", "Loser Game 14", "7", "15"));
                break;
            case 9:
                schedule.add(schedRow(list.get(0), list.get(1), "1", "1"));
                schedule.add(schedRow(list.get(2), list.get(3), "2", "2"));
                schedule.add(schedRow(list.get(4), list.get(5), "2", "3"));
                schedule.add(schedRow(list.get(6), "Winner Game 1", "2", "4"));
                schedule.add(schedRow(list.get(7), list.get(8), "2", "5"));
                schedule.add(schedRow("Loser Game 1", "Loser Game 2", "2", "6"));
                schedule.add(schedRow("Loser Game 4", "Loser Game 5", "3", "7"));
                schedule.add(schedRow("Loser Game 3", "Winner Game 6", "3", "8"));
                schedule.add(schedRow("Winner Game 2", "Winner Game 3", "3", "9"));
                schedule.add(schedRow("Winner Game 5", "Winner Game 4", "3", "10"));
                schedule.add(schedRow("Winner Game 7", "Loser Game 9", "4", "11"));
                schedule.add(schedRow("Loser Game 10", "Winner Game 8", "4", "12"));
                schedule.add(schedRow("Winner Game 9", "Winner Game 10", "5", "13"));
                schedule.add(schedRow("Winner Game 11", "Winner Game 12", "5", "14"));
                schedule.add(schedRow("Loser Game 13", "Winner Game 14", "6", "15"));
                schedule.add(schedRow("Winner Game 13", "Loser Game 15", "7", "16"));
                schedule.add(schedRow("Winner Game 16", "Loser Game 16", "8", "17"));
                break;
            case 10:
                schedule.add(schedRow(list.get(0), list.get(1), "1", "1"));
                schedule.add(schedRow(list.get(2), list.get(3), "1", "2"));
                schedule.add(schedRow(list.get(4), list.get(5), "2", "3"));
                schedule.add(schedRow(list.get(6), list.get(7), "2", "4"));
                schedule.add(schedRow("Winner Game 1", list.get(8), "2", "5"));
                schedule.add(schedRow(list.get(9), "Winner Game 2", "2", "6"));
                schedule.add(schedRow("Loser Game 2", "Loser Game 5", "2", "7"));
                schedule.add(schedRow("Loser Game 1", "Loser Game 6", "2", "8"));
                schedule.add(schedRow("Winner Game 7", "Loser Game 3", "3", "9"));
                schedule.add(schedRow("Loser Game 4", "Winner Game 8", "3", "10"));
                schedule.add(schedRow("Winner Game 3", "Winner Game 5", "3", "11"));
                schedule.add(schedRow("Winner Game 6", "Winner Game 4", "3", "12"));
                schedule.add(schedRow("Loser Game 12", "Winner Game 9", "4", "13"));
                schedule.add(schedRow("Winner Game 10", "Loser Game 11", "4", "14"));
                schedule.add(schedRow("Winner Game 11", "Winner Game 12", "5", "15"));
                schedule.add(schedRow("Winner Game 13", "Winner Game 14", "5", "16"));
                schedule.add(schedRow("Loser Game 15", "Winner Game 15", "6", "17"));
                schedule.add(schedRow("Winner Game 15", "Winner Game 17", "7", "18"));
                schedule.add(schedRow("Winner Game 18", "Loser Game 18", "8", "19"));
                break;
            default:
                break;

        }

        schedRepo.populateSchedTable(schedTable, schedule, this);
        System.out.println("Schedule:\n\n");
        for (HashMap<String, String> map: schedule){
            System.out.println(map);
        }
        return schedule;
    }


    private HashMap<String, String> schedRow(String team1, String team2, String round, String game) {
        HashMap<String, String> schedRow = new HashMap<>();
        schedRow.put("Team1", team1);
        schedRow.put("Team2", team2);
        schedRow.put("Round", round);
        schedRow.put("Game", game);
        return schedRow;
    }

    @Override
    public void onClick(View v) {
        if (v == mUpdate) {
            updateSchedule();
        }
    }

    private void save() throws ParseException {
        if (isScheduleComplete()) {
            String schedId = RandomStringUtils.randomAlphanumeric(8);
            SchedRepo schedRepo = new SchedRepo();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            schedRepo.insert(schedule, schedId, sdf.format(new Date()), sport, type);
            schedRepo.insertGameSched(schedId, gameSchedule);

            Toast.makeText(this, "Schedule Saved!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, ScheduleList.class);
            intent.putExtra("title", "Schedules");
            startActivity(intent);

            finish();
        } else {
            Toast.makeText(this, "Please complete the game schedule.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth);
        Date date = cal.getTime();
        DateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

        for (int i=0;i<schedule.size(); i++) {
            TextInputLayout textInputLayout = (TextInputLayout) gamesSchedLayout.getChildAt(i);
            FrameLayout frameLayout = (FrameLayout) textInputLayout.getChildAt(0);
            TextInputEditText textInputEditText = (TextInputEditText) frameLayout.getChildAt(0);
            if (textInputEditText.getTag().equals(TAG)) {
                textInputEditText.setText(df.format(date));
            }
        }
    }

    public boolean isScheduleComplete() throws ParseException {
        ArrayList<HashMap<String, String>> sched = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i=0;i<schedule.size(); i++) {
            TextInputLayout textInputLayout = (TextInputLayout) gamesSchedLayout.getChildAt(i);
            FrameLayout frameLayout = (FrameLayout) textInputLayout.getChildAt(0);
            TextInputEditText textInputEditText = (TextInputEditText) frameLayout.getChildAt(0);

            if (!textInputEditText.getText().toString().equalsIgnoreCase("Set Date")) {
                HashMap<String, String> map = new HashMap<>();
                Date date = new SimpleDateFormat("MMM d, yyyy").parse(textInputEditText.getText().toString());
                String dateForSQL = df.format(date);
                map.put("Game", textInputEditText.getTag().toString());
                map.put("Date", dateForSQL);
                sched.add(map);
            } else {
                break;
            }
        }

        if (sched.size() != schedule.size()) {
            return false;
        } else {
            gameSchedule.addAll(sched);
            return true;
        }
    }
}