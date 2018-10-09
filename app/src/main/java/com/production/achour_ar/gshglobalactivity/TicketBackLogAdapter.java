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

import com.production.achour_ar.gshglobalactivity.DataModel.TicketModel;

import java.util.ArrayList;

public class TicketBackLogAdapter extends ArrayAdapter<TicketModel> implements View.OnClickListener{

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



    public TicketBackLogAdapter(ArrayList<TicketModel> data, Context context) {
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

                Snackbar
                        .make(v, "Faut nettoyer mon grand", Snackbar.LENGTH_LONG)
                        .setAction("No action", null)
                        .show();

                break;
        }
    }


    private int lastPosition = -1;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TicketModel TicketModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final TicketBackLogAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new TicketBackLogAdapter.ViewHolder();
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
            viewHolder = (TicketBackLogAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(TicketModel.getTitreTicket());
        viewHolder.txtDate.setText(TicketModel.getDateTicket());
        viewHolder.txtSLA.setText(TicketModel.getSlaTicket());
        viewHolder.txtTempsRestant.setText(getTitleFromStatut(TicketModel.getStatut()));
        viewHolder.txtTempsRestant.setTextColor(Color.parseColor("#434343"));
        viewHolder.info.setImageResource(getIcon(TicketModel.getStatut()));
        viewHolder.layout.setBackgroundColor(getColorBG(TicketModel.getStatut()));
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);

        // Return the completed view to render on screen
        return convertView;
    }

    private int getIcon(String statut) {
        if (statut.equals("2")){
            return R.drawable.ic_priority_high_red_30dp;
        }
        else if (statut.equals("4")){
            return R.drawable.ic_more_horiz_black_30dp;
        }

        return 0;
    }

    private int getColorBG(String statut) {
        if (statut.equals("2")){
            return Color.parseColor("#3caa0000");
        }
        else if (statut.equals("4")){
            return Color.parseColor("#62dea968");
        }
        return 0;
    }


    private String getTitleFromStatut(String statut) {
        if (statut.equals("2")){
            return "En retard";
        }
        else if (statut.equals("4")){
            return "En attente...";
        }
        return null;
    }


}