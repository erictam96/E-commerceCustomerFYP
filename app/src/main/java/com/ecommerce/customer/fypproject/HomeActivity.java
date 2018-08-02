package com.ecommerce.customer.fypproject;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ecommerce.customer.fypproject.adapter.Comment;
import com.ecommerce.customer.fypproject.adapter.CommentAdapter;
import com.ecommerce.customer.fypproject.adapter.OnItemClick_2;
import com.ecommerce.customer.fypproject.adapter.Post;
import com.ecommerce.customer.fypproject.adapter.PostViewAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;

import javax.annotation.Nullable;

import static android.app.Activity.RESULT_OK;
import static com.ecommerce.customer.fypproject.adapter.PostViewAdapter.RequestPermissionCode;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.login.widget.ProfilePictureView.TAG;


public class HomeActivity extends Fragment implements OnItemClick_2{


    private Timer timer = new Timer();
    private List<Post> ListOfPost;
    private List<Comment> ListOfComment;

    private RecyclerView recyclerViewPost;
    private RecyclerView recyclerViewComment;
    final String HTTP_JSON_URL = "https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    //final String HTTP_JSON_URL = "http://10.0.2.2/cashierbookPHP/Eric/customer_function.php";
    boolean check1 = true;
    private boolean doneLoad=false;

    private RecyclerView.LayoutManager layoutManagerofPostView;
    private RecyclerView.Adapter postViewAdapter;
    private RecyclerView.LayoutManager layoutManagerofComment;
    private RecyclerView.Adapter commentAdapter;

    View view ;
    private ProgressBar progressBar;
    //int RecyclerViewItemPosition ;

    SnapHelper snapHelper;
    private int loadIndex=0;
    private PostViewAdapter ownRecycle;
    private FirebaseAuth firebaseAuth;
    private String uid,udisplayname,uemail;

    //testing purpose
    private Date c = Calendar.getInstance().getTime();
    private SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    String formattedDate = df.format(c);

    private Dialog commentDialog;
    private ImageView commentCamera,commentGallery,commentViewPic,commentRemove,commentPost;
    private EditText commentDesc;
    private View commentDivNoPic,commentDivPic,commentBackground;
    private ProgressDialog progressDialog;
    private ProgressBar commentProgressBar;
    private String postid,likeid;

    private Uri  mImageUri,ConvertImage;
    private Integer img=0;
    private String mImageFileLocation;
    private Boolean checkimage=false;
    private Bitmap bitmappic;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference commentRef = db.collection("Comment");
    private CollectionReference likeRef = db.collection("Like");
    private StorageReference mStorageRef;
    private String temppostID;
    private Uri downloadUri;
    private static final String KEY_NAME = "commentUsername";
    private static final String KEY_DESCRIPTION = "commentDesc";
    private static final String KEY_DATE = "commentDate";
    private static final String KEY_PIC = "commentImageUrl";

    private CollectionReference custpost = db.collection("CustPost");
    private CollectionReference socialuser = db.collection("SocialUser");
    private DocumentSnapshot lastResult;

    private  String tempUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_homepage, container, false);
        GetFirebaseAuth();
        Map<String,Object> userdata = new HashMap<>();
        userdata.put("username", udisplayname);
        userdata.put("useremail", uemail);
        socialuser.document(uid).set(userdata);

        mStorageRef = FirebaseStorage.getInstance().getReference("postpic");
        ListOfPost = new ArrayList<>();

        recyclerViewPost = rootView.findViewById(R.id.rcvHome3);
        recyclerViewPost.setHasFixedSize(true);

        //snapHelper.attachToRecyclerView(recyclerViewPost);

        progressBar = rootView.findViewById(R.id.progressBar);

        layoutManagerofPostView = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewPost.setLayoutManager(layoutManagerofPostView);
        loadIndex = 0;

        //JSON_HTTP_CALL();
        loadPost();

        recyclerViewPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)&&doneLoad) {

                    //Toast.makeText(FindActivity.this,"LAst",Toast.LENGTH_LONG).show();
                    loadIndex+=6;
                    //JSON_HTTP_CALL();
                    loadPost();

                }
            }
        });

        CommentDialog();

        return rootView;
    }


    /*private void JSON_HTTP_CALL() {
        ListOfPost = new ArrayList<>();
        doneLoad = false;

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(loadIndex==0) {
                    recyclerViewPost.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                if(response.equalsIgnoreCase("[]")){
                    //ownRecycle.refreshList();
                    recyclerViewPost.setVisibility(View.VISIBLE);
                }
                else {
                    try {
                        ParseJSonResponse(response);
                        recyclerViewPost.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put("searchKey","");
                HashMapParams.put("loadIndex",Integer.toString(loadIndex));
                HashMapParams.put("uid",uid);

                //HashMapParams.put("fetchPost",Integer.toString(loadIndex));   //localhost
                String FinalData = ProcessClass.HttpRequest(HTTP_JSON_URL, HashMapParams);
                return FinalData;
            }

        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();
    }

    private void ParseJSonResponse(String array) throws JSONException {
        JSONArray jarr = new JSONArray(array);

        ArrayClear();
        for(int i = 0; i<jarr.length(); i++) {

            //DataAdapter GetDataAdapter2 = new DataAdapter();
            Post post=new Post();
            JSONObject json = null;
            try {

                json = jarr.getJSONObject(i);

                post.setUsername(json.getString("shopname"));
                post.setPostDesc(json.getString("prodname"));
                post.setPostdate(formattedDate);
                post.setProfilepicUrl("R.drawable.user_icon");
                post.setPostpicUrl(json.getString("imageurl"));

                //post.setPostID(json.getInt("postid")); //added postid
                //post.setUsername(json.getString("displayname"));
                //post.setPostDesc(json.getString("postdesc"));
                //post.setPostdate(json.getString("postdate"));
                //post.setProfilepicUrl("R.drawable.user_icon");
                //post.setPostpicUrl(json.getString("imageurl"));

                post.setTotal_likes(100);
                post.setTotal_comment(100);

            } catch (JSONException e) {

            }
            ListOfPost.add(post);

            // ListOfdataAdapter.add(GetDataAdapter2);
        }
        doneLoad=true;
        if(loadIndex==0){
            ownRecycle = new PostViewAdapter(ListOfPost,this.getContext(),this);
            postViewAdapter = ownRecycle;
            recyclerViewPost.setAdapter(postViewAdapter);
        }else{
            //recyclerViewadapter.notifyItemRangeChanged(loadIndex,ListOfProduct.size());
            ownRecycle.addList(ListOfPost);
        }

    }*/

    private void ArrayClear() {
        ListOfPost.clear();
        ListOfComment.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer.purge();
    }

    private void GetFirebaseAuth(){
        firebaseAuth= FirebaseAuth.getInstance();//get firebase object
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent = new Intent(getActivity(), SplashScreenActivity.class);
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(getActivity(),R.string.sessionexp,Toast.LENGTH_LONG).show();
        }else {
            uid = firebaseAuth.getCurrentUser().getUid();
            udisplayname = firebaseAuth.getCurrentUser().getDisplayName();
            uemail = firebaseAuth.getCurrentUser().getEmail();
        }

    }

    @Override
    public void onClick(String value, String value2) {
        //value received
        if(value.equalsIgnoreCase("comment")){
            postid = value2;
            loadCommentRealtime(postid);
            commentDialog.show();
        }
        else if(value.equalsIgnoreCase("like")){
            postid = value2;
            Map<String,Object> like = new HashMap<>();
            like.put("likePostid",postid);
            like.put("likeUserid", uid);
            like.put("likeUsername",udisplayname);

            likeRef.add(like);
        }
        else if(value.equalsIgnoreCase("dislike")){
            postid = value2;
            likeRef.whereEqualTo("likePostid",postid)
                    .whereEqualTo("likeUserid",uid)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                        String templikeid = documentSnapshot.getId();
                        likeRef.document(templikeid).delete();
                    }
                }
            });
        }

    }

    private void CommentDialog() {
        commentDialog = new Dialog(Objects.requireNonNull(this.getContext()), R.style.MaterialDialogSheet);
        commentDialog.setContentView(R.layout.comment_layout); // your custom view.
        commentDialog.setCancelable(true);
        Objects.requireNonNull(commentDialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        commentDialog.getWindow().setGravity(Gravity.CENTER);

        commentCamera = commentDialog.findViewById(R.id.camera);
        commentPost = commentDialog.findViewById(R.id.post);
        commentDesc = commentDialog.findViewById(R.id.comment_bar);
        commentGallery = commentDialog.findViewById(R.id.gallery);
        commentViewPic = commentDialog.findViewById(R.id.view);
        commentRemove = commentDialog.findViewById(R.id.remove);
        commentRemove.setVisibility(View.GONE);
        commentViewPic.setVisibility(View.GONE);
        commentDivNoPic = commentDialog.findViewById(R.id.divider2);
        commentDivPic = commentDialog.findViewById(R.id.divider3);
        commentBackground = commentDialog.findViewById(R.id.whiteBackground);

        ListOfComment = new ArrayList<>();
        recyclerViewComment = commentDialog.findViewById(R.id.recyclerview);
        recyclerViewComment.setHasFixedSize(true);
        layoutManagerofComment = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewComment.setLayoutManager(layoutManagerofComment);


        commentCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!EnableRuntimePermission()) {
                    //Create a temporary file to store captured image
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File photo = null;
                    try {
                        // place where to store camera taken picture
                        photo = createTemporaryFile("temp", ".jpg");
                        photo.delete();
                    } catch (Exception e) {
                        Log.e("ImageCapture", e.toString());
                        Toast.makeText(getActivity(), "Please check SD card! Image shot is impossible!", Toast.LENGTH_LONG).show();
                    }

                    //File provider to generate files URI
                    mImageUri = FileProvider.getUriForFile(getApplicationContext(), "com.ecommerce.customer.fypproject.fileprovider", Objects.requireNonNull(photo));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                    //start camera intent
                    startActivityForResult(Intent.createChooser(intent, "Select Image From Camera"), img);
                }


            }
        });

        commentGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent Gallery to get images
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                img+=10;
                startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), img);

            }
        });

        commentRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmappic=null;
                editTextLayout("comment without photo");
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) commentDesc.getLayoutParams();
                params.setMargins(250, 0, 10, 0);
                commentDesc.setLayoutParams(params);
                checkimage=false;
                img = 0;
            }
        });

        commentDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                img = 0;
                commentDesc.getText().clear();
                commentDesc.setError(null);
                editTextLayout("comment without photo");
                if(checkimage){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) commentDesc.getLayoutParams();
                    params.setMargins(250, 0, 10, 0);
                    commentDesc.setLayoutParams(params);
                }
                checkimage=false;
            }
        });

        commentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCommentRealtime(postid);
                UploadToServerFunction();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(RegisterProductActivity.this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getActivity(),"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    private boolean EnableRuntimePermission(){
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        !=PackageManager.PERMISSION_GRANTED&& (ContextCompat.checkSelfPermission(
                getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)) {
            // Permission is not granted, then request for permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, RequestPermissionCode);
            return true;
        }else{
            return false;
        }

    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


    private static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        Objects.requireNonNull(imageStream).close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(img, selectedImage);
        return img;
    }

    //Get directory and create a temp jpg file
    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        Log.d("Directory",tempDir.toString());
        if(!tempDir.exists())
        {
            tempDir.mkdirs();
        }
        File image = File.createTempFile(part, ext, tempDir);
        mImageFileLocation = image.getAbsolutePath();
        return image;
    }

    //get the image file from URI then convert into bitmap
    private Bitmap grabImage() throws IOException {
        Uri uri = Uri.fromFile(new File(mImageFileLocation));
        return handleSamplingAndRotationBitmap(Objects.requireNonNull(getActivity()),uri);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri;
            // Uri uri = data.getData();
            switch (requestCode) {
                //set the bitmap into imageview
                case 0:
                    try {
                        bitmappic = grabImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    commentViewPic.setImageBitmap(bitmappic);
                    checkimage=true;
                    editTextLayout("comment with photo");

                    break;

                case 10:
                    uri = data.getData();
                    if (uri != null) {
                        try {

                            bitmappic = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    commentViewPic.setImageBitmap(bitmappic);
                    checkimage=true;
                    editTextLayout("comment with photo");
                    break;

            }

        }

    }

    //change the layout of comment bar
    private void editTextLayout(String value){
        if(value.equalsIgnoreCase("comment with photo")) {
            commentViewPic.setVisibility(View.VISIBLE);
            commentRemove.setVisibility(View.VISIBLE);
            commentDivPic.setVisibility(View.VISIBLE);
            commentDivNoPic.setVisibility(View.INVISIBLE);
            commentCamera.setVisibility(View.GONE);
            commentGallery.setVisibility(View.GONE);
            commentBackground.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) commentDesc.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(20, 0, 20, 0);
            commentDesc.setLayoutParams(params); //causes layout update
        }

        else if(value.equalsIgnoreCase("comment without photo")) {
            commentViewPic.setVisibility(View.GONE);
            commentRemove.setVisibility(View.GONE);
            commentCamera.setVisibility(View.VISIBLE);
            commentGallery.setVisibility(View.VISIBLE);

        }
    }

    //Calculate date
    private String dateCalculation(Date date1, Date date2) {
        String time;

        long different = date1.getTime() - date2.getTime();

        //1 minute = 60 seconds
        //1 hour = 60 x 60 = 3600
        //1 day = 3600 x 24 = 86400
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if(elapsedDays!=0) {
            if(elapsedDays<7) {
                time = Long.toString(elapsedDays) + " Day";
            }
            else if(elapsedDays<365) {
                time = Integer.toString((int)elapsedDays/7) + " Week";
            }
            else{
                time = Integer.toString((int)elapsedDays/365) + " Yr";
            }

        }
        else if(elapsedHours!=0){
            time = Long.toString(elapsedHours) + " Hr";
        }
        else if(elapsedMinutes!=0){
            time = Long.toString(elapsedMinutes) + " Min";
        }
        else{
            time = "Just Now";
        }

        //Log.d("hihihihi", "days\n" + elapsedDays + "Hrs\n" + elapsedHours + "Min\n" + elapsedMinutes + "Sec\n" + elapsedSeconds);

        return time;
    }

    private void loadPost(){
        ListOfPost = new ArrayList<>();
        doneLoad = false;
        progressBar.setVisibility(View.VISIBLE);
        final Query query;
        if(lastResult == null) {
            query = custpost.orderBy("postdate", Query.Direction.DESCENDING).limit(6);
        } else{
            query = custpost.orderBy("postdate", Query.Direction.DESCENDING).startAfter(lastResult).limit(6);
        }

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressBar.setVisibility(View.GONE);
                        ArrayClear();
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Post post = new Post();

                            post.setPostpicUrl(Objects.requireNonNull(documentSnapshot.get("imageUrl1")).toString());
                            post.setPostDesc(Objects.requireNonNull(documentSnapshot.get("postdescription")).toString());
                            post.setPostdate(Objects.requireNonNull(documentSnapshot.get("postdate")).toString());
                            post.setProfilepicUrl("R.drawable.user_icon");
                            post.setUsername(Objects.requireNonNull(documentSnapshot.get("username")).toString());
                            post.setPostID(documentSnapshot.getId());
                            post.setUserid(Objects.requireNonNull(documentSnapshot.get("userid")).toString());

                            ListOfPost.add(post);
                        }

                        if(queryDocumentSnapshots.size() > 0) {
                            lastResult = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);

                            doneLoad = true;
                            if (loadIndex == 0) {
                                ownRecycle = new PostViewAdapter(ListOfPost, HomeActivity.this.getContext(),HomeActivity.this);
                                postViewAdapter = ownRecycle;
                                recyclerViewPost.setAdapter(postViewAdapter);
                            } else {
                                ownRecycle.addList(ListOfPost);
                            }
                        } else {
                            //ownRecycle.refreshList();
                            doneLoad = false;
                            Toast.makeText(getActivity(), "No more post", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"No post show", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void loadCommentRealtime(String postid) {
        ListOfComment.clear();
        commentRef.whereEqualTo("commentPostid",postid)
                .addSnapshotListener(Objects.requireNonNull(this.getActivity()), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    return;
                }
                ListOfComment.clear();
                for(DocumentSnapshot documentSnapshot : Objects.requireNonNull(queryDocumentSnapshots)){
                    Comment comment = new Comment();
                    comment.setCommentUsername(documentSnapshot.getString(KEY_NAME));
                    comment.setCommentDesc(documentSnapshot.getString(KEY_DESCRIPTION));
                    comment.setCommentDate(documentSnapshot.getString(KEY_DATE));
                    comment.setCommentPicUrl(documentSnapshot.getString(KEY_PIC));

                    try{
                        Date d = Calendar.getInstance().getTime();
                        Date date = df.parse(documentSnapshot.getString(KEY_DATE));
                        comment.setCommentDate(dateCalculation(d,date));
                    }
                    catch(ParseException ex){
                        ex.printStackTrace();
                    }

                    ListOfComment.add(comment);
                }
                commentAdapter = new CommentAdapter(ListOfComment,getContext());
                commentAdapter.notifyDataSetChanged();
                recyclerViewComment.setAdapter(commentAdapter);
            }
        });

    }

    private void UploadToServerFunction() {

        if(!commentDesc.getText().toString().isEmpty()) {
            progressDialog = ProgressDialog.show(getContext(),"","Comment Posting...",false,false);
            //commentProgressBar.setVisibility(View.VISIBLE);

            Map<String, Object> comment = new HashMap<>();
            comment.put("commentUserId", uid);
            comment.put("commentUsername", udisplayname);
            comment.put("commentPostid", postid);
            comment.put("commentDesc", commentDesc.getText().toString());

            Date t = Calendar.getInstance().getTime();
            String time = df.format(t);
            comment.put("commentDate", time);
            comment.put("commentProfilePicUrl", null);

            commentRef.add(comment)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            temppostID = documentReference.getId();
                            commentDesc.getText().clear();

                            ByteArrayOutputStream byteArrayOutputStreamObject;
                            byteArrayOutputStreamObject = new ByteArrayOutputStream();

                            if(checkimage) {
                                editTextLayout("comment without photo");
                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) commentDesc.getLayoutParams();
                                params.setMargins(250, 0, 10, 0);
                                commentDesc.setLayoutParams(params);
                                img = 0;

                                // Converting bitmap image to jpeg format, so by default image will upload in jpeg format.
                                bitmappic.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
                                String path = MediaStore.Images.Media.insertImage(Objects.requireNonNull(getActivity()).getContentResolver(), bitmappic, "Title", null);
                                ConvertImage = Uri.parse(path);

                                final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + ".jpg");

                                fileReference.putFile(ConvertImage).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            throw Objects.requireNonNull(task.getException());
                                        }
                                        return fileReference.getDownloadUrl();

                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        Uri downloadUri = task.getResult();

                                        commentRef.document(temppostID).update("commentImageUrl", downloadUri.toString());
                                        progressDialog.dismiss();
                                        //commentProgressBar.setVisibility(View.INVISIBLE);

                                        Toast.makeText(getActivity(), "Comment uploaded", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else{
                                commentRef.document(temppostID).update("commentImageUrl", null);
                                //commentProgressBar.setVisibility(View.INVISIBLE);
                                progressDialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    Log.d("Message:", e.toString());
                }
            });

        } else { commentDesc.setError("Field can't be empty."); }
    }

    //Retrive comment from Firestore Db
    public void loadComment(){
        ListOfComment.clear();
        commentRef.whereEqualTo("commentPostid",postid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Comment comment = new Comment();
                            comment.setCommentUsername(documentSnapshot.getString(KEY_NAME));
                            comment.setCommentDesc(documentSnapshot.getString(KEY_DESCRIPTION));
                            comment.setCommentDate(documentSnapshot.getString(KEY_DATE));

                            try{
                                Date d = Calendar.getInstance().getTime();
                                Date date = df.parse(documentSnapshot.getString(KEY_DATE));
                                //Date date = df.parse("25-Jun-2016 23:05:36");
                                comment.setCommentDate(dateCalculation(d,date));
                            }
                            catch(ParseException e){
                                e.printStackTrace();
                            }

                            ListOfComment.add(comment);
                        }
                        //commentAdapter = new CommentAdapter(ListOfComment,getContext());
                        recyclerViewComment.setAdapter(commentAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

    }

    private void UploadToServerFunction2() {

        if (!commentDesc.getText().toString().isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            ByteArrayOutputStream byteArrayOutputStreamObject;
            byteArrayOutputStreamObject = new ByteArrayOutputStream();

            if (checkimage) {
                editTextLayout("comment without photo");
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) commentDesc.getLayoutParams();
                params.setMargins(250, 0, 10, 0);
                commentDesc.setLayoutParams(params);
                img = 0;

                // Converting bitmap image to jpeg format, so by default image will upload in jpeg format.
                bitmappic.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
                String path = MediaStore.Images.Media.insertImage(Objects.requireNonNull(getActivity()).getContentResolver(), bitmappic, "Title", null);
                ConvertImage = Uri.parse(path);

                final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + ".jpg");

                fileReference.putFile(ConvertImage).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return fileReference.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri downloadUri = task.getResult();

                        Map<String, Object> comment = new HashMap<>();
                        comment.put("commentUserId", uid);
                        comment.put("commentUsername", udisplayname);
                        comment.put("commentPostid", postid);
                        comment.put("commentDesc", commentDesc.getText().toString());
                        commentDesc.getText().clear();

                        Date t = Calendar.getInstance().getTime();
                        String time = df.format(t);
                        comment.put("commentDate", time);
                        comment.put("commentProfilePicUrl", null);
                        comment.put("commentImageUrl", downloadUri.toString());

                        commentRef.add(comment);
                        checkimage=false;
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), R.string.postuploaded, Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Map<String, Object> comment = new HashMap<>();
                comment.put("commentUserId", uid);
                comment.put("commentUsername", udisplayname);
                comment.put("commentPostid", postid);
                comment.put("commentDesc", commentDesc.getText().toString());
                commentDesc.getText().clear();

                Date t = Calendar.getInstance().getTime();
                String time = df.format(t);
                comment.put("commentDate", time);
                comment.put("commentProfilePicUrl", null);
                comment.put("commentImageUrl", null);
                commentRef.add(comment);
                checkimage=false;

            }
        } else {
            commentDesc.setError("Field can't be empty.");
        }

    }

    //@Override
    //public void callback(View view) {
        //commentProgressBar = view.findViewById(R.id.progressbar);
    //}
}

