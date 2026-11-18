package com.gor.habittracker;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "WidgetBridge")
public class WidgetBridge extends Plugin {
    @PluginMethod
    public void addEvent(PluginCall call) {
        String title = call.getString("title", "Habit Tracker");
        int hour = call.getInt("hour", 9);
        int minute = call.getInt("minute", 0);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
        cal.set(java.util.Calendar.MINUTE, minute);
        cal.set(java.util.Calendar.SECOND, 0);
        if (cal.getTimeInMillis() < System.currentTimeMillis()) cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
        long start = cal.getTimeInMillis();
        Intent i = new Intent(Intent.ACTION_INSERT);
        i.setData(android.provider.CalendarContract.Events.CONTENT_URI);
        i.putExtra(android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME, start);
        i.putExtra(android.provider.CalendarContract.EXTRA_EVENT_END_TIME, start + 15 * 60 * 1000);
        i.putExtra(android.provider.CalendarContract.Events.TITLE, title);
        i.putExtra(android.provider.CalendarContract.Events.RRULE, "FREQ=DAILY");
        i.putExtra(android.provider.CalendarContract.Events.HAS_ALARM, 1);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
        call.resolve();
    }

    @PluginMethod
    public void update(PluginCall call) {
        String json = call.getString("json", "{}");
        Context c = getContext();
        c.getSharedPreferences("habit_widget", Context.MODE_PRIVATE).edit().putString("data", json).apply();
        AppWidgetManager m = AppWidgetManager.getInstance(c);
        int[] ids = m.getAppWidgetIds(new ComponentName(c, HabitWidgetProvider.class));
        Intent i = new Intent(c, HabitWidgetProvider.class);
        i.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        c.sendBroadcast(i);
        call.resolve();
    }
}
