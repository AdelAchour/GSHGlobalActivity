package com.production.achour_ar.gshglobalactivity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.production.achour_ar.gshglobalactivity.R;
import com.production.achour_ar.gshglobalactivity.data_model.TicketModel;

import java.util.ArrayList;

public class TicketResoluAdapter extends ArrayAdapter<TicketModel> implements View.OnClickListener {

    private ArrayList<TicketModel> dataSet;
    Context mContext;


    // View lookup cache
    private class ViewHolder {
        TextView txtName;
        TextView txtDate;
        TextView txtSLA;
        TextView txtTempsRestant;
        ImageView info;
        RelativeLayout layout;

    }


    public TicketResoluAdapter(ArrayList<TicketModel> data, Context context) {
        super(context, R.layout.row_item_ticket, data);
        this.dataSet = data;
        this.mContext = context;
    }


    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        TicketModel TicketModel = (TicketModel) object;

        switch (v.getId()) {
            case R.id.item_info:
                if (!TicketModel.isTicketEnRetard()) {
                    Snackbar
                            .make(v, "Ticket résolu à temps : ", Snackbar.LENGTH_LONG)
                            .setAction("No action", null)
                            .show();
                } else if (TicketModel.isTicketEnRetard()) {
                    Snackbar
                            .make(v, "Ticket résolu en retard ", Snackbar.LENGTH_LONG)

                            .setAction("No action", null)
                            .show();


                    break;
                }
        }
    }


    private int lastPosition = -1;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TicketModel TicketModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final TicketResoluAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new TicketResoluAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_ticket, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.titreTV);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.dateTV);
            viewHolder.txtSLA = (TextView) convertView.findViewById(R.id.slaTV);
            viewHolder.txtTempsRestant = (TextView) convertView.findViewById(R.id.SLARestantTV);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);
            viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.backgroundRow);


            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TicketResoluAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(TicketModel.getTitreTicket());
        viewHolder.txtDate.setText(TicketModel.getDateTicket());
        viewHolder.txtSLA.setText(TicketModel.getSlaTicket());
        viewHolder.txtTempsRestant.setText(TexteClos(TicketModel.isTicketEnRetard()));
        viewHolder.txtTempsRestant.setTextColor(Color.parseColor("#434343"));
        viewHolder.info.setImageResource(R.drawable.ic_check_green_30dp);
        viewHolder.layout.setBackgroundColor(getColorBG(TicketModel.isTicketEnRetard()));
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);

        // Return the completed view to render on screen
        return convertView;
    }

    private String TexteClos(boolean ticketEnRetard) {
        String respose = "";
        if ((ticketEnRetard)) {
            respose = "Résolu en retard";
        } else {
            respose = "Résolu à temps";
        }
        return respose;
    }

    private int getColorBG(boolean ticketEnRetard) {
        int color;

        if (ticketEnRetard) {
            color = Color.parseColor("#3caa0000");
        } else {
            color = Color.parseColor("#ffffff");
        }
        return color;
    }

}