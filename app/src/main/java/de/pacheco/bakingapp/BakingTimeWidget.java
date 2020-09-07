package de.pacheco.bakingapp;

import de.pacheco.bakingapp.activities.RecipeListActivity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class BakingTimeWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String text) {
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        text = text == null ? widgetText.toString() : text;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_time_widget);
        views.setTextViewText(R.id.appwidget_text, text);
        Intent configIntent = new Intent(context, RecipeListActivity.class);
        configIntent.putExtra("widgetId", appWidgetId);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_text, configPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds =
                appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));
        String text = intent.getStringExtra("howto");
        int widgetId = intent.getIntExtra("widgetId", -1);
        if (widgetId == -1) {
            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, text);
            }
        } else {
            updateAppWidget(context, appWidgetManager, widgetId, text);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, "");
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

