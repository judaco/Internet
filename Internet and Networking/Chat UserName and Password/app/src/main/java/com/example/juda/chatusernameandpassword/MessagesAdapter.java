package com.example.juda.chatusernameandpassword;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Juda on 24/12/2016.
 */

public class MessagesAdapter extends ArrayAdapter<Message> {

    private Activity activity;
    private List<Message> messages;
    private String userName;

    public MessagesAdapter(Activity activity, List<Message> messages) {
        super(activity, R.layout.item_message, messages);
        this.activity = activity;
        this.messages = messages;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    static class ViewContainer{
        TextView lblSender;
        TextView lblContent;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewContainer viewContainer = null;
        if(rowView == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_message, null);
            viewContainer = new ViewContainer();
            viewContainer.lblContent = (TextView)rowView.findViewById(R.id.lblContent);
            viewContainer.lblSender = (TextView)rowView.findViewById(R.id.lblSender);
            rowView.setTag(viewContainer);
        }else{
            viewContainer = (ViewContainer)rowView.getTag();
        }
        Message message = messages.get(position);
        viewContainer.lblContent.setText(message.getContent());
        viewContainer.lblSender.setText(message.getSender());
        viewContainer.lblSender.setTextColor( activity.getResources().getColor(userName.equals(message.getSender()) ? android.R.color.holo_blue_dark : android.R.color.black, null));

        return rowView;
    }
}