package com.ppanero.disease_checker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


/**
 * Created by Light on 14/03/16.
 */
public class DiseaseItemAdapter extends ArrayAdapter<DiseaseItem> {

    Context context;
    int layoutResourceId;
    List<DiseaseItem> data = null;


    public DiseaseItemAdapter(Context context, int layoutResourceId, List<DiseaseItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DiseaseItemHolder holder = null;

        if(row == null)
        {
            //LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new DiseaseItemHolder();
            holder.name_tv = (TextView)row.findViewById(R.id.disease_tv);
            holder.level_tv = (TextView)row.findViewById(R.id.level_tv);
            holder.location_date_tv = (TextView)row.findViewById(R.id.location_date_tv);
            holder.twitter_tv = (TextView)row.findViewById(R.id.twitter_tv);
            holder.news_tv = (TextView)row.findViewById(R.id.news_tv);
            holder.cdc_tv = (TextView)row.findViewById(R.id.cdc_tv);
            holder.twitter_icon = (ImageView)row.findViewById(R.id.twitter_iv);
            holder.news_icon = (ImageView)row.findViewById(R.id.news_iv);
            holder.cdc_icon = (ImageView)row.findViewById(R.id.cdc_iv);

            row.setTag(holder);
        }
        else
        {
            holder = (DiseaseItemHolder)row.getTag();
        }

        DiseaseItem diseaseItem = data.get(position);
        holder.name_tv.setText(diseaseItem.getName());
        holder.level_tv.setText(diseaseItem.getLevel().toString());
        holder.level_tv.setTextColor(getLevelColor(diseaseItem.getLevel()));
        holder.location_date_tv.setText(diseaseItem.getLocationDate());
        holder.twitter_tv.setText(String.valueOf(diseaseItem.getTwitterCount()));
        holder.news_tv.setText(String.valueOf(diseaseItem.getNewsCount()));
        holder.cdc_tv.setText(String.valueOf(diseaseItem.getCdcLevel()));

        return row;
    }

    private int getLevelColor(DiseaseLevel level) {
        switch (level){
            case HIGH:
                return  Color.parseColor("#ff0000");
            case MEDIUM:
                return  Color.parseColor("#ff6600");
            case LOW:
                return Color.parseColor("#ffc61a");
            case INACTIVE:
            case UNDEF:
            default:
                return Color.parseColor("#000000");

        }
    }

    static class DiseaseItemHolder {
        TextView name_tv;
        TextView level_tv;
        TextView location_date_tv;
        TextView twitter_tv;
        TextView news_tv;
        TextView cdc_tv;
        ImageView twitter_icon;
        ImageView news_icon;
        ImageView cdc_icon;
    }
}

