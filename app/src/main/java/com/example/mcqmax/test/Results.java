package com.example.mcqmax.test;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mcqmax.R;
import com.example.mcqmax.utils.Common;

import java.util.ArrayList;
import java.util.Objects;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class Results extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_results);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.colorPrimary));
        }

        String cat = getIntent().getStringExtra(Common.category);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(cat);

        new Common(getApplicationContext());

        String history = Common.getSharedPreferences(cat + Common.history);

        int total = Integer.parseInt(Common.getSharedPreferences(cat + Common.total));

        history = "0," + history;
        String[] s3 = history.split(",");
        String score = Common.recentScore + s3[s3.length - 1] + "/" + total;
        ((TextView) findViewById(R.id.score)).setText(score);

        //Draw chart
        LineChartView lineChartView = findViewById(R.id.chart);
        String[] axisData = new String[s3.length];
        int[] yAxisData = new int[s3.length];
        ArrayList<PointValue> yAxisValues = new ArrayList<>();
        ArrayList<AxisValue> axisValues = new ArrayList<>();

        Line line = new Line(yAxisValues).setColor(getResources().getColor(R.color.yAxis));

        for (int i = 0; i < s3.length; i++) {
            axisData[i] = String.valueOf(i);
            yAxisData[i] = Integer.parseInt(s3[i]);
        }
        for (int i = 0; i < axisData.length; i++) {
            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
        }

        for (int i = 0; i < yAxisData.length; i++) {
            yAxisValues.add(new PointValue(i, yAxisData[i]));
        }

        ArrayList<Line> lines = new ArrayList<>();
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        lineChartView.setLineChartData(data);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        data.setAxisXBottom(axis);
        Axis yAxis = new Axis();
        data.setAxisYLeft(yAxis);
        axis.setTextSize(16);
        axis.setName(Common.labelX);
        axis.setTextColor(getResources().getColor(R.color.axis));
        yAxis.setTextColor(getResources().getColor(R.color.axis));
        yAxis.setTextSize(16);
        yAxis.setName(Common.labelY);
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top = total;
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
