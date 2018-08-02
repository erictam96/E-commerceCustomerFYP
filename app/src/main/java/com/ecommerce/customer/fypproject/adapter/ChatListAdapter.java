package com.ecommerce.customer.fypproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ecommerce.customer.fypproject.ChatActivity;
import com.ecommerce.customer.fypproject.R;

import java.util.List;
import java.util.Stack;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder>{
    private  Stack<ChatList> dataHere;
    private final Context context;
    private final List<ChatList> dataAdapter;
    boolean firstRun=true;

    public ChatListAdapter( List<ChatList> getDataAdapter,Context context){
        //List<ChatList> getDataAdapter,
        super();
        //this.dataAdapter = new ArrayList<>();
        this.dataAdapter=getDataAdapter;
        this.context = context;

       // notifyItemRangeChanged(0,dataAdapter.size());

//        for(int a=0;a<getDataAdapter.size();a++){
//            dataAdapter.add(getDataAdapter.pop());
//        }
        //dataHere=new ArrayList<>();
    }
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatlist_card, parent, false);

        return new ChatListAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ChatListAdapter.ViewHolder Viewholder, final int position) {

        // checked=new boolean[dataAdapters.size()];
//        if(!dataAdapter.isEmpty()){//first run
//            dataHere.push()
//        }



             //final ChatList dataAdapterOBJ = dataAdapter.peek();
             final ChatList dataAdapterOBJ = dataAdapter.get(position);
             Viewholder.ChatName.setText(dataAdapterOBJ.getName());
             Viewholder.ChatMsg.setText(dataAdapterOBJ.getMsg());
             Viewholder.ChatDate.setText(dataAdapterOBJ.getDate());
        Log.e("bind adapter","called");

        Viewholder.ChatCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click","click");
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("UID",dataAdapterOBJ.getRecvUID());
                intent.putExtra("username",dataAdapterOBJ.getName());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataAdapter.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        final TextView ChatName;
        final TextView ChatMsg;
        final TextView ChatDate;
        final CircleImageView ChatPic;
        final CardView ChatCard;

        ViewHolder(View itemView) {
            super(itemView);
            ChatName=itemView.findViewById(R.id.txtChatUsername);
            ChatMsg=itemView.findViewById(R.id.txtChatLastMsg);
            ChatDate=itemView.findViewById(R.id.txtChatDate);
            ChatPic = itemView.findViewById(R.id.imgChatUserLogo);
            ChatCard = itemView.findViewById(R.id.cardviewChatList);
        }
    }

    public void addNode(ChatList x){
        Log.e("add node","called");
        chkAndRemove(x);
        dataAdapter.add(0,x);
        notifyItemRangeChanged(0,dataAdapter.size());

    }
    public void addLastNode(ChatList x){
        Log.e("add node","called");
        dataAdapter.add(x);
        notifyItemRangeChanged(0,dataAdapter.size());

    }

    private void chkAndRemove(ChatList x){
        for(int a=0;a<dataAdapter.size();a++){
            if(dataAdapter.get(a).getRecvUID().equals(x.getRecvUID())){
                dataAdapter.remove(a);
            }
        }
    }


}

