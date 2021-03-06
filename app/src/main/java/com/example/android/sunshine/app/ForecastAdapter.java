package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.R;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {


    public static final int VIEW_TYPE_TODAY = 0;
    public static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    private boolean mUseTodayLayout = true;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void setUseTodayLayout(boolean useTodayLayout){
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    /**
     * Copy/paste note: Replace existing newView() method in ForecastAdapter with this one.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        // Determine layoutId from viewType
        int layoutId = -1;
        switch (viewType){
            case VIEW_TYPE_TODAY:
                layoutId = R.layout.list_item_forecast_today;
                break;
            case VIEW_TYPE_FUTURE_DAY:
                layoutId = R.layout.list_item_forecast;
                break;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ForecastViewHolder forecastViewHolder = new ForecastViewHolder(view);
        view.setTag(forecastViewHolder);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        ForecastViewHolder forecastViewHolder = (ForecastViewHolder) view.getTag();
        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        // Use placeholder image for now
        ImageView iconView = forecastViewHolder.iconView;
        switch (getItemViewType(cursor.getPosition())){
            case VIEW_TYPE_TODAY:
                iconView.setImageDrawable(context.getResources()
                        .getDrawable(Utility.getArtResourceForWeatherCondition(weatherId)));
                break;
            case VIEW_TYPE_FUTURE_DAY:
                iconView.setImageDrawable(context.getResources()
                        .getDrawable(Utility.getIconResourceForWeatherCondition(weatherId)));
                break;
        }

        // Read date from cursor
        TextView dateView = forecastViewHolder.dateView;
        long dateInMillis = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        dateView.setText(Utility.getFriendlyDayString(context, dateInMillis));
        // Read weather forecast from cursor
        TextView forecastView = forecastViewHolder.descriptionView;
        String forecast = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        forecastView.setText(forecast);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        TextView highView = forecastViewHolder.highTempView;
        highView.setText(Utility.formatTemperature(context, high, isMetric));

        // Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        TextView lowView = forecastViewHolder.lowTempView;
        lowView.setText(Utility.formatTemperature(context, low, isMetric));

    }


}