package org.nexussoft.spstlunches;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by vesel on 14.05.2016.
 */
public class BasicWidgetProvider extends AppWidgetProvider {

    private RemoteViews mViews;
    private AppWidgetManager mManager;
    private int[] mWidgetIds;
    private Context mContext;

    private boolean mClickUpdate = false;
    private boolean mShouldUpdateData = true;

    public static String UPDATE_ACTION = "org.nexussoft.spstlunches.BasicWidgetProvider.UPDATE_ACTION";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.i("BasicWidget", "Updating widget...");

        mViews = new RemoteViews(context.getPackageName(), R.layout.basic_widget);
        mManager = appWidgetManager;
        mWidgetIds = appWidgetIds;
        mContext = context;

        mViews.setOnClickPendingIntent(R.id.refresh_button, getPendingSelfIntent(context, UPDATE_ACTION));

        if(mShouldUpdateData) {
            mShouldUpdateData = false;
            Log.i("BasicWidget", "Starting data download task.");
            new DownloadTask().execute();
        } else {
            Log.i("BasicWidget", "Data update skipped.");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.i("BasicWidget", "Intent action: " + intent.getAction());
        mContext = context;
        if(intent.getAction().equals(UPDATE_ACTION)) {
            mClickUpdate = true;
            mShouldUpdateData = true;

            Toast.makeText(mContext, R.string.updating, Toast.LENGTH_SHORT).show();

            update(context);
        }
    }

    public PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass()).setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void update(Context context) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        ComponentName widget = new ComponentName(context.getPackageName(), BasicWidgetProvider.class.getName());
        int[] widgetIds = widgetManager.getAppWidgetIds(widget);

        onUpdate(context, widgetManager, widgetIds);
    }

    private class DownloadTask extends AsyncTask<Void, Void, Void> {

        private String[] data;
        private boolean mSuccess;

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!mSuccess) {
                if (mClickUpdate) {
                    Toast.makeText(mContext, R.string.widget_update_failed, Toast.LENGTH_SHORT).show();
                    mClickUpdate = false;
                    update(mContext);
                } else {
                    if(mManager != null)
                        mManager.updateAppWidget(mWidgetIds, mViews);
                }

                Log.i("BasicWidget", "The data upload has failed!");
                return;
            }

            if (!mClickUpdate) {
                mViews.setTextViewText(R.id.lunch_1_title, data[0].substring(0, 1).toUpperCase() + data[0].substring(1));
                mViews.setTextViewText(R.id.lunch_2_title, data[1].substring(0, 1).toUpperCase() + data[1].substring(1));

                mViews.setTextViewText(R.id.lunch_1_description, data[2]);
                mViews.setTextViewText(R.id.lunch_2_description, data[2]);

                mManager.updateAppWidget(mWidgetIds, mViews);
            } else {
                RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.basic_widget);
                ComponentName widget = new ComponentName(mContext, BasicWidgetProvider.class);

                remoteViews.setTextViewText(R.id.lunch_1_title, data[0].substring(0, 1).toUpperCase() + data[0].substring(1));
                remoteViews.setTextViewText(R.id.lunch_2_title, data[1].substring(0, 1).toUpperCase() + data[1].substring(1));

                remoteViews.setTextViewText(R.id.lunch_1_description, data[2]);
                remoteViews.setTextViewText(R.id.lunch_2_description, data[2]);

                Toast.makeText(mContext, R.string.widget_update_successful, Toast.LENGTH_SHORT).show();
                mClickUpdate = false;
                AppWidgetManager.getInstance(mContext).updateAppWidget(widget, remoteViews);
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("BasicWidget", "Trying to download new data...");
            try {
                data = new DataProvider().getLatest();
                mSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                mSuccess = false;
            }

            return null;
        }
    }
}
