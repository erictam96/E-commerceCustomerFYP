package com.ecommerce.customer.fypproject.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ecommerce.customer.fypproject.R;
import com.ecommerce.customer.fypproject.SplashScreenActivity;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ItemFeedbackAdapter extends RecyclerView.Adapter<ItemFeedbackAdapter.ViewHolder>  {
    private final Context context;

    private final List<ItemFeedback> dataAdapters;
    private Dialog ratingDialog;
    private RatingBar ratingBar;
    private EditText commentText;
    private TextView itemName;
    private TextView itemVariant;
    private TextView wordcount;
    private ImageView dialogItemImage;
    private int clickedPosition;
    private final String PHPURL="https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    private String uid;
    private ProgressDialog progressDialog;
    private final OnItemClick mCallback;




    public ItemFeedbackAdapter(List<ItemFeedback> getDataAdapter, Context context, OnItemClick listener){

        super();
        this.dataAdapters = getDataAdapter;
        this.context = context;
        this.mCallback = listener;

    }

    @Override
    public ItemFeedbackAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback_card, parent, false);

        ItemFeedbackAdapter.ViewHolder viewHolder = new ItemFeedbackAdapter.ViewHolder(view);
        GetFirebaseAuth();
        viewRatingDialog();
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(true);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(context.getResources().getString(R.string.prepareFeedbackList));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemFeedbackAdapter.ViewHolder Viewholder, final int position) {


        ItemFeedback dataAdapterOBJ = dataAdapters.get(position);


        Viewholder.shopName.setText(dataAdapterOBJ.getShopname());
        Viewholder.deliveredDate.setText(dataAdapterOBJ.getDeliveredDate());
        Viewholder.itemName.setText(dataAdapterOBJ.getItemName());
        Viewholder.itemVariant.setText(context.getResources().getString(R.string.variant2)+dataAdapterOBJ.getItemVariant());
        Viewholder.itemQuantity.setText(context.getResources().getString(R.string.qty3)+dataAdapterOBJ.getQuantity());


        Glide.with(context)
                .load(dataAdapterOBJ.getItemURL()) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.photo) // any placeholder to load at start
                        .error(R.drawable.photo)  // any image in case of error
                        .override(500, 500) // resizing
                        .centerCrop())
                .into(Viewholder.itemPic);  // imageview object

        Glide.with(context)
                .load(dataAdapterOBJ.getRetailerProfilePicURL()) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.photo) // any placeholder to load at start
                        .error(R.drawable.photo)  // any image in case of error
                        .override(200, 200) // resizing
                        .centerCrop())
                .into(Viewholder.retailerProfilePic);  // imageview object

        Viewholder.ratingBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedPosition=Viewholder.getAdapterPosition();
                ratingDialog.show();
            }
        });
