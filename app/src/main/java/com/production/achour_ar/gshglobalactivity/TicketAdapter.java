package com.production.achour_ar.gshglobalactivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
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
import java.util.Random;

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
        boolean notification1 = false;
        boolean notification2 = false;
        int cpt = 0;

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //Log.d("I'm here ",String.valueOf(System.currentTimeMillis()));

                Bundle bundle = msg.getData();
                long timeLeftMS = bundle.getLong("time");
                String Nom = bundle.getString("name");
                String idTicket = bundle.getString("id");

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

                String timeNotif2Jours = "02:00:00:00";
                String timeNotif1Jour = "01:00:00:00";
                String timeNotif2Heures = "00:02:00:00";
                String timeNotif1Heure = "00:01:00:00";
                String timeNotif30Min = "00:00:30:00";
                String timeNotif10Min = "00:00:10:00";

                if (timeLeftText.equals(timeNotif2Jours)){
                    int idNotif = Integer.valueOf(idTicket) + 000000001 ;
                    Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),
                            R.mipmap.moyennepriorite);

                    NotifyUser("Urgence moyenne", ""+Nom+" : Il vous reste 2 jours avant l'expiration du ticket", bitmap, idNotif);

                }

                if (timeLeftText.equals(timeNotif1Jour)){
                    Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),
                            R.mipmap.moyennepriorite);
                    int idNotif = Integer.valueOf(idTicket) + 000000002 ;

                    NotifyUser("Urgence moyenne", ""+Nom+" : Il vous reste 1 journée avant l'expiration du ticket", bitmap, idNotif);

                }

                if (timeLeftText.equals(timeNotif2Heures)){
                    Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),
                            R.mipmap.hautepriorite);
                    int idNotif = Integer.valueOf(idTicket) + 000000003 ;

                    NotifyUser("Urgence haute", ""+Nom+" : Il ne vous reste plus que 2 heures avant l'expiration du ticket", bitmap, idNotif);

                }

                if (timeLeftText.equals(timeNotif1Heure)){
                    Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),
                            R.mipmap.fire);
                    int idNotif = Integer.valueOf(idTicket) + 000000004 ;

                    txtTempsRestant.setTextColor(Color.parseColor("#ca1f1f"));
                    NotifyUser("Urgence haute", ""+Nom+" : Il ne vous reste plus qu'1 heure avant l'expiration du ticket", bitmap, idNotif);

                }

                if (timeLeftText.equals(timeNotif30Min)){
                    Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),
                            R.mipmap.timefinal);
                    int idNotif = Integer.valueOf(idTicket) + 000000005 ;

                    NotifyUser("Urgence haute", ""+Nom+" : Il ne vous reste plus que 30 minutes avant l'expiration du ticket", bitmap, idNotif);

                }

                if (timeLeftText.equals(timeNotif10Min)){
                    Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),
                            R.mipmap.timefinal);
                    int idNotif = Integer.valueOf(idTicket) + 000000006 ;

                    NotifyUser("Urgence haute", ""+Nom+" : Il ne vous reste plus que 10 minutes avant l'expiration du ticket", bitmap, idNotif);

                }


                System.out.println("Titre "+Nom+ " | "+timeLeftMS+ " | "+txtTempsRestant.getText() );


                info.setImageResource(getIconUrgence(timeLeftMS));


            }
        };

        Handler handlerLate = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                txtTempsRestant.setText("En retard");
                txtTempsRestant.setTextColor(Color.parseColor("#434343"));
                layout.setBackgroundColor(Color.parseColor("#3caa0000"));
                info.setImageResource(R.drawable.haute);
            }
        };

        Handler handlerFinishLate = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String Nom = bundle.getString("name");
                String idTicket = bundle.getString("id");

                txtTempsRestant.setText("En retard");
                txtTempsRestant.setTextColor(Color.parseColor("#434343"));
                layout.setBackgroundColor(Color.parseColor("#3caa0000"));
                info.setImageResource(R.drawable.haute);

                Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),
                        R.mipmap.timeexpire);
                int idNotif = Integer.valueOf(idTicket) + 000000007 ;

                NotifyUser("Temps expiré", ""+Nom+" : Le ticket n'a pas été résolu à temps et est en retard", bitmap, idNotif);
            }
        };

        Handler handlerAttente = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                txtTempsRestant.setText("En attente...");
                txtTempsRestant.setTextColor(Color.parseColor("#434343"));
                layout.setBackgroundColor(Color.parseColor("#949494"));
                info.setImageResource(R.drawable.enattente);
            }
        };

        public void startTimer(long timeLeftMS, String statut, final String Nom, final String idTicket) {
            if(statut.equals("4")){
                handlerAttente.sendEmptyMessage(0);
            }
            else{
                if (timeLeftMS<0){
                    handlerLate.sendEmptyMessage(0);
                }
                else{
                    CountDownTimer countDownTimer = new CountDownTimer(timeLeftMS, 1000) {

                        @Override
                        public void onTick(long l) {
                            Bundle bundle = new Bundle();
                            bundle.putLong("time", l);
                            bundle.putString("name", Nom);
                            bundle.putString("id", idTicket);
                            Message message = new Message();
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onFinish() {
                            Bundle bundle = new Bundle();
                            bundle.putString("name", Nom);
                            bundle.putString("id", idTicket);
                            Message message = new Message();
                            message.setData(bundle);
                            handlerFinishLate.sendMessage(message);
                        }
                    }.start();
                }
            }

        }

    }

    private boolean TimeLeftBetween(long timeLeftMS, String min, String max) {
        boolean isOkay = false ;
        if ((timeLeftMS >= getMSfromTime(min))&&(timeLeftMS <= getMSfromTime(max))){
            isOkay = true;
        }

        return isOkay;
    }

    private void NotifyUser(String title, String content, Bitmap largeIcon, int idNotif) {
        Uri uriSoundNotif = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Random random = new Random();
        int ID_NOTIF = idNotif;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext(),  "")
                .setSmallIcon(R.drawable.sablier)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(content).setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //Vibration
        mBuilder.setVibrate(new long[] { 0, 1000, 1000, 1000, 1000 });

        //LED
        mBuilder.setLights(Color.RED, 3000, 3000);

        //Ton
        mBuilder.setSound(uriSoundNotif);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

        notificationManager.notify(ID_NOTIF, mBuilder.build());
    }

    private long getMSfromTime(String timeLeftText) {
        long timeMS = 0;

        //"00:01:21"

        String days = timeLeftText.substring(0, 2);
        String hours = timeLeftText.substring(3, 5);
        String minutes = timeLeftText.substring(6, 8);
        String seconds = timeLeftText.substring(9, 11);

        //System.out.println("hours = "+hours+" | minutes = "+minutes+" | seconds = "+seconds);

        long daysMS = 24*60*60*1000*Long.valueOf(days);
        long hoursMS = 3600000*Long.valueOf(hours);
        long minutesMS = 60000*Long.valueOf(minutes);
        long secondsMS = 1000*Long.parseLong(seconds);
        timeMS = daysMS + hoursMS + minutesMS + secondsMS;

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
        long timeLeft;
        String Statut;
        String Nom;
        String idTicket;
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


            timeLeft = Long.valueOf(TicketModel.getTempsRestantTicket());
            Statut = TicketModel.getStatut();
            Nom = TicketModel.getTitreTicket();
            idTicket = TicketModel.getIdTicket();


            result=convertView;
            //viewHolder.startTimer(Long.valueOf(TicketModel.getTempsRestantTicket()));


            viewHolder.startTimer(timeLeft, Statut, Nom, idTicket);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        //convertView.setHasTransientState(true);
        viewHolder.txtName = (TextView) convertView.findViewById(R.id.titreTV);
        viewHolder.txtDate = (TextView) convertView.findViewById(R.id.dateTV);
        viewHolder.txtSLA = (TextView) convertView.findViewById(R.id.slaTV);
        viewHolder.txtTempsRestant = (TextView) convertView.findViewById(R.id.SLARestantTV);
        viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);
        viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.backgroundRow);

        timeLeft = Long.valueOf(TicketModel.getTempsRestantTicket());
        Statut = TicketModel.getStatut();
        Nom = TicketModel.getTitreTicket();
        idTicket = TicketModel.getIdTicket();


        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(TicketModel.getTitreTicket());
        viewHolder.txtDate.setText(TicketModel.getDateTicket());
        viewHolder.txtSLA.setText(TicketModel.getSlaTicket());
        if (Long.valueOf(TicketModel.getTempsRestantTicket())<0){
            viewHolder.txtTempsRestant.setText("En retard");
        }
        //viewHolder.txtTempsRestant.setText(TicketModel.getTempsRestantTicket());
        //viewHolder.info.setImageResource(getIconUrgence(TicketModel.getUrgenceTicket()));
        //viewHolder.info.setImageResource(getIconUrgence(TicketModel.getUrgenceTicket()));
        viewHolder.layout.setBackgroundColor(getColorBG(TicketModel.isTicketEnRetard()));
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);

        System.out.println("getView");

