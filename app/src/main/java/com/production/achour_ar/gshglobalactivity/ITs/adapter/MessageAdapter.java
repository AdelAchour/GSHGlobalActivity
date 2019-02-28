package com.production.achour_ar.gshglobalactivity.ITs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.production.achour_ar.gshglobalactivity.ITs.data_model.RemoteMessageModel;
import com.production.achour_ar.gshglobalactivity.R;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<RemoteMessageModel> {

    private ArrayList<RemoteMessageModel> dataSet;
    Context mContext;


    // View lookup cache
    private class ViewHolder {
        TextView txtNameSender;
        TextView txtTitleMessage;
        TextView txtDateMessage;
    }


    public MessageAdapter(ArrayList<RemoteMessageModel> data, Context context) {
        super(context, R.layout.row_item_message, data);
        this.dataSet = data;
        this.mContext = context;
    }


    private int lastPosition = -1;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RemoteMessageModel model = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final MessageAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new MessageAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_message, parent, false);
            viewHolder.txtNameSender = convertView.findViewById(R.id.namesender);
            viewHolder.txtTitleMessage = convertView.findViewById(R.id.titleremotemessage);
            viewHolder.txtDateMessage = convertView.findViewById(R.id.dateMessage);


            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MessageAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtNameSender.setText(model.getName());
        viewHolder.txtTitleMessage.setText(model.getTitle());
        viewHolder.txtDateMessage.setText(model.getDate());


        // Return the completed view to render on screen
        return convertView;
    }

}