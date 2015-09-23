package barqsoft.footballscores;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class ScoresWidget extends AppWidgetProvider {

    private static final String SORT_ORDER = "ASC";

    private static String[] dbCols = new String[]{
            DatabaseContract.scores_table.DATE_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL
    };

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.scores_widget);

        ContentResolver cr = context.getContentResolver();

        // Retrieve the most recent record, which is why we sort by SORT_ORDER (desc)
        Cursor cursor = cr.query(
                DatabaseContract.BASE_CONTENT_URI,
                dbCols,
                null,
                null,
                DatabaseContract.scores_table.DATE_COL + " " + SORT_ORDER
        );

        // Don't bother retrieving the data unless it exists
        if(cursor == null || cursor.getCount() < 1 || !cursor.moveToFirst()) {
            return;
        }

        String date = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.DATE_COL));
        views.setTextViewText(R.id.data_textview, date);

        String homeName = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_COL));
        views.setTextViewText(R.id.home_name, homeName);

        String awayName = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_COL));
        views.setTextViewText(R.id.away_name, awayName);

        String awayScore = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL));
        String homeScore = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL));
        views.setTextViewText(R.id.score_textview, homeScore + " - " + awayScore);

        views.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(homeName));
        views.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(awayName));

        cursor.close();
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
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

