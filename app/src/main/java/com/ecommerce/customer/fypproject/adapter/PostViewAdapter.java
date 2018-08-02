package com.ecommerce.customer.fypproject.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ecommerce.customer.fypproject.R;
import com.ecommerce.customer.fypproject.ReviewActivity;
import com.ecommerce.customer.fypproject.ViewUserProfile;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.List;
import java.util.Objects;

public class PostViewAdapter extends RecyclerView.Adapter<PostViewAdapter.ViewHolder> {

    private Context context;
    private Dialog shareDialog;
    private ImageView shareFB,shareTwitter,shareInsta;
    private boolean[] temp;
    private TextView shareShareTo;
    private CallbackManager callbackManager;
    private ShareDialog fbsharedialog;

    public  static final int RequestPermissionCode  = 1 ;

    private List<Post> dataAdapters;
    private OnItemClick_2 mCallback;



    public PostViewAdapter(List<Post> getDataAdapter, Context context, OnItemClick_2 listener){

        super();
        this.dataAdapters = getDataAdapter;
        this.context = context;
        this.mCallback = listener;
       // liked = new boolean[dataAdapters.size()];
        temp = new boolean[dataAdapters.size()];
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_card_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        //ViewCommentDialog();
        ViewShareDialog();

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final PostViewAdapter.ViewHolder Viewholder, final int position) {

        final Post dataAdapterOBJ = dataAdapters.get(position);

        Glide.with(context)
                .load(dataAdapterOBJ.getProfilepicUrl()) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.profile_pic_icon) // any placeholder to load at start
                        .error(R.drawable.profile_pic_icon)  // any image in case of error
                        .override(200, 200) // resizing
                        .centerCrop())
                .into(Viewholder.postUserPic);

        Viewholder.postUserName.setText(dataAdapterOBJ.getUsername());

        Glide.with(context)
                .load(dataAdapterOBJ.getPostpicUrl()) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.photo) // any placeholder to load at start
                        .error(R.drawable.photo)  // any image in case of error
                        .override(200, 200) // resizing
                        .centerCrop())
                .into(Viewholder.postProductPic);

        Viewholder.postDate.setText(dataAdapterOBJ.getPostdate());
        Viewholder.postDetail.setText(dataAdapterOBJ.getPostDesc());
        Viewholder.totalLikes.setText(dataAdapterOBJ.getTotal_likes()+" likes");
        Viewholder.totalComments.setText(dataAdapterOBJ.getTotal_comment()+" comments");

        Viewholder.postSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postID = dataAdapterOBJ.getPostID();
                Intent intent = new Intent(context, ReviewActivity.class);
                intent.putExtra("postID", postID);
                /*Bundle b = new Bundle();
                b.putInt("key", 1); //Your id
                intent.putExtras(b); //Put your id to your next Intent*/

                ((Activity) context).startActivityForResult(intent, 101);
            }
        });

        Viewholder.postUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid = dataAdapterOBJ.getUserid();
                Intent intent = new Intent(context, ViewUserProfile.class);
                intent.putExtra("userID", userid);
                context.startActivity(intent);
            }
        });

        Viewholder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postID=dataAdapterOBJ.getPostID();
                mCallback.onClick("comment",postID);
                Log.d("Seeee ID","postID"+postID);
            }
        });

        Viewholder.likeBtn.setPressed(dataAdapterOBJ.isLiked());

            if(dataAdapterOBJ.isLiked()){
                Viewholder.likeBtn.setText(R.string.liked);
                Viewholder.likeBtn.setBackgroundColor(Color.GRAY);

            }
            else {
                Viewholder.likeBtn.setText(R.string.like);
                Viewholder.likeBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.com_facebook_button_background));

            }

        Viewholder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataAdapterOBJ.setLiked(Viewholder.likeBtn.isPressed());
                //liked[position] = Viewholder.likeBtn.isPressed();
                //temp[position] = liked[position];

                if (dataAdapterOBJ.isLiked()) {
                    if(Viewholder.likeBtn.getText().toString().equals(context.getString(R.string.like))){
                        Viewholder.likeBtn.setText(R.string.liked);
                        Viewholder.likeBtn.setBackgroundColor(Color.GRAY);
                        //postID=dataAdapterOBJ.getPostID();
                        //mCallback.onClick("like",postID);
                        //Log.d("Seeee ID like","postID"+postID);

                    }
                    else if(Viewholder.likeBtn.getText().toString().equals(context.getString(R.string.liked))){
                        Viewholder.likeBtn.setText(R.string.like);
                        Viewholder.likeBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.com_facebook_button_background));
                        //postID=dataAdapterOBJ.getPostID();
                        //mCallback.onClick("dislike",postID);
                        //Log.d("Seeee ID dislike","postID"+postID);
                    }
                }
                //Log.d("Result", "the position" + position);
            }

        });

        Viewholder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {

        return dataAdapters.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView postUserName;
        TextView postDetail;
        TextView postDate;
        TextView postSeeMore;
        TextView totalLikes;
        TextView totalComments;
        ImageView postUserPic;
        ImageView postProductPic;
        Button likeBtn;
        Button commentBtn;
        Button shareBtn;

        ViewHolder(View itemView) {

            super(itemView);
            postUserName = itemView.findViewById(R.id.post_user_name) ;
            postDetail = itemView.findViewById(R.id.post_content) ;
            postDate = itemView.findViewById(R.id.post_date);
            postSeeMore = itemView.findViewById(R.id.post_see_more);
            postUserPic = itemView.findViewById(R.id.post_profile_pic);
            postProductPic =  itemView.findViewById(R.id.post_pic) ;
            likeBtn = itemView.findViewById(R.id.like_button);
            commentBtn = itemView.findViewById(R.id.comment_button);
            shareBtn = itemView.findViewById(R.id.share_button);
            totalLikes = itemView.findViewById(R.id.totalLikes);
            totalComments = itemView.findViewById(R.id.totalComments);

        }
    }

    private void ViewShareDialog(){
        shareDialog=new Dialog(context,R.style.MaterialDialogSheet);
        shareDialog.setContentView(R.layout.share_layout);
        shareDialog.setCancelable(true);
        Objects.requireNonNull(shareDialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        shareDialog.getWindow().setGravity(Gravity.CENTER);

        shareShareTo=shareDialog.findViewById(R.id.shareTo);
        shareFB=shareDialog.findViewById(R.id.fb);
        shareTwitter=shareDialog.findViewById(R.id.twitter);
        shareInsta=shareDialog.findViewById(R.id.insta);

        callbackManager = CallbackManager.Factory.create();
        fbsharedialog = new ShareDialog((Activity) context);

        shareFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("www.cashierbook.com"))
                        .build();
                    if(ShareDialog.canShow(ShareLinkContent.class)){
                        fbsharedialog.show(linkContent);
                    }
            }
        });

        Answers.getInstance().logShare(new ShareEvent()
                .putMethod("Facebook")
                .putContentName("NA")
                .putContentType("ProductPost")
                .putContentId("NA"));
    }

    private String postID;
    private String username;

    public void addList(List<Post> dataAdapter){
        dataAdapters.addAll(dataAdapter);
        notifyItemRangeChanged(0,dataAdapters.size());
       // liked = new boolean[dataAdapters.size()];
        //ystem.arraycopy(temp,0,liked,0,temp.length);
        //temp = new boolean[dataAdapters.size()];
    }
    public void refreshList(){
        notifyItemRangeChanged(0,dataAdapters.size());
       // liked = new boolean[dataAdapters.size()];
        //System.arraycopy(temp,0,liked,0,temp.length);
        //temp = new boolean[dataAdapters.size()];
    }
    public List<Post>getListData(){
        return dataAdapters;
    }

}