//        System.out.println("Titre : "+TicketModel.getTitreTicket()); //getting each item's name
//        System.out.println("Time = "+TicketModel.getTempsRestantTicket()); //getting each item's time left and it's correct
//        System.out.println("Rebours = "+calculRebours(Long.valueOf(TicketModel.getTempsRestantTicket())));

        // Return the completed view to render on screen
        return convertView;
    }



    private String calculRebours(long timeLeftMS) {
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



    private int getIconUrgence(long timeLeftMS) {
        int icon = 0;

        /*if((urgenceTicket.equals("Très basse"))||(urgenceTicket.equals("Basse"))){
            icon = R.drawable.basse;
        }
        else if((urgenceTicket.equals("Haute"))||(urgenceTicket.equals("Très haute"))){
            icon = R.drawable.haute;
        }
        else {
            icon = R.drawable.moyenne;
        }*/

        String limite = "00:02:00:00";
        String aLaise = "03:00:00:00";

        if (timeLeftMS <= getMSfromTime(limite)){
            icon = R.drawable.haute;
        }
        else if ((timeLeftMS > getMSfromTime(limite))&&((timeLeftMS <= getMSfromTime(aLaise)))){
            icon = R.drawable.moyenne;
        }
        else if(timeLeftMS > getMSfromTime(aLaise)) {
            icon = R.drawable.basse;
        }

        return icon;
    }


}
