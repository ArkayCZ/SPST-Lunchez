package org.nexussoft.spstlunches;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by vesel on 14.05.2016.
 */
public class BasicWidgetProvider extends AppWidgetProvider {

    private RemoteViews mViews;
    private AppWidgetManager mManager;
    private int[] mWidgetIds;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.i("WIDGET", "Doing stuff with the widget!");

        mViews = new RemoteViews(context.getPackageName(), R.layout.basic_widget);
        mManager = appWidgetManager;
        mWidgetIds = appWidgetIds;

        mViews.setTextViewText(R.id.lunch_1_title, "TITLE!");

        new DownloadTask().execute();
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    private class DownloadTask extends AsyncTask<Void, Void, Void> {

        String[] data;

        @Override
        protected void onPostExecute(Void aVoid) {
            mViews.setTextViewText(R.id.lunch_1_title, data[0].substring(0, 1).toUpperCase() + data[0].substring(1));
            mViews.setTextViewText(R.id.lunch_2_title, data[1].substring(0, 1).toUpperCase() + data[1].substring(1));

            mViews.setTextViewText(R.id.lunch_1_description, data[2]);
            mViews.setTextViewText(R.id.lunch_2_description, data[3]);

            mManager.updateAppWidget(mWidgetIds, mViews);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                data = new DataProvider().getLatest();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
