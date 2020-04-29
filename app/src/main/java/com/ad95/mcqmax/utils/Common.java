package com.ad95.mcqmax.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ad95.mcqmax.Splash;
import com.ad95.mcqmax.database.databaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class Common {
    private static String Url = "https://raw.githubusercontent.com/AD95/MCQMax/master/loadData.php";

    private static Context ctx;
    private static databaseHelper databaseHelper;

    public Common(Context context) {
        ctx = context;
    }

    public static String dbName = "mcq.db";
    public static String mcqTable = "mcq";
    public static String choicesTable = "choices";
    public static String total = "_total";
    public static String progress = "_progress";
    public static String score = "_score";
    public static String history = "_history";
    public static String chosenPercent = "% chosen by others";
    public static String category = "category";
    public static String question = "question";
    public static String labelY = "Score";
    public static String labelX = "Attempts";
    public static String recentScore = "Most Recent Score: ";
    public static String checkInternet = "Check your internet connection and try again";
    public static String cached = "cached";
    public static String question_text = "question_text";
    public static String correct_answer_id = "correct_answer_id";
    public static String id = "id";
    public static String chosen_percentage = "chosen_percentage";
    public static String answer_text = "answer_text";
    public static String question_id = "question_id";
    public static String updating = "Updating";
    public static String update = "Update Question List?";
    public static String sure = "If you refresh the question list, you will lose your current progress. Are you sure?";
    public static String yes = "Yes";
    public static String no = "No";
    public static String action = "action";
    public static String na = "NA";
    public static String all = "all";
    public static String progress_text = "Current Progress: ";


    private static boolean isOnline() {
        if (ctx != null) {
            ConnectivityManager cm =
                    (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        return false;
    }

    public static void sync(final Activity activity, final Intent intent) {
        if (ctx == null)
            return;

        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setMessage(updating);
        progressDialog.setCancelable(false);

        final JSONArray[] obj = {null};

        databaseHelper = com.ad95.mcqmax.database.databaseHelper.getInstance(ctx);

        final Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    if (obj[0] != null) {
                        databaseHelper.dropTables();

                        for (int i = 0; i < obj[0].length(); i++) {

                            JSONObject jsonObject = obj[0].getJSONObject(i);
                            String id = jsonObject.getString(Common.id);
                            String correct_answer_id = jsonObject.getString(Common.correct_answer_id);
                            String question_text = jsonObject.getString(Common.question_text);
                            String category = jsonObject.getString(Common.category);
                            JSONArray answers = jsonObject.getJSONArray("answers");

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(Common.id, id);
                            contentValues.put(Common.correct_answer_id, correct_answer_id);
                            contentValues.put(Common.question_text, question_text);
                            contentValues.put(Common.category, category);

                            databaseHelper.addEntry(contentValues);

                            for (int j = 0; j < answers.length(); j++) {

                                jsonObject = answers.getJSONObject(j);

                                String id1 = jsonObject.getString(Common.id);
                                String chosen_percentage = jsonObject.getString(Common.chosen_percentage);
                                String answer_text = jsonObject.getString(Common.answer_text);

                                contentValues = new ContentValues();
                                contentValues.put(Common.id, id1);
                                contentValues.put(Common.chosen_percentage, chosen_percentage);
                                contentValues.put(Common.answer_text, answer_text);
                                contentValues.put(Common.question_id, id);

                                databaseHelper.addEntry(contentValues);
                            }
                        }
                    }

                    progressDialog.dismiss();

                    if (intent != null) {
                        activity.finish();
                        activity.startActivity(intent);
                    }
                } catch (Exception ignored) {
                }
            }
        };

        if (isOnline()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Common.Url,
                    new Response.Listener() {
                        @Override
                        public void onResponse(Object response) {
                            try {
                                obj[0] = new JSONArray(response.toString());
                                setSharedPreferences(cached, "true");
                                myThread.start();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(activity, checkInternet, Toast.LENGTH_SHORT).show();
                    myThread.start();
                }
            });

            RequestQueue queue = Volley.newRequestQueue(ctx);
            queue.add(stringRequest);
        } else {
            Toast.makeText(activity, checkInternet, Toast.LENGTH_SHORT).show();
            if(activity instanceof Splash)
                activity.finish();
        }
    }

    public static void setSharedPreferences(String key, String value) {
        if (ctx == null)
            return;
        SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = share.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static void removeSharedPreferences(String key) {
        if (ctx == null)
            return;
        SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = share.edit();
        if (share.contains(key)) {
            edit.remove(key);
            edit.apply();
        }
    }

    public static void removeAllSharedPreferences() {
        if (ctx == null)
            return;
        SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = share.edit();
        edit.clear();
        edit.apply();
    }

    public static String getSharedPreferences(String key) {
        String res = Common.na;
        SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(ctx);
        HashMap<String, String> map = (HashMap<String, String>) share.getAll();
        if (map.containsKey(key))
            return map.get(key);
        return res;
    }

}
