package com.ad95.mcqmax;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.ad95.mcqmax.database.databaseHelper;
import com.ad95.mcqmax.test.Results;
import com.ad95.mcqmax.test.TestStart;
import com.ad95.mcqmax.utils.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

public class MainScreen extends AppCompatActivity {

    Map<String, CardView> cat_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        overridePendingTransition(R.anim.fade_in_splash, R.anim.fade_out_splash);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.colorPrimary));
        }

        LinearLayout linearLayout = findViewById(R.id.card_layout);

        databaseHelper db = databaseHelper.getInstance(getApplicationContext());

        ArrayList<String> categories = db.fetchCategories();

        new Common(getApplicationContext());

        cat_map = new HashMap<>();

        for (final String cat : categories) {

            final CardView cardView = (CardView) LayoutInflater.from(this).inflate(R.layout.category_card, linearLayout, false);

            final String history = Common.getSharedPreferences(cat + Common.history);

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.history) {

                        Intent intent = new Intent(MainScreen.this, Results.class);
                        intent.putExtra(Common.category, cat);

                        startActivity(intent);

                    } else if (v.getId() == R.id.restart) {

                        Common.removeSharedPreferences(cat);
                        Common.removeSharedPreferences(cat + Common.score);
                        Common.removeSharedPreferences(cat + Common.progress);

                        cardView.findViewById(R.id.starting).setVisibility(View.VISIBLE);
                        cardView.findViewById(R.id.progress).setVisibility(GONE);

                        if (history.equals(Common.na)) {
                            cardView.findViewById(R.id.history).setVisibility(GONE);
                            cardView.findViewById(R.id.divider1).setVisibility(GONE);
                        }

                    } else {

                        Intent intent = new Intent(MainScreen.this, TestStart.class);
                        intent.putExtra(Common.category, cat);
                        intent.putExtra(Common.action, v.getId());

                        startActivityForResult(intent, 0);

                    }
                }
            };

            cardView.findViewById(R.id.restart).setOnClickListener(clickListener);
            cardView.findViewById(R.id.resume).setOnClickListener(clickListener);
            cardView.findViewById(R.id.start).setOnClickListener(clickListener);
            cardView.findViewById(R.id.history).setOnClickListener(clickListener);

            String progress = Common.getSharedPreferences(cat);
            if (!progress.equals(Common.na)) {
                cardView.findViewById(R.id.starting).setVisibility(GONE);
            } else {
                cardView.findViewById(R.id.progress).setVisibility(GONE);
            }

            if (history.equals(Common.na)) {
                cardView.findViewById(R.id.history).setVisibility(GONE);
                cardView.findViewById(R.id.divider1).setVisibility(GONE);
            }

            TextView textView = cardView.findViewById(R.id.category);
            textView.setText(cat);

            linearLayout.addView(cardView);

            cat_map.put(cat, cardView);
        }

        //Add 'ALL' category card
        final CardView cardView = (CardView) LayoutInflater.from(this).inflate(R.layout.category_card, linearLayout, false);

        final String progress = Common.getSharedPreferences(Common.all);
        final String history = Common.getSharedPreferences("all_history");

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.history) {

                    Intent intent = new Intent(MainScreen.this, Results.class);
                    intent.putExtra(Common.category, Common.all);
                    startActivity(intent);

                } else if (v.getId() == R.id.restart) {

                    Common.removeSharedPreferences(Common.all);
                    Common.removeSharedPreferences(Common.all + Common.score);
                    Common.removeSharedPreferences(Common.all + Common.progress);

                    cardView.findViewById(R.id.starting).setVisibility(View.VISIBLE);
                    cardView.findViewById(R.id.progress).setVisibility(GONE);

                    if (history.equals(Common.na)) {
                        cardView.findViewById(R.id.history).setVisibility(GONE);
                        cardView.findViewById(R.id.divider1).setVisibility(GONE);
                    }

                } else {

                    Intent intent = new Intent(MainScreen.this, TestStart.class);
                    intent.putExtra(Common.category, Common.all);
                    intent.putExtra(Common.action, v.getId());

                    startActivityForResult(intent, 0);

                }
            }
        };

        cardView.findViewById(R.id.restart).setOnClickListener(clickListener);
        cardView.findViewById(R.id.resume).setOnClickListener(clickListener);
        cardView.findViewById(R.id.start).setOnClickListener(clickListener);
        cardView.findViewById(R.id.history).setOnClickListener(clickListener);

        if (!progress.equals(Common.na)) {
            cardView.findViewById(R.id.starting).setVisibility(GONE);
        } else {
            cardView.findViewById(R.id.progress).setVisibility(GONE);
        }
        if (history.equals(Common.na)) {
            cardView.findViewById(R.id.history).setVisibility(GONE);
            cardView.findViewById(R.id.divider1).setVisibility(GONE);
        }

        TextView textView = cardView.findViewById(R.id.category);
        textView.setText("All");

        linearLayout.addView(cardView);

        cat_map.put(Common.all, cardView);

        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prevent double click
                v.setEnabled(false);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
                builder.setCancelable(true);
                builder.setTitle(Common.update);
                builder.setMessage(Common.sure);

                builder.setPositiveButton(Common.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Common.removeAllSharedPreferences();
                        Common.sync(MainScreen.this, getIntent());
                    }
                });

                builder.setNegativeButton(Common.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.create().show();

                v.setEnabled(true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            //Refresh page
            startActivity(getIntent());
        }
    }
}
