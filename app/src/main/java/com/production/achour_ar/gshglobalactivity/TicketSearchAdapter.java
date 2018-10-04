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

public class TicketSearchAdapter extends ArrayAdapter<TicketSearchModel> implements View.OnClickListener{

    private ArrayList<TicketSearchModel> dataSet;
    Context mContext;

    // View lookup cache
    private class ViewHolder {
        TextView txtName;
        TextView txtDate;
        TextView txtStatut;
    }

    public TicketSearchAdapter(ArrayList<TicketSearchModel> data, Context context) {
        super(context, R.layout.row_item_ticket_search, data);
        this.dataSet = data;
        this.mContext = context;
    }


    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        TicketSearchModel TicketSearchModel = (TicketSearchModel)object;

        switch (v.getId())
        {
           /* case R.id.item_info:
                
                break;*/
        }
    }

    private int lastPosition = -1;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TicketSearchModel TicketSearchModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final TicketSearchAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new TicketSearchAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_ticket_search, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.titreTicketSearchTV);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.dateTicketSearchTV);
            viewHolder.txtStatut = (TextView) convertView.findViewById(R.id.statutTicketSearchTV);


            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TicketSearchAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(TicketSearchModel.getTitreTicket());
        viewHolder.txtDate.setText(TicketSearchModel.getDateTicket());
        viewHolder.txtStatut.setText(etatText(TicketSearchModel.getStatutTicket()));

        // Return the completed view to render on screen
        return convertView;
    }

    private String etatText(String etatTicket) {
        String etat = "";
        int et = Integer.valueOf(etatTicket);
        switch (et){
            case 1:
                etat = "Nouveau";
                break;
            case 2:
                etat = "En cours";
                break;
            case 3:
                etat = "En cours (Planifié)";
                break;
            case 4:
                etat = "En attente";
                break;
            case 5:
                etat = "Résolu";
                break;
            case 6:
                etat = "Clos";
                break;
        }

        return etat;
    }

}