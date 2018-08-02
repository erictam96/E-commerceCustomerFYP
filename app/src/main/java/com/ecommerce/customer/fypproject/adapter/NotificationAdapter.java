package com.ecommerce.customer.fypproject.adapter;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ecommerce.customer.fypproject.CheckoutItemActivity;
import com.ecommerce.customer.fypproject.ManageOrderActivity;
import com.ecommerce.customer.fypproject.R;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private final List<NotificationList> dataAdapter;
    private final Context context;
    private OnItemClick mCallback;
    private final String NotificationPathOnServer = "https://ecommercefyp.000webhostapp.com/retailer/customer_manage_user.php";
    //String NotificationPathOnServer = "http://10.0.2.2/cashierbookPHP/Eric/customer_manage_user.php";

    public  NotificationAdapter(List<NotificationList> getDataAdapter, Context context){
        super();
        this.dataAdapter = getDataAdapter;
        this.context = context;
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card, parent, false);

        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final NotificationAdapter.ViewHolder Viewholder, final int position) {

        // checked=new boolean[dataAdapters.size()];
        final NotificationList dataAdapterOBJ = dataAdapter.get(position);

        if(dataAdapterOBJ.getNotifyTitle().equalsIgnoreCase("Order on the way")){
            Viewholder.notifyTitle.setText(context.getResources().getString(R.string.notificationOrderOTW));
        }else if(dataAdapterOBJ.getNotifyTitle().equalsIgnoreCase("Your item has arrived")){
            Viewholder.notifyTitle.setText(context.getResources().getString(R.string.itemArrived));
        }else if(dataAdapterOBJ.getNotifyTitle().equalsIgnoreCase("Order cancelled")) {
            Viewholder.notifyTitle.setText(context.getResources().getString(R.string.ordercancelled));
        }else{
            String x=context.getString(R.string.itemAvailable);
            Viewholder.notifyTitle.setText(x);
        }
        String msg=dataAdapterOBJ.getNotifyMsg();
        msg=msg.replace("Your item",context.getString(R.string.itemAvailable2));
        msg=msg.replace("is available. Please make payment within 24 hours",context.getString(R.string.itemAvailable3));
        msg=msg.replace("your order is ready to deliver",context.getString(R.string.notificationOrderOTW2));
        msg=msg.replace("Your order is delivering",context.getString(R.string.orderDelivering));
        msg=msg.replace("Please click manage order to collect item",context.getString(R.string.itemArrivedMsg));
        Log.e("notf msg",msg);
        Viewholder.notifyMsg.setText(msg);

        String date=dataAdapterOBJ.getNotifyDate();
        date=date.replace("ago",context.getResources().getString(R.string.ago));
        date=date.replace("hours",context.getResources().getString(R.string.hours));
        date=date.replace("hour",context.getResources().getString(R.string.hour));
        date=date.replace("days",context.getResources().getString(R.string.days));
        date=date.replace("day",context.getResources().getString(R.string.day));
        date=date.replace("years",context.getResources().getString(R.string.years));
        date=date.replace("year",context.getResources().getString(R.string.year));
        date=date.replace("months",context.getResources().getString(R.string.months));
        date=date.replace("month",context.getResources().getString(R.string.month));
        date=date.replace("minutes",context.getResources().getString(R.string.minutes));
        date=date.replace("minute",context.getResources().getString(R.string.minute));
        date=date.replace("seconds",context.getResources().getString(R.string.seconds));
        date=date.replace("second",context.getResources().getString(R.string.second));

        Viewholder.notifyDate.setText(date);
        Glide.with(context)
                .load(dataAdapterOBJ.getNotifyURL()) // image url
                .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.cashierbooklogosmall) // any placeholder to load at start
                        .error(R.drawable.cashierbooklogosmall)  // any image in case of error
                        .override(500, 500) // resizing
                        .centerCrop())
                .into(Viewholder.notifyPic);
        if(dataAdapterOBJ.getNotifyStatus().equals("UNREAD")){
            Viewholder.notifycard.setBackground(context.getResources().getDrawable(R.color.light_grey));
        }
        Viewholder.notifycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String GOTO = dataAdapterOBJ.getNotifyAction();
                JSON_HTTP_CALL(dataAdapterOBJ.getNotifyID());
                PendingIntent pendingIntent;
                switch (GOTO) {
                    case "donepackitem":
                        Intent intent = new Intent(context, ManageOrderActivity.class);
                        context.startActivity(intent);
                        break;
                    case "checkoutorder":
                        Intent intent1 = new Intent(context, CheckoutItemActivity.class);
                        context.startActivity(intent1);
                        break;
                    default:
                        break;
                }
            }
        });
    }


    private void JSON_HTTP_CALL(final String NotifyID) {
        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                Log.d("Response", response);
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();
                HashMapParams.put("notifyID", NotifyID);
                return ProcessClass.HttpRequest(NotificationPathOnServer, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();
    }

    @Override
    public int getItemCount() {
        return dataAdapter.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        final TextView notifyTitle;
        final TextView notifyMsg;
        final TextView notifyDate;
        final CircleImageView notifyPic;
        final CardView notifycard;

        ViewHolder(View itemView) {
            super(itemView);
            notifyTitle=itemView.findViewById(R.id.notificationtitle);
            notifyMsg=itemView.findViewById(R.id.notificationtext);
            notifyDate=itemView.findViewById(R.id.notificationdate);
            notifycard = itemView.findViewById(R.id.notification_card);
            notifyPic= itemView.findViewById(R.id.notificationPic);
        }
    }
}
