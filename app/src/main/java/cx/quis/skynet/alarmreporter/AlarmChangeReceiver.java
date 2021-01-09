package cx.quis.skynet.alarmreporter;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class AlarmChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String url = sharedPreferences.getString("report_url", "");
        if (url.isEmpty()) {
            Log.i(TAG, "No URL configured to report next alarm to.");
            return;
        }
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo next = manager.getNextAlarmClock();
        long timestamp = 0;
        if (next != null) {
            timestamp = next.getTriggerTime();
        }
        Log.i(TAG, "Sending request to "+ url + " about the next alarm being at "+ timestamp);
        report(context, url, timestamp);
    }

    private void report(Context context, String url, long timestamp) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Alarm Reporter: " + response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = error.getMessage();
                if (error.networkResponse != null) {
                    message = "HTTP " + error.networkResponse.statusCode;
                }
                Toast.makeText(context, "Alarm Reporter error: " + message, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("timestamp", String.valueOf(timestamp));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);
    }
}