package com.production.achour_ar.gshglobalactivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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


    // View lookup cache
    private class ViewHolder {
        TextView txtName;
        TextView txtType;
        TextView txtTempsRestant;
        TextView txtDate;
        TextView txtSLA;
        ImageView info;
        RelativeLayout layout;
        boolean isSLAlate = false;

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                System.out.println("handler");

                Bundle bundle = msg.getData();
                long timeLeftMS = bundle.getLong("time");

                int day = (int) ((timeLeftMS / (24*3600000)));
                int hour = (int) ((timeLeftMS / (1000*60*60)) % 24);
                int minute = (int) ((timeLeftMS / (60000)) % 60);
                int seconde = (int)timeLeftMS % 60000 / 1000;

                String timeLeftText = "";

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

                txtTempsRestant.setText(timeLeftText);

                if (timeLeftMS <= getMSfromTime("00:10:00")){
                    txtTempsRestant.setTextColor(Color.parseColor("#ca1f1f"));
                }

            }
        };

        Handler handlerLate = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                txtTempsRestant.setText("En retard");
                txtTempsRestant.setTextColor(Color.parseColor("#434343"));
                layout.setBackgroundColor(Color.parseColor("#3caa0000"));
            }
        };

        public void startTimer(long timeLeftMS) {
            if (timeLeftMS<0){
                handlerLate.sendEmptyMessage(0);
            }
            else{
                CountDownTimer countDownTimer = new CountDownTimer(timeLeftMS, 1000) {

                    @Override
                    public void onTick(long l) {
                        Bundle bundle = new Bundle();
                        bundle.putLong("time", l);
                        Message message = new Message();
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFinish() {
                        handlerLate.sendEmptyMessage(0);
                    }
                }.start();
            }
        }

        //statut
        public void isEnAttente(String getTicketStatut) {

            if (getTicketStatut)

            }

    }

    private long getMSfromTime(String timeLeftText) {
        long timeMS = 0;

        //"00:01:21"
        String hours = timeLeftText.substring(0, 2);
        String minutes = timeLeftText.substring(3, 5);
        String seconds = timeLeftText.substring(6, 8);

        //System.out.println("hours = "+hours+" | minutes = "+minutes+" | seconds = "+seconds);

        long hoursMS = 3600000*Long.valueOf(hours);
        long minutesMS = 60000*Long.valueOf(minutes);
        long secondsMS = 1000*Long.parseLong(seconds);
        timeMS = hoursMS + minutesMS + secondsMS;

        return timeMS;
    }


    public TicketAdapter(ArrayList<TicketModel> data, Context context) {
        super(context, R.layout.row_item_ticket, data);
        this.dataSet = data;
        this.mContext=context;
        //startUpdateTimer();
    }


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
            viewHolder.startTimer(Long.valueOf(TicketModel.getTempsRestantTicket()));
            viewHolder.isEnAttente(TicketModel.getStatutTicket());
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
        //viewHolder.txtTempsRestant.setText(TicketModel.getTempsRestantTicket());
        viewHolder.info.setImageResource(getIconUrgence(TicketModel.getUrgenceTicket()));
        viewHolder.layout.setBackgroundColor(getColorBG(TicketModel.isTicketEnRetard()));
        /*if (TicketModel.isTicketEnRetard()){
            viewHolder.txtTempsRestant.setText("En retard !");
        }*/
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);

        System.out.println("Here : "+TicketModel.getTitreTicket()); //getting each item's name
        System.out.println("Time = "+TicketModel.getTempsRestantTicket()); //getting each item's time left and it's correct
        System.out.println("Rebours = "+calculRebours(Long.valueOf(TicketModel.getTempsRestantTicket())));

        // Return the completed view to render on screen
        return convertView;
    }

    private String calculRebours(long timeLeftMS) {
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

        return timeLeftText;
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