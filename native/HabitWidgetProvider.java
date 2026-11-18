package com.gor.habittracker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import org.json.JSONArray;
import org.json.JSONObject;

public class HabitWidgetProvider extends AppWidgetProvider {
    private static final int[] ROWS = {
        R.id.w_r1, R.id.w_r2, R.id.w_r3, R.id.w_r4, R.id.w_r5, R.id.w_r6
    };

    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        String raw = ctx.getSharedPreferences("habit_widget", Context.MODE_PRIVATE).getString("data", null);
        for (int id : ids) {
            RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.widget_habits);
            String title = "Habit Tracker";
            int shown = 0;
            try {
                if (raw != null) {
                    JSONObject o = new JSONObject(raw);
                    title = o.optString("title", title);
                    JSONArray rows = o.optJSONArray("rows");
                    if (rows != null) {
                        for (int i = 0; i < rows.length() && i < ROWS.length; i++) {
                            JSONObject r = rows.getJSONObject(i);
                            boolean done = r.optBoolean("d");
                            v.setTextViewText(ROWS[i], (done ? "✓  " : "○  ") + r.optString("t"));
                            v.setTextColor(ROWS[i], done ? 0xFF4ADE80 : 0xFFFFFFFF);
                            v.setViewVisibility(ROWS[i], View.VISIBLE);
                            shown++;
                        }
                    }
                }
            } catch (Exception e) {
                // keep defaults
            }
            for (int i = shown; i < ROWS.length; i++) {
                v.setViewVisibility(ROWS[i], View.GONE);
            }
            v.setTextViewText(R.id.w_title, title);
            Intent launch = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());
            if (launch != null) {
                PendingIntent pi = PendingIntent.getActivity(ctx, 0, launch,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                v.setOnClickPendingIntent(R.id.w_root, pi);
            }
            mgr.updateAppWidget(id, v);
        }
    }
}
