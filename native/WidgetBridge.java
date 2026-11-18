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
