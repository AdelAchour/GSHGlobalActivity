package com.production.achour_ar.gshglobalactivity.ITs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.production.achour_ar.gshglobalactivity.ITs.data_model.ChartModel;
import com.production.achour_ar.gshglobalactivity.R;

import java.util.ArrayList;

public class ChartAdapter extends ArrayAdapter<ChartModel> implements View.OnClickListener {

    private ArrayList<ChartModel> dataSet;
    Context mContext;


    // View lookup cache
    private class ViewHolder {
        TextView txtChartTitle;
        LinearLayout LayoutColorChart;

    }


    public ChartAdapter(ArrayList<ChartModel> data, Context context) {
        super(context, R.layout.row_chart, data);
        this.dataSet = data;
        this.mContext = context;
    }


    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        ChartModel ChartModel = (ChartModel) object;

        switch (v.getId()) {
            //case R.id.item_info:

        }
    }


    private int lastPosition = -1;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ChartModel ChartModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ChartAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ChartAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_chart, parent, false);
            viewHolder.txtChartTitle = (TextView) convertView.findViewById(R.id.chartTitle);
            viewHolder.LayoutColorChart = (LinearLayout) convertView.findViewById(R.id.chartColor);


            result = convertView;
            //viewHolder.startTimer(Long.valueOf(ChartModel.getTempsRestantTicket()));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChartAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtChartTitle.setText(ChartModel.getTitreChart());
        viewHolder.LayoutColorChart.setBackgroundResource(getCartColorFromDrawable(ChartModel.getStatutChart()));

        // Return the completed view to render on screen
        return convertView;
        
    }

    private int getCartColorFromDrawable(String chartColor) {
        switch (chartColor){
            case "Ouvert":
                return R.drawable.chart_shape_ouvert;
            case "Clos":
                return R.drawable.chart_shape_clos;
            case "Retard":
                return R.drawable.chart_shape_enretard;
            case "Attente":
                return R.drawable.chart_shape_attente;
            case "Resolu":
                return R.drawable.chart_shape_resolu;
            case "ClosRetard":
                return R.drawable.chart_shape_clos_retard;
            case "Stat":
                return R.drawable.shape_success;

        }

        return 0;
    }

}