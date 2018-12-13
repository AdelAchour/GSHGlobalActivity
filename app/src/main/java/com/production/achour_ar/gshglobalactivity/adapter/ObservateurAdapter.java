package com.production.achour_ar.gshglobalactivity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.production.achour_ar.gshglobalactivity.data_model.ObservateurModel;
import com.production.achour_ar.gshglobalactivity.R;

import java.util.ArrayList;

public class ObservateurAdapter extends ArrayAdapter<ObservateurModel> {

    private ArrayList<ObservateurModel> dataSet;
    Context mContext;


    // View lookup cache
    private class ViewHolder {
        TextView txtNom;
    }


    public ObservateurAdapter(ArrayList<ObservateurModel> data, Context context) {
        super(context, R.layout.row_item_observateur, data);
        this.dataSet = data;
        this.mContext = context;
        //startUpdateTimer();
    }


    private int lastPosition = -1;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ObservateurModel model = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_observateur, parent, false);
            viewHolder.txtNom = (TextView) convertView.findViewById(R.id.name);


            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        lastPosition = position;

        viewHolder.txtNom.setText(model.getPrenomObs()+" "+model.getNomObs());


        // Return the completed view to render on screen
        return convertView;
    }

}