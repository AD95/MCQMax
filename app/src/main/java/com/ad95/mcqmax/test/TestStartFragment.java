package com.ad95.mcqmax.test;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ad95.mcqmax.R;
import com.ad95.mcqmax.utils.Common;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class TestStartFragment extends Fragment {


    private String selected;                            //answer selected
    private LinearLayout choices_view;
    private FloatingActionButton floatingActionButton;  //next question button

    static TestStartFragment newInstance() {
        return new TestStartFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.test_start_fragment, container, false);

        ArrayList<String> mcq = (ArrayList<String>) getArguments().getSerializable(Common.question);
        Map<String, ArrayList<String>> choices = (Map<String, ArrayList<String>>) getArguments().getSerializable(Common.choicesTable);

        TextView question = rootView.findViewById(R.id.question);
        choices_view = rootView.findViewById(R.id.choices);
        floatingActionButton = rootView.findViewById(R.id.fab);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionButton.hide();
                ((TestStart) Objects.requireNonNull(getActivity())).putFragment();
            }
        });

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selected = String.valueOf(buttonView.getId());
                showProgress();
                floatingActionButton.show();
            }
        };

        for (String s3 : Objects.requireNonNull(choices).keySet()) {

            View choice = LayoutInflater.from(getActivity()).inflate(R.layout.choice, choices_view, false);

            RadioButton radioButton = choice.findViewById(R.id.radioButton);
            radioButton.setText(Html.fromHtml(Objects.requireNonNull(choices.get(s3)).get(1)).toString());
            radioButton.setId(Integer.parseInt(s3));

            int chosen = (int) (Double.parseDouble(Objects.requireNonNull(choices.get(s3)).get(0)) * 1000);
            int chosen1 = chosen / 10;
            String percent = chosen1 + Common.chosenPercent;

            ((TextView) choice.findViewById(R.id.chosen)).setText(percent);

            ((ProgressBar) choice.findViewById(R.id.progressBar)).setProgress(chosen);

            radioButton.setOnCheckedChangeListener(checkedChangeListener);

            choices_view.addView(choice);
        }

        String question_text = Html.fromHtml(Objects.requireNonNull(mcq).get(1)).toString().trim();
        String[] lastIndexOf = question_text.split("\n");
        String ques = lastIndexOf[lastIndexOf.length - 1];
        int len = ques.length();
        Log.d("question_text", question_text);
        Log.d("question_text.length()", question_text.length()+"");
        Log.d("len", len+"");
        Log.d("lastIndexOf.length", lastIndexOf.length+"");
        question.setText(question_text.substring(0, question_text.length() - len ).trim());
        ((TextView) rootView.findViewById(R.id.question2)).setText(ques.trim());

        return rootView;
    }

    private void showProgress() {

        ((TestStart) Objects.requireNonNull(getActivity())).updateScore(selected);

        for (int i = 0; i < choices_view.getChildCount(); i++) {

            ProgressBar progressBar = choices_view.getChildAt(i).findViewById(R.id.progressBar);
            //Show progressbar and text layout
            choices_view.getChildAt(i).findViewById(R.id.parent).setVisibility(View.VISIBLE);
            //Animate progressbar
            ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, progressBar.getProgress());
            progressAnimator.setDuration(3000);
            progressAnimator.setInterpolator(new LinearInterpolator());
            progressAnimator.start();
            //Disable other radio buttons
            ((LinearLayout) choices_view.getChildAt(i)).getChildAt(0).setClickable(false);

        }

    }

}
