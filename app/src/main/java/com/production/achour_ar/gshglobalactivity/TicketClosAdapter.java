package com.production.achour_ar.gshglobalactivity;

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
import java.util.ArrayList;


import com.production.achour_ar.gshglobalactivity.data_model.TicketModel;

public class TicketClosAdapter extends ArrayAdapter<TicketModel> implements View.OnClickListener{

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

    public TicketClosAdapter(ArrayList<TicketModel> data, Context context) {
        super(context, R.layout.row_item_ticket, data);
        this.dataSet = data;
        this.mContext=context;
    }


    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        TicketModel TicketModel=(TicketModel)object;

        switch (v.getId())
        {
            case R.id.item_info:
                if(!TicketModel.isTicketEnRetard()){
                    //String retardtime = TransformInTime(TicketModel.getTempsRetard());
                    //String resolutiontime = TransformInTime(TicketModel.getTempsResolution());
                    Snackbar
                            //.make(v, "Temps de résolution : " +resolutiontime, Snackbar.LENGTH_LONG)
                            .make(v, "Clos à temps", Snackbar.LENGTH_LONG)
                            .setAction("No action", null)
                            .show();
                }
                else if (TicketModel.isTicketEnRetard()){
                    //String retardtime = TransformInTime(TicketModel.getTempsRetard());
                    //String resolutiontime = TransformInTime(TicketModel.getTempsResolution());

                    Snackbar
                            //.make(v, "Temps de résolution : " +resolutiontime
                            //+"\nRetard de : "+retardtime, Snackbar.LENGTH_LONG)
                            .make(v, "Clos en retard", Snackbar.LENGTH_LONG)
                            .setAction("No action", null)
                            .show();
                }

                break;
        }
    }

    private String TransformInTime(String tempsMS) {
        long timeLeftMS = Long.valueOf(tempsMS);
        //int months = (int) ((timeLeftMS / (30*24*60*60*1000)));
        int day = (int) ((timeLeftMS / (24*60*60*1000))); //%30
        int hour = (int) ((timeLeftMS / (1000*60*60)) % 24);
        int minute = (int) ((timeLeftMS / (60*1000)) % 60);
        int seconde = (int)timeLeftMS % 60000 / 1000;

        String timeLeftText = "";

        //if (months<10) timeLeftText += "0";
        //timeLeftText += months;
        //timeLeftText += ":";
        if (day<10) timeLeftText += "0";
        timeLeftText += day;
        timeLeftText += ":";
        if (hour<10) timeLeftText += "0";
        timeLeftText += hour;
        timeLeftText += ":";
        if (minute<10) timeLeftText += "0";
        timeLeftText += minute;
        timeLeftText += ":";
        if (seconde<10) timeLeftText += "0";
        timeLeftText += seconde;



        return timeLeftText;
    }

    private int lastPosition = -1;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TicketModel TicketModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_ticket, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.titreTV);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.dateTV);
            viewHolder.txtSLA = (TextView) convertView.findViewById(R.id.slaTV);
            viewHolder.txtTempsRestant = (TextView) convertView.findViewById(R.id.SLARestantTV);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);
            viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.backgroundRow);


            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
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
        if ((ticketEnRetard)){
            respose = "Clos en retard";
        }
        else{
            respose = "Clos à temps";
        }
        return respose;
    }


    private int getColorBG(boolean ticketEnRetard) {
        int color;

        if (ticketEnRetard){
            color = Color.parseColor("#3caa0000");
        }
        else{
            color = Color.parseColor("#ffffff");
        }
        return color;
    }



}