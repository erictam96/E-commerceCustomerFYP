package com.ecommerce.customer.fypproject.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ecommerce.customer.fypproject.ItemDetailActivity;
import com.ecommerce.customer.fypproject.R;
import com.ecommerce.customer.fypproject.SplashScreenActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CheckoutItemAdapter extends RecyclerView.Adapter<CheckoutItemAdapter.ViewHolder>{
    private final Context context;

    private final List<CartItem> dataAdapters;
    CheckoutItemAdapter.ViewHolder currentView;
    private final boolean[] checked;
    private final OnItemClick mCallback;
    private final String PHPURL="https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    private Dialog photoDialog,cancelDialog;
    private ImageView viewImage;
    private int currentindex=0,maxImgCount=1;
    private int photoPosition=0,clickedPosition=0;
    private TextView sellerTxt,photoDateTxt,wordcount;
    private RadioGroup radioGroup;
    private EditText cancelText;
    private final DecimalFormat df2 = new DecimalFormat("0.00");
    private String uid;


    public CheckoutItemAdapter(List<CartItem> getDataAdapter, Context context, OnItemClick listener){

        super();
        this.dataAdapters = getDataAdapter;
        this.context = context;
        checked=new boolean[dataAdapters.size()];
        this.mCallback = listener;

    }

    @Override
    public CheckoutItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item_card, parent, false);

        CheckoutItemAdapter.ViewHolder viewHolder = new CheckoutItemAdapter.ViewHolder(view);
        viewPhotoDialog();
        viewCancelDialog();
        GetFirebaseAuth();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CheckoutItemAdapter.ViewHolder Viewholder, final int position) {


        final CartItem dataAdapterOBJ = dataAdapters.get(position);


        Viewholder.shopNameText.setText(dataAdapterOBJ.getRetailerShopName());
       // Viewholder.itemStatusText.setText(dataAdapterOBJ.getItemStatus());
        Viewholder.itemname.setText(dataAdapterOBJ.getProdName());
        Viewholder.itemvariant.setText(dataAdapterOBJ.getProdVariant());
        Viewholder.itemprice.setText("RM "+df2.format(Double.parseDouble(dataAdapterOBJ.getProdPrice())));
        Viewholder.itemprice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        Viewholder.itemqty.setText(dataAdapterOBJ.getCartQty());
        Viewholder.itemChkBox.setChecked(checked[Viewholder.getAdapterPosition()]);

        Viewholder.discountPrice.setText("RM "+df2.format(Double.parseDouble(dataAdapterOBJ.getDiscountedPrice())));

        Viewholder.discountText.setText("-"+dataAdapterOBJ.getDiscount()+"%");

        if(Integer.parseInt(dataAdapterOBJ.getDiscount())==0){
            Viewholder.itemprice.setVisibility(View.INVISIBLE);
            Viewholder.discountText.setVisibility(View.INVISIBLE);
        }else{
            Viewholder.itemprice.setVisibility(View.VISIBLE);
            Viewholder.discountText.setVisibility(View.VISIBLE);
        }


        Viewholder.realPhotoBut.setVisibility(View.GONE);

        Viewholder.realPhotoBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoPosition=Viewholder.getAdapterPosition();

                loadImage(dataAdapters.get(photoPosition).getRealPhoto1());

                String catstring=context.getString((R.string.photofromseller));
                catstring=catstring+Viewholder.shopNameText.getText();
                sellerTxt.setText(catstring);

                catstring=context.getString((R.string.photodate));
                catstring=catstring+dataAdapters.get(photoPosition).getPhotodate();
                photoDateTxt.setText(catstring);

                photoDialog.show();
            }
        });

//different view setting with different item status
        if(dataAdapterOBJ.getItemStatus().equalsIgnoreCase("pending")){
            Viewholder.itemStatusText.setTextColor(Color.GRAY);
            Viewholder.itemStatusText.setText(context.getString((R.string.pending)));
        }else if(dataAdapterOBJ.getItemStatus().equalsIgnoreCase("available")){
            Viewholder.itemStatusText.setText(context.getString((R.string.available)));
            Viewholder.itemStatusText.setTextColor(Color.GREEN);
            Viewholder.realPhotoBut.setVisibility(View.VISIBLE);
            //photo button animation, the breathing light animation
            final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
            animation.setDuration(1000); // duration - half a second
            animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
            animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
            animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Viewholder.realPhotoBut.setHasTransientState(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            Viewholder.realPhotoBut.setHasTransientState(true);
            Viewholder.realPhotoBut.startAnimation(animation);
        }else if(dataAdapterOBJ.getItemStatus().equalsIgnoreCase("cancel no response")){
            Viewholder.itemStatusText.setTextColor(Color.RED);
            Viewholder.itemStatusText.setText(context.getString(R.string.cancelNoRsp));
            Viewholder.delBut.setVisibility(View.VISIBLE);
            Viewholder.delBut.setClickable(true);
        }else {
            Viewholder.itemStatusText.setTextColor(Color.RED);
            Viewholder.itemStatusText.setText(context.getString(R.string.outofstock));
            Viewholder.delBut.setVisibility(View.VISIBLE);
            Viewholder.delBut.setClickable(true);
        }

        if(!dataAdapterOBJ.getItemStatus().equalsIgnoreCase("available")){
            Viewholder.itemChkBox.setEnabled(false);
            Viewholder.itemChkBox.setChecked(false);
            checked[Viewholder.getAdapterPosition()] = false;
        }
//load retailer profile header
        Glide.with(context)
                .load(dataAdapterOBJ.getRetailerProfPicURL()) // image url
                .apply(new RequestOptions()
                .placeholder(R.drawable.photo) // any placeholder to load at start
                .error(R.drawable.photo)  // any image in case of error
                .override(200, 200) // resizing
                .centerCrop())
                .into(Viewholder.shopImg);  // imageview object

//load product image
        Glide.with(context)
                .load(dataAdapterOBJ.getProdprofURL()) // image url
                .apply(new RequestOptions()
                .placeholder(R.drawable.photo) // any placeholder to load at start
                .error(R.drawable.photo)  // any image in case of error
                .override(500, 500) // resizing
                .centerCrop())
                .into(Viewholder.itemImg);  // imageview object

        Viewholder.itemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ItemDetailActivity.class);
                //Pack Data to Send
                intent.putExtra("prodCode",dataAdapterOBJ.getProdcode());
                //intent.putExtra("onlickListener",ClassUtils.getAllInterfaces);

                //open activity
                context.startActivity(intent);
            }
        });
        Viewholder.itemChkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checked[Viewholder.getAdapterPosition()] = Viewholder.itemChkBox.isChecked();
                mCallback.onClick(Double.toString(calculateTotal()));
                if(loopChk()){
                    mCallback.onClick("check");
                }else{
                    mCallback.onClick("uncheck");
                }
                //here adadasdasdasdasdasdasdasdsdasdasdas


            }
        });

        Viewholder.delBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                mCallback.onClick("refresh");
                clickedPosition=Viewholder.getAdapterPosition();
                cancelDialog.show();
            }
        });
//        Viewholder.orderText.setText(dataAdapterOBJ.getOrderNumber());
//        Viewholder.deliveredText.setText(dataAdapterOBJ.getNumberDeliveredItem());
//        Viewholder.totalItem.setText(dataAdapterOBJ.getTotalItem());
//        Viewholder.dateText.setText(dataAdapterOBJ.getDate());
    }
    @Override
    public int getItemCount() {

        return dataAdapters.size();
    }

    //function to make all available item checkbox become checked/uncheck
    public void chkAll(boolean value){

        //   triggerChange=true;

        for(int a=0;a<dataAdapters.size();a++){
            if(dataAdapters.get(a).getItemStatus().equalsIgnoreCase("available")){
                checked[a]=value;
                Log.d("trigger no.",Integer.toString(a)+Boolean.toString(checked[a]));
            }

        }
        //triggerChange=false;
        notifyItemRangeChanged(0,dataAdapters.size());
        mCallback.onClick(Double.toString(calculateTotal()));
    }



    class ViewHolder extends RecyclerView.ViewHolder{

        // public Button itemModelName;
        final CheckBox itemChkBox;
        final TextView shopNameText;
        final TextView itemStatusText;
        final TextView itemname;
        final TextView itemvariant;
        final TextView itemprice;
        final TextView itemqty;
        final TextView discountText;
        final TextView discountPrice;
        final ImageButton delBut;
        final ImageView shopImg;
        final ImageView itemImg;
        final Button realPhotoBut;


        @SuppressLint("CutPasteId")
        ViewHolder(View itemView) {

            super(itemView);
            // itemModelName = (Button) itemView.findViewById(R.id.itemModelName) ;
//            orderText=itemView.findViewById(R.id.orderText);
//
//            totalItem=itemView.findViewById(R.id.totalItemText);
//            dateText=itemView.findViewById(R.id.orderdateText);
//            deliveredText=itemView.findViewById(R.id.deliverdItemText);

            itemChkBox=itemView.findViewById(R.id.checkOutChkBox);
            shopNameText=itemView.findViewById(R.id.checkoutShopName);
            itemStatusText=itemView.findViewById(R.id.checkoutItemStatus);
            itemname=itemView.findViewById(R.id.checkoutTxtTitle);
            itemvariant=itemView.findViewById(R.id.payVariantText);
            itemprice=itemView.findViewById(R.id.checkoutTxtPrice);
            itemqty=itemView.findViewById(R.id.checkoutQty);
            delBut=itemView.findViewById(R.id.delChkoutItemBut);
            shopImg=itemView.findViewById(R.id.checkoutShopIcon);
            itemImg=itemView.findViewById(R.id.CheckoutItemImg);
            realPhotoBut=itemView.findViewById(R.id.viewRealPhotoBut);
            discountText=itemView.findViewById(R.id.checkoutDiscountTxt);
            discountPrice=itemView.findViewById(R.id.checkoutDiscountTxtPrice);
        }
    }

    private boolean cartIsChecked(int index){
        return checked[index];
    }

    public boolean[] getCheckedList(){
        return checked;
    }
//calculate all checked item total
    public double calculateTotal(){
        double cartsubtotal=0;
        for(int a=0;a<dataAdapters.size();a++){
            if(cartIsChecked(a)){
                cartsubtotal=cartsubtotal+Integer.parseInt(dataAdapters.get(a).getCartQty())*Double.parseDouble(dataAdapters.get(a).getDiscountedPrice());
            }
        }


        Log.d("total",df2.format(cartsubtotal));
        return cartsubtotal;
    }
//send command to server to delete pending order
private void JSON_HTTP_CALL(final String expdate, final String custid, final String feedback, final String cancelType) {

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //placeOrderDialog.show();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                mCallback.onClick("done refresh");

                Log.d("Response", response);
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put("delChkOutItem", expdate);
                HashMapParams.put("custid", custid);
                HashMapParams.put("reason", feedback);
                HashMapParams.put("canceltype", cancelType);
                HashMapParams.put("cancelProdName", dataAdapters.get(clickedPosition).getProdName());
                HashMapParams.put("cancelProdVariant", dataAdapters.get(clickedPosition).getProdVariant());


                return ProcessClass.HttpRequest(PHPURL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();

    }
//dialog to view real time photo sent by retailer
    private void viewPhotoDialog(){
        photoDialog = new Dialog(context, R.style.MaterialDialogSheet);
        photoDialog.setContentView(R.layout.real_photo_layout); // your custom view.
        photoDialog.setCancelable(true);
        Objects.requireNonNull(photoDialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        photoDialog.getWindow().setGravity(Gravity.CENTER);

        viewImage=photoDialog.findViewById(R.id.realViewImage);
        Button viewleft = photoDialog.findViewById(R.id.realViewleft);
        Button viewright = photoDialog.findViewById(R.id.realViewright);
        sellerTxt=photoDialog.findViewById(R.id.sellerNameText);
        photoDateTxt=photoDialog.findViewById(R.id.sellerReplyDate);
        loadImage(dataAdapters.get(photoPosition).getRealPhoto1());

        viewleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentindex>0){
                    rollDie();
                    currentindex--;
                    switch (currentindex){
                        case 0:
                            loadImage(dataAdapters.get(photoPosition).getRealPhoto1());
                            break;
                        case 1:
                            loadImage(dataAdapters.get(photoPosition).getRealPhoto2());
                            break;

                        default:
                            break;
                    }

                }
            }
        });


        viewright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataAdapters.get(photoPosition).getRealPhoto2().equalsIgnoreCase("null")){
                    maxImgCount=0;
                }else{
                    maxImgCount=1;
                }
                if(currentindex<maxImgCount){
                    rollDie();
                    currentindex++;
                    switch (currentindex){
                        case 0:
                            loadImage(dataAdapters.get(photoPosition).getRealPhoto1());
                            break;
                        case 1:
                            loadImage(dataAdapters.get(photoPosition).getRealPhoto2());
                            break;
                        default:
                            break;
                    }

                }
            }
        });
        //((BitmapDrawable)itemDetailImage.getDrawable()).getBitmap()
    }
    private void loadImage(String url){
        Glide.with(context)
                .load(url) // image url
                .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                .apply(new RequestOptions()
                .placeholder(R.drawable.photo) // any placeholder to load at start
                .error(R.drawable.photo)  // any image in case of error
                .override(1000, 1000) // resizing
                .fitCenter())
                .into(viewImage);
        viewImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }
    //animation on clicked next photo in viewphotodialog
    private void rollDie() {
        // Setup the animation.
        Animation shake = AnimationUtils.loadAnimation(context,
                android.R.anim.fade_in);

        viewImage.setAnimation(shake);

        shake.start();

    }
    //dialog to ask why customer delete order
    private void viewCancelDialog(){
        cancelDialog = new Dialog(context, R.style.MaterialDialogSheet);
        cancelDialog.setContentView(R.layout.cancel_pending_order); // your custom view.
        cancelDialog.setCancelable(true);
        Objects.requireNonNull(cancelDialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cancelDialog.getWindow().setGravity(Gravity.CENTER);

        wordcount=cancelDialog.findViewById(R.id.txtwordcount);
        radioGroup=cancelDialog.findViewById(R.id.radio_cancel);
        cancelText=cancelDialog.findViewById(R.id.cancel_text);
        Button submitCancelReason = cancelDialog.findViewById(R.id.pending_cancel_submit_but);
        Button cancelSubmit = cancelDialog.findViewById(R.id.pending_cancel_but);

        //pre-select 1st option
        radioGroup.check(R.id.radio1);
        cancelText.setEnabled(false);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.radio3){
                    cancelText.setEnabled(true);
                    cancelText.addTextChangedListener(textWatcher);
                }else{
                    cancelText.setEnabled(false);
                }
            }
        });



        submitCancelReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cancel_feedback;
                String expdate= dataAdapters.get(clickedPosition).getExpdate();
                String custid=uid;
                String cancel_type="";

                if(radioGroup.getCheckedRadioButtonId()==R.id.radio3){
                   cancel_feedback=cancelText.getText().toString();
                }else{
                    RadioButton x=cancelDialog.findViewById(radioGroup.getCheckedRadioButtonId());
                    cancel_feedback=x.getText().toString();
                }
                if(dataAdapters.get(clickedPosition).getItemStatus().equalsIgnoreCase("available")){
                    cancel_type="cancel available";
                }else if(dataAdapters.get(clickedPosition).getItemStatus().equalsIgnoreCase("pending")){
                    cancel_type="cancel pending";
                }else if(dataAdapters.get(clickedPosition).getItemStatus().equalsIgnoreCase("out of stock")){
                    cancel_type="cancel out of stock";
                }else if(dataAdapters.get(clickedPosition).getItemStatus().equalsIgnoreCase("cancel no response")){
                    cancel_type="cancel no response";

                }

                JSON_HTTP_CALL(expdate,custid,cancel_feedback,cancel_type);
                mCallback.onClick("refresh");
                cancelDialog.dismiss();
            }
        });

        cancelSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDialog.dismiss();
            }
        });

        //submitCancelReason=0;

        //viewImage=cancelDialog.findViewById(R.id.realViewImage);

    }

    private boolean loopChk(){
        for(int a=0;a<dataAdapters.size();a++){
            if(!cartIsChecked(a)){
                return false;
            }
        }
        return true;
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String word = 150-cancelText.length()+context.getResources().getString(R.string.charLeft);
            wordcount.setText(word);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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
