package com.ad95.mcqmax.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.ad95.mcqmax.R;
import com.ad95.mcqmax.database.databaseHelper;
import com.ad95.mcqmax.utils.Common;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class TestStart extends AppCompatActivity {

    Map<String, ArrayList<String>> mcq;
    String progress;                        //Current question id
    Map<String, ArrayList<String>> choices;
    int score = 0;                          //Number of right answers
    int progress_no;                        //Question number reached
    ArrayList<String> serial;               //Question ids
    String category;
    TestStartFragment test;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        overridePendingTransition(R.anim.fade_in_splash, R.anim.fade_out_splash);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_start_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.colorPrimary));
        }

        category = getIntent().getStringExtra(Common.category);

        new Common(getApplicationContext());

        mcq = databaseHelper.getInstance(getApplicationContext()).fetchMCQ(category);

        serial = new ArrayList<>(mcq.keySet());
        progress = serial.get(0);

        Common.setSharedPreferences(category + Common.total, String.valueOf(serial.size()));

        int action = getIntent().getIntExtra(Common.action, -1);

        if (action == R.id.resume) {
            progress = Common.getSharedPreferences(category + Common.progress);
            score = Integer.parseInt(Common.getSharedPreferences(category + Common.score));
        } else {
            Common.removeSharedPreferences(category);
            Common.removeSharedPreferences(category + Common.progress);
        }

        progress_no = serial.indexOf(progress);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Common.progress_text + progress_no + "/" + serial.size());

        putFragment();

    }

    void putFragment() {

        if(snackbar != null) {
            snackbar.dismiss();
        }

        if (progress_no == serial.size()) {
            Intent intent = new Intent(TestStart.this, Results.class);
            intent.putExtra(Common.category, category);
            setResult(Activity.RESULT_OK);
            startActivity(intent);
            finish();
        }

        test = TestStartFragment.newInstance();
        choices = databaseHelper.getInstance(getApplicationContext()).fetchChoices(progress);

        Bundle bundle = new Bundle();
        bundle.putSerializable(Common.question, mcq.get(progress));
        bundle.putSerializable(Common.choicesTable, (Serializable) choices);
        test.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container, test)
                .commitNow();

    }

    void updateScore(String choice_selected) {

        progress_no++;

        if (Common.getSharedPreferences(category + Common.score).equals(Common.na)) {
            Common.setSharedPreferences(category + Common.score, "0");
        }

        if (choice_selected.equals(Objects.requireNonNull(mcq.get(progress)).get(0))) {
            score++;
            Common.setSharedPreferences(category + Common.score, String.valueOf(score));
            snackbar = Snackbar
                    .make(findViewById(R.id.snackbar), "That's the right answer!", Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            snackbar.show();
        } else {
            snackbar = Snackbar
                    .make(findViewById(R.id.snackbar), "Sorry, that's the wrong answer", Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.red));
            snackbar.show();
        }

        if (progress_no == serial.size()) {

            String progress1 = Common.getSharedPreferences(category + Common.history);

            if (progress1.equals(Common.na))
                Common.setSharedPreferences(category + Common.history, String.valueOf(score));
            else
                Common.setSharedPreferences(category + Common.history, progress1 + "," + score);

            Common.removeSharedPreferences(category);
            Common.removeSharedPreferences(category + Common.score);

        } else {

            Objects.requireNonNull(getSupportActionBar()).setTitle(Common.progress_text + progress_no + "/" + serial.size());

            progress = serial.get(progress_no);

            Common.setSharedPreferences(category, "");
            Common.setSharedPreferences(category + Common.progress, progress);

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        super.onBackPressed();
    }

}