//        Viewholder.orderText.setText(dataAdapterOBJ.getOrderNumber());
//        Viewholder.collectText.setText(dataAdapterOBJ.getNumberReadyCollectItem());
//        Viewholder.totalItem.setText(dataAdapterOBJ.getTotalItem());
//        Viewholder.dateText.setText(dataAdapterOBJ.getDate());
//        if(Integer.parseInt(dataAdapterOBJ.getNumberReadyCollectItem().toString())>0){
//            Viewholder.notifyLabel.setVisibility(View.VISIBLE);
//        }else{
//            Viewholder.notifyLabel.setVisibility(View.INVISIBLE);
//        }
////
    }


    @Override
    public int getItemCount() {

        return dataAdapters.size();
    }





    class ViewHolder extends RecyclerView.ViewHolder{

        // public Button itemModelName;
        final ImageView retailerProfilePic;
        final ImageView itemPic;
        final TextView shopName;
        final TextView deliveredDate;
        final TextView itemName;
        final TextView itemVariant;
        final TextView itemQuantity;
        final Button ratingBut;


        ViewHolder(View itemView) {

            super(itemView);
            // itemModelName = (Button) itemView.findViewById(R.id.itemModelName) ;
            retailerProfilePic=itemView.findViewById(R.id.feedbackRetailerProfileImg);
            itemPic=itemView.findViewById(R.id.feedbackItemImage);
            shopName=itemView.findViewById(R.id.feedbackShopName);
            deliveredDate=itemView.findViewById(R.id.feedbackDeliveryDate);
            itemName=itemView.findViewById(R.id.feedbackItemName);
            itemVariant=itemView.findViewById(R.id.feedbackItemVariant);
            itemQuantity=itemView.findViewById(R.id.feedbackItemQty);
            ratingBut=itemView.findViewById(R.id.feedbackRatingBut);

        }
    }

    private void viewRatingDialog(){
        ratingDialog = new Dialog(context, R.style.MaterialDialogSheet);
        Objects.requireNonNull(ratingDialog.getWindow()).setBackgroundDrawable(context.getResources().getDrawable(R.drawable.transparent_back));
        ratingDialog.setContentView(R.layout.item_feedback_dialog); // your custom view.
        ratingDialog.setCancelable(true);
        ratingDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ratingDialog.getWindow().setGravity(Gravity.CENTER);


        dialogItemImage=ratingDialog.findViewById(R.id.ratingImage);
        ratingBar=ratingDialog.findViewById(R.id.ratingBar);
        commentText=ratingDialog.findViewById(R.id.feedbackDescription);
        TextView submitBut = ratingDialog.findViewById(R.id.feedbackSubmit);
        itemName=ratingDialog.findViewById(R.id.feedbackDialogItemName);
        itemVariant=ratingDialog.findViewById(R.id.feedbackDialogItemVariant);
        wordcount=ratingDialog.findViewById(R.id.feedbackWordCount);


        commentText.addTextChangedListener(textWatcher);

        ratingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,Float.toString(ratingBar.getRating()),Toast.LENGTH_SHORT).show();
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(context,Float.toString(ratingBar.getRating()),Toast.LENGTH_SHORT).show();

            }
        });



        ratingDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Glide.with(context)
                        .load(dataAdapters.get(clickedPosition).getItemURL()) // image url
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.photo) // any placeholder to load at start
                                .error(R.drawable.photo)  // any image in case of error
                                .override(1000, 1000) // resizing
                                .centerCrop())
                        .into(dialogItemImage);
                itemName.setText(dataAdapters.get(clickedPosition).getItemName());
                itemVariant.setText(dataAdapters.get(clickedPosition).getItemVariant());

                dialogItemImage.bringToFront();
                commentText.setText("");
                float x=3;
                ratingBar.setRating(x);

            }
        });

        submitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSON_HTTP_CALL();
                ratingDialog.dismiss();

            }
        });


    }
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String word = 300-commentText.length()+context.getResources().getString(R.string.charLeft);
            wordcount.setText(word);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void JSON_HTTP_CALL() {
        Answers.getInstance().logCustom(new CustomEvent("Item Feedback")
                .putCustomAttribute("Rating Star",Float.toString(ratingBar.getRating())));
        GetFirebaseAuth();
        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);

                Log.e("ResponseFeedback", response);
                mCallback.onClick("refresh");
                progressDialog.dismiss();
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();


                JSONObject objectDetail = new JSONObject();
                JSONArray array = new JSONArray();
                try {

                    objectDetail.put("custid", uid);
                    objectDetail.put("ratingStar", Float.toString(ratingBar.getRating()));
                    objectDetail.put("ratingComment", commentText.getText().toString());
                    objectDetail.put("prodname", dataAdapters.get(clickedPosition).getItemName());
                    objectDetail.put("prodvariant", dataAdapters.get(clickedPosition).getItemVariant());
                    objectDetail.put("deliveredTime", dataAdapters.get(clickedPosition).getDeliveredDate());

                    array.put(objectDetail);
                }catch (Exception ignored){

                }
                HashMapParams.put("feedbackComment",array.toString());
                return ProcessClass.HttpRequest(PHPURL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();

    }

    private void GetFirebaseAuth(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent = new Intent(context, SplashScreenActivity.class);
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            Toast.makeText(context,R.string.sessionexp,Toast.LENGTH_LONG).show();
        }else uid = firebaseAuth.getCurrentUser().getUid();
    }

}
