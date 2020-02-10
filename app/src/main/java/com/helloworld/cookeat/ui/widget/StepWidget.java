package com.helloworld.cookeat.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

import com.helloworld.cookeat.R;

/**
 * Implementation of App Widget functionality.
 */
public class StepWidget extends AppWidgetProvider {

    public static String description = "";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        updateWidget(context, appWidgetManager, description);
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, String message) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.step_widget);
        description = message;
        rv.setTextViewText(R.id.stepDetailWidget, message);
        appWidgetManager.updateAppWidget(new ComponentName(context.getPackageName(), StepWidget.class.getName()), rv);
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

