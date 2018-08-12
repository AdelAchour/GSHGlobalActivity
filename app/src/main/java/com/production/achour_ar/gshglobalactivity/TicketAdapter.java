package com.production.achour_ar.gshglobalactivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.production.achour_ar.gshglobalactivity.DataModel.TicketModel;

public class TicketAdapter extends ArrayAdapter<TicketModel> implements View.OnClickListener{

    private ArrayList<TicketModel> dataSet;
    Context mContext;
    static long timeLeftMS = Long.valueOf(TicketModel.getTempsRestantTicket());



    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
        static TextView txtTempsRestant;
        TextView txtDate;
        TextView txtSLA;
        ImageView info;
        RelativeLayout layout;

        Handler handlerTic;

    }


    public TicketAdapter(ArrayList<TicketModel> data, Context context) {
        super(context, R.layout.row_item_ticket, data);
        this.dataSet = data;
        this.mContext=context;
        //startUpdateTimer();
    }

    /*private void startUpdateTimer() {
        countDownTimer = new CountDownTimer(timeLeftMS, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftMS = l;
                handlerTic.sendEmptyMessage(0);
            }

            @Override
            public void onFinish() {
                //handlerFinish.sendEmptyMessage(0);
            }
        }.start();
    }*/



    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        TicketModel TicketModel=(TicketModel)object;

        switch (v.getId())
        {
            case R.id.item_info:

                Snackbar.make(v, "is Late? : " +TicketModel.isTicketEnRetard(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
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
        System.out.println("I'm in getView ndor !");
        System.out.println("Time from = "+timeLeftMS);
        startTimer();



        viewHolder.txtName.setText(TicketModel.getTitreTicket());
        viewHolder.txtDate.setText(TicketModel.getDateTicket());
        viewHolder.txtSLA.setText(TicketModel.getSlaTicket());
        //viewHolder.txtTempsRestant.setText(TicketModel.getTempsRestantTicket());
        viewHolder.info.setImageResource(getIconUrgence(TicketModel.getUrgenceTicket()));
        viewHolder.layout.setBackgroundColor(getColorBG(TicketModel.isTicketEnRetard()));
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);



        // Return the completed view to render on screen
        return convertView;
    }

    private void startTimer() {
        System.out.println("I'm in startTimer !");

        CountDownTimer countDownTimer = new CountDownTimer(timeLeftMS, 1000) {
            @Override
            public void onTick(long l) {
                System.out.println("I'm in TIC !");
                timeLeftMS = l;
                handlerTic.sendEmptyMessage(0);
                //System.out.println("restant : "+timeLeftMS);
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }


    Handler handlerTic = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            System.out.println("handler");

            int hour = (int) ((timeLeftMS / (1000*60*60)) % 24);
            int minute = (int) ((timeLeftMS / (60000)) % 60);
            int seconde = (int)timeLeftMS % 60000 / 1000;

            String timeLeftText = "";

            if (hour<10) timeLeftText += "0";
            timeLeftText += hour;
            timeLeftText += ":";
            if (minute<10) timeLeftText += "0";
            timeLeftText += minute;
            timeLeftText += ":";
            if (seconde<10) timeLeftText += "0";
            timeLeftText += seconde;

            //txtTempsRestant.setText(timeLeftText);
            System.out.println(timeLeftText);

            //timeRemaining.setText("Hey done!");
        }
    };



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



    private int getIconUrgence(String urgenceTicket) {
        int icon;

        if((urgenceTicket.equals("Très basse"))||(urgenceTicket.equals("Basse"))){
            icon = R.drawable.basse;
        }
        else if((urgenceTicket.equals("Haute"))||(urgenceTicket.equals("Très haute"))){
            icon = R.drawable.haute;
        }
        else {
            icon = R.drawable.moyenne;
        }

        return icon;
    }


}
