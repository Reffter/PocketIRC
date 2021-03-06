package pt.ubi.di.pdm.pocketirc;

//imports
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ChannelsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements MessageRecyclerViewAdapter.ItemClickListener{
    private final ArrayList<String> mData;
    private final LayoutInflater mInflater;
    private MessageRecyclerViewAdapter messageAdapter;

    // data is passed into the constructor
    ChannelsRecyclerViewAdapter(Context context, ArrayList<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View view = mInflater.inflate(R.layout.channel_list_recycler_view_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder,int position) {
        ((ViewHolder)holder).typeTextView.setText(mData.get(position));

        if(ChatRoom.channel_status.get(mData.get(position)) == 1){
            ((ViewHolder)holder).status.setVisibility(View.VISIBLE);
        }
        else{
            ((ViewHolder)holder).status.setVisibility(View.INVISIBLE);
        }

        if(mData.get(position).equals("NickServ"))
        {
            ((ViewHolder)holder).remove.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(mData.get(holder.getAdapterPosition()));

                //Update the current chat
                ChatRoom.chatName = mData.get(holder.getAdapterPosition());

                //Updates roomStatus
                ChatRoom.channel_status.put(mData.get(holder.getAdapterPosition()),0);
                notifyItemChanged(holder.getAdapterPosition());

                // Update message Adapter
                messageAdapter = new MessageRecyclerViewAdapter(v.getContext(), ChatRoom.channels_messageList.get(ChatRoom.chatName));
                ChatRoom.messageRecyclerView.setAdapter(messageAdapter);
                messageAdapter.setClickListener(ChannelsRecyclerViewAdapter.this);

                ChatRoom.toolbar.setTitle(ChatRoom.chatName);
                ChatRoom.drawerLayout.closeDrawer(ChatRoom.leftDrawer);

                //Empty the user List
                ChatRoom.channelUserList.clear();

                //Update userList
                ChatRoom.cmd.names(ChatRoom.chatName);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onItemClick(View view, int position) {
        System.out.println("Pixotas");
    }

    public void delete(View v,int position) { //removes the row
        if(mData.get(position).equals(ChatRoom.chatName))
        {
            //Update the current chat
            ChatRoom.chatName = "NickServ";

            // Update message Adapter
            messageAdapter = new MessageRecyclerViewAdapter(v.getContext(), ChatRoom.channels_messageList.get(ChatRoom.chatName));
            ChatRoom.messageRecyclerView.setAdapter(messageAdapter);
            messageAdapter.setClickListener(ChannelsRecyclerViewAdapter.this);

            ChatRoom.toolbar.setTitle(ChatRoom.chatName);
            ChatRoom.drawerLayout.closeDrawer(ChatRoom.leftDrawer);

            //Empty the user List
            ChatRoom.channelUserList.clear();

            //Update userList
            ChatRoom.cmd.names(ChatRoom.chatName);
        }
        System.out.println(mData.get(position));
        ChatRoom.cmd.part(mData.get(position),"User left");

        //Update statusList
        ChatRoom.channel_status.remove(mData.get(position));

        ChatRoom.channels_messageList.remove(mData.get(position));
        mData.remove(position);
        notifyItemRemoved(position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView typeTextView;
        ImageButton remove;
        ImageView status;

        ViewHolder(View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.channel_row_title);
            remove = itemView.findViewById(R.id.channel_row_remove);
            status = itemView.findViewById(R.id.channel_row_status);

            remove.setOnClickListener(ViewHolder.this);
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

            builder.setTitle("Exit room");
            builder.setMessage("Are you sure?");

            builder.setPositiveButton("YES", (dialog, which) -> {
                // Do nothing but close the dialog
                delete(v,getAdapterPosition()); //calls the method above to delete
                dialog.dismiss();
            });
            builder.setNegativeButton("NO", (dialog, which) -> {

                // Do nothing
                dialog.dismiss();
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

}
