package com.ecommerce.customer.fypproject;

import android.Manifest;
import android.app.AlertDialog;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.customer.fypproject.adapter.Purchased;
import com.ecommerce.customer.fypproject.adapter.PurchasedAdapter;
import com.ecommerce.customer.fypproject.adapter.UploadProcess;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WritePostActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView txtUserName;
    private EditText postDescription;
    private ImageView userPic;
    private ImageView postPic1;
    private ImageView postPic2;
    private ImageView postPic3;
    private Button postBtn;

    private RecyclerView chooseItemrcv;

    private FirebaseAuth firebaseAuth;
    private HorizontalScrollView multiplePicview;

    private String PHPURL="https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    //String PHPURL="http://10.0.2.2/cashierbookPHP/Eric/customer_function.php";
    private List<Purchased> purchasedList;
    private PurchasedAdapter purchasedAdapter;
    private String uid, udisplayname;
    private RecyclerView.Adapter recyclerViewadapter;
    private RecyclerView.LayoutManager layoutManagerOfrecyclerView;
    private boolean isEmpty;
    private Integer img;
    private boolean check;
    private boolean[] remove= {false,false,false},checkimage ={false,false,false};
    private CharSequence[] items;
    private static final int RequestPermissionCode  = 1 ;
    private Bitmap[] bitmapSelect={null,null,null};
    private String mImageFileLocation;
    private Uri uris;
    private ArrayList<Bitmap> bitmapArray = new ArrayList<>();
    private ArrayList<Uri> ConvertImage= new ArrayList<>();
    private ProgressDialog progressDialog;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference custpost = db.collection("CustPost");
    private StorageReference mStorageRef;
    //private DatabaseReference mDatabaseRef;
    private String temppostID;
    //private CollectionReference postpic = db.collection("PostPic");
    private CollectionReference postpic = db.collection("CustPost");

    private Date c = Calendar.getInstance().getTime();
    private SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    private String currentTime = df.format(c);

    private int tempint = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        txtUserName = findViewById(R.id.user_name);
        postDescription = findViewById(R.id.posteditText);
        userPic = findViewById(R.id.user_pic);
        postPic1 = findViewById(R.id.postpic1);
        postPic2 = findViewById(R.id.postpic2);
        postPic3 = findViewById(R.id.postpic3);
        chooseItemrcv = findViewById(R.id.chooseitemrcv);
        multiplePicview = findViewById(R.id.multiplepostpic);
        postBtn = findViewById(R.id.btnPost);

        GetFirebaseAuth();

        mStorageRef = FirebaseStorage.getInstance().getReference("postpic");
        //mDatabaseRef = FirebaseDatabase.getInstance().getReference("postpic");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Toolbar myposttoolbar = findViewById(R.id.posttoolbar);
        setSupportActionBar(myposttoolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.createpost);

        txtUserName.setText(udisplayname);
        if (ContextCompat.checkSelfPermission(WritePostActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)

        postPic1.setOnClickListener(this);
        postPic2.setOnClickListener(this);
        postPic3.setOnClickListener(this);

        postBtn.setOnClickListener(this);

        chooseItemrcv.setHasFixedSize(true);
        layoutManagerOfrecyclerView = new LinearLayoutManager(this);
        chooseItemrcv.setLayoutManager(layoutManagerOfrecyclerView);

        JSON_HTTP_CALL();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri;
            switch (requestCode) {

                //After convert into bitmap then set into image view
                case 0:
                    try {
                        bitmapSelect[0] = grabImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    postPic1.setImageBitmap(bitmapSelect[0]);
                    remove[0] = false;
                    checkimage[0]=true;
                    break;
                case 1:
                    try {
                        bitmapSelect[1] = grabImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    postPic2.setImageBitmap(bitmapSelect[1]);
                    remove[1] = false;
                    checkimage[1]=true;
                    break;
                case 2:
                    try {
                        bitmapSelect[2] = grabImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    postPic3.setImageBitmap(bitmapSelect[2]);
                    remove[2] = false;
                    checkimage[2]=true;
                    break;
                case 10:
                    uri = data.getData();
                    if (uri != null) {
                        try {
                            bitmapSelect[0] = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        bitmapSelect[0] =(Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    }
                    postPic1.setImageBitmap(bitmapSelect[0]);
                    remove[0] = false;
                    checkimage[0]=true;
                    break;
                case 11:
                    uri = data.getData();
                    if (uri != null) {
                        try {
                            bitmapSelect[1] = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        bitmapSelect[1] =(Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    }
                    postPic2.setImageBitmap(bitmapSelect[1]);
                    remove[1] = false;
                    checkimage[1]=true;
                    break;
                case 12:
                    uri = data.getData();
                    if (uri != null) {
                        try {
                            bitmapSelect[2] = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        bitmapSelect[2] =(Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    }
                    postPic3.setImageBitmap(bitmapSelect[2]);
                    remove[2] = false;
                    checkimage[2]=true;
                    break;
            }
        }


    }

    //for order history
    private void JSON_HTTP_CALL() {

        purchasedList=new ArrayList<>();
        purchasedAdapter=null;

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                try {
                    if(response.equalsIgnoreCase("[]")){
                        isEmpty=false;
                    }else{
                        isEmpty=true;
                    }
                    ParseJSonResponse(response);
                } catch (JSONException e) {
                    //Crashlytics.logException(e);
                    // handle your exception here!
                    e.printStackTrace();
                }
                Log.d("Response", response);
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put("getPurchaseHistory", uid);
                String FinalData = ProcessClass.HttpRequest(PHPURL, HashMapParams);
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();

    }

    //get response string and set into recyclerView
    private void ParseJSonResponse(String array) throws JSONException {

        JSONArray jarr = new JSONArray(array);//lv 1 array

        int totalQty=0;
        for(int a=0;a<jarr.length();a++){
            Purchased x=new Purchased();
            JSONObject json;
            json=jarr.getJSONObject(a);

            x.setImgURL(json.getString("url"));
            x.setPurchasedItemName(json.getString("itemName"));
            x.setPurchasedDate(json.getString("orderdate"));

            purchasedList.add(x);
        }



        purchasedAdapter= new PurchasedAdapter(purchasedList, this);
        recyclerViewadapter =purchasedAdapter;
        chooseItemrcv.setAdapter(recyclerViewadapter);

    }

    @Override
    public void onClick(View view) {
        int i;
        switch (view.getId()) {
            case R.id.postpic1:
                i = 0;
                selectimage(i);
                break;
            case R.id.postpic2:
                i = 1;
                selectimage(i);
                break;
            case R.id.postpic3:
                i = 2;
                selectimage(i);
                break;
            case R.id.btnPost:
                check = false;
                if(postDescription.getText().toString().isEmpty()){
                    postDescription.setError("Field can't be empty!");
                    check = true;
                }
                if(bitmapSelect[0]!=null)
                    bitmapArray.add(bitmapSelect[0]);
                if(bitmapSelect[1]!=null)
                    bitmapArray.add(bitmapSelect[1]);
                if(bitmapSelect[2]!=null)
                    bitmapArray.add(bitmapSelect[2]);
                if(bitmapArray.isEmpty()){
                    check=true;
                    Toast.makeText(this, "Image is Required", Toast.LENGTH_SHORT).show();
                }
                if(!check){
                    try {
                        UploadToServerFunction();
                    } catch (Exception e) {
                        //Crashlytics.logException(e);
                        // handle your exception here!
                        e.printStackTrace();
                    }
                }
            default:
                break;
        }
    }

    private void UploadToServerFunction(){
        for(int a=0;a<bitmapArray.size();a++){
            ByteArrayOutputStream byteArrayOutputStreamObject;
            byteArrayOutputStreamObject = new ByteArrayOutputStream();

            // Converting bitmap image to jpeg format, so by default image will upload in jpeg format.
            bitmapArray.get(a).compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(),bitmapArray.get(a),"Title",null);
            ConvertImage.add(Uri.parse(path));
        }

        progressDialog = ProgressDialog.show(WritePostActivity.this,"Post is Uploading","Please Wait",false,false);

        Map<String,Object> post = new HashMap<>();
        post.put("userid", uid);
        post.put("username", udisplayname);
        post.put("postdescription", postDescription.getText().toString());
        post.put("postdate", currentTime);
        post.put("prodcode", null);
        post.put("imageUrl1", "none");
        post.put("imageUrl2", "none");
        post.put("imageUrl3", "none");
        custpost.add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                temppostID = documentReference.getId();

                for(int i=0; i<ConvertImage.size(); i++){
                    final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()+".jpg");

                    fileReference.putFile(ConvertImage.get(i)).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw Objects.requireNonNull(task.getException());
                            }
                            return fileReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            Uri downloadUri = task.getResult();

                            //PostImage postimg = new PostImage(downloadUri.toString(), temppostID);
                            //String uploadID = mDatabaseRef.push().getKey();
                            //mDatabaseRef.child(uploadID).setValue(postimg);
                            //postpic.add(postimg);
                            postpic.document(temppostID).update(
                                    "imageUrl" + tempint, downloadUri.toString()
                            );
                            tempint++;

                            progressDialog.dismiss();

                            Intent intent = new Intent(WritePostActivity.this,MainActivity.class);// New activity
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                            Toast.makeText(WritePostActivity.this,R.string.postuploaded,Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(WritePostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(WritePostActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.d("Message:", e.toString());
            }
        });

    }

    private void selectimage(int i){
        img = i;
        switch (img){
            //select_array with Remove image button
            case 0:
                if(checkimage[0]){
                    items= getResources().getStringArray(R.array.select_array);
                }else{
                    items= getResources().getStringArray(R.array.select_arraywithoutRemove);
                }
                break;
            case 1:
                if(checkimage[1]){
                    items= getResources().getStringArray(R.array.select_array);
                }else{
                    items= getResources().getStringArray(R.array.select_arraywithoutRemove);
                }
                break;
            case 2:
                if(checkimage[2]){
                    items= getResources().getStringArray(R.array.select_array);
                }else{
                    items= getResources().getStringArray(R.array.select_arraywithoutRemove);
                }
                break;

        }
        //Show dialog box for select camera,gallery,remove,cancel
        AlertDialog.Builder builder = new AlertDialog.Builder(WritePostActivity.this);
        builder.setTitle( "Add Image" );
        builder.setItems( items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(items[i].equals("Camera")){
                    if(!EnableRuntimePermission()){
                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        //Create a temp jpg file to store full size captured image
                        File photo=null;
                        try
                        {
                            // place where to store camera taken picture
                            photo = createTemporaryFile("temp", ".jpg");
                            photo.delete();
                        }
                        catch(Exception e)
                        {
                            Log.e("ImageCapture", e.toString());
                            Toast.makeText(WritePostActivity.this, "Please check SD card! Image shot is impossible!",Toast.LENGTH_LONG).show();
                        }
                        //File provider is to generate files URI and it is declare at manifest and xml folder
                        uris = FileProvider.getUriForFile(getApplicationContext(),"com.ecommerce.customer.fypproject.fileprovider", Objects.requireNonNull(photo));
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uris);
                        //start camera intent
                        startActivityForResult(Intent.createChooser(intent,"Select Image From Camera"),img);
                    }
                }else if(items[i].equals( "Gallery" )){
                    if(!EnableRuntimePermission()){
                    //Intent to open gallery to select image
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    img+=10;
                    startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), img);}
                }else if (items[i].equals("Remove")){
                    //remove images and set boolean for check any changes
                    switch (img){
                        case 0:
                            bitmapSelect[0] = null;
                            remove[0] = true;
                            checkimage[0]= false;
                            postPic1.setImageDrawable(getResources().getDrawable(R.drawable.photo));
                            break;
                        case 1:
                            bitmapSelect[1] = null;
                            remove[1] = true;
                            checkimage[1]= false;
                            postPic2.setImageDrawable(getResources().getDrawable(R.drawable.photo));
                            break;
                        case 2:
                            bitmapSelect[2] = null;
                            remove[2] = true;
                            checkimage[2]= false;
                            postPic3.setImageDrawable(getResources().getDrawable(R.drawable.photo));
                            break;
                        default:
                            break;
                    }
                }
                else if(items[i].equals( "Cancel" )){
                    dialog.dismiss();
                }
            }
        } );
        builder.show();
    }

    private boolean EnableRuntimePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        !=PackageManager.PERMISSION_GRANTED&& (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)) {
            // Permission is not granted, then request for permission
            ActivityCompat.requestPermissions( this,
                    new String[]{android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, RequestPermissionCode);
            return true;
        }else{
            return false;
        }
    }
    private File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdirs();
        }
        File image = File.createTempFile(part, ext, tempDir);
        mImageFileLocation = image.getAbsolutePath();
        return image;
    }

    //Convert the image file to bitmap
    private Bitmap grabImage() throws IOException {
        Uri uri = Uri.fromFile(new File(mImageFileLocation));
        return handleSamplingAndRotationBitmap(getApplicationContext(),uri);
    }

    private void GetFirebaseAuth(){
        firebaseAuth=FirebaseAuth.getInstance();//get firebase object
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this,R.string.sessionexp,Toast.LENGTH_LONG).show();
        }
        else{
            uid = firebaseAuth.getCurrentUser().getUid();
            udisplayname = firebaseAuth.getCurrentUser().getDisplayName();
        }
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
}
