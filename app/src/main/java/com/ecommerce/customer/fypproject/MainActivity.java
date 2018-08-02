package com.ecommerce.customer.fypproject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private EditText searchTextBut;
    private ImageButton searchButton;
    private ShoppingCartActivity tab3;
    private ProfileActivity tab4;
    private Button defaultButton;
    private String uid;
    private  boolean doneCount;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private FloatingActionButton fabpost,fabfriend;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar contenttoolbar = findViewById(R.id.contenttoolbar);
        setSupportActionBar(contenttoolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        GetFirebaseAuth();
        TabLayout tab = findViewById(R.id.tabs);
        //tab.set
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        searchTextBut = findViewById(R.id.searchTextBut);
        searchTextBut.setVisibility(View.VISIBLE);
        searchTextBut.requestFocus();
        searchTextBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentSearch();
            }
        });


        searchButton = findViewById(R.id.searchButton);
        searchButton.setVisibility(View.VISIBLE);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentSearch();
            }
        });

        //Log.e("Token",FirebaseInstanceId.getInstance().getToken());

        @SuppressLint("CutPasteId") final TabLayout tabLayout = findViewById(R.id.tabs);


        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        // HomeActivity tab1=new HomeActivity();
                        getSupportActionBar().setTitle("");
                        searchTextBut.setVisibility(View.VISIBLE);
                        searchButton.setVisibility(View.VISIBLE);
                        fabpost.setVisibility(View.VISIBLE);
                        fabfriend.setVisibility(View.VISIBLE);
                        searchTextBut.requestFocus();

                        break;
                    //return tab1;
                    case 1:
                        // NotificationActivity tab2=new NotificationActivity();
                        getSupportActionBar().setTitle(R.string.category3);
                        searchTextBut.setVisibility(View.GONE);
                        searchButton.setVisibility(View.GONE);
                        fabpost.setVisibility(View.GONE);
                        fabfriend.setVisibility(View.GONE);
//                        try{
//                            tab2.JSON_HTTP_CALL();
//                        }catch (Exception e){
//                            tab2 = new NotificationActivity();
//                        }

                        break;
                    // return tab2;
                    case 2:
                        //AboutUsActivity tab3=new AboutUsActivity();
                        getSupportActionBar().setTitle(R.string.shopcart);
                        searchTextBut.setVisibility(View.GONE);
                        searchButton.setVisibility(View.GONE);
                        fabpost.setVisibility(View.GONE);
                        fabfriend.setVisibility(View.GONE);
                        try{
                            tab3.JSON_HTTP_CALL();
                        }catch (Exception e){
                            tab3 = new ShoppingCartActivity();
                        }

                        break;
                    // return tab3;
                    case 3:
                        //ProfileActivity tab4=new ProfileActivity();
                        getSupportActionBar().setTitle(R.string.profile);
                        searchTextBut.setVisibility(View.GONE);
                        searchButton.setVisibility(View.GONE);
                        fabpost.setVisibility(View.GONE);
                        fabfriend.setVisibility(View.GONE);

                        try{
                            tab4.JSON_HTTP_CALL();
                        }catch (Exception e){
                            tab4=new ProfileActivity();
                        }


                        break;
                    //return tab4;
                    default:
                        getSupportActionBar().setTitle("Home");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fabpost = findViewById(R.id.fabPost);
        fabpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WritePostActivity.class);
                startActivity(intent);
            }
        });
        fabfriend = findViewById(R.id.fabFriend);
        fabfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewFriends.class);
                startActivity(intent);
            }
        });


        showTarcUserOnlyNotice();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.notification_icon) {
            Intent myIntent5 = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(myIntent5);
        }else if (item.getItemId()==R.id.chat_icon){
            Intent myIntent5 = new Intent(MainActivity.this, ChatListActivity.class);
            startActivity(myIntent5);
        }
        return mToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_icon, menu);
        getMenuInflater().inflate(R.menu.notification_icon, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.exitMsg)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);

                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    //Call random 6 item from server


    //call intent to open item detail activity

    class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    //tab1.getActivity().setTitle("Home");
                    return new HomeActivity();
                case 1:
                    return new CategoryActivity();
                case 2:
                     tab3 = new ShoppingCartActivity();
                    // tab3.getActivity().setTitle("Cart");

                    return tab3;
                case 3:
                    tab4 = new ProfileActivity();
                    //tab4.getActivity().setTitle("Profile");
                    return tab4;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:

                    return getResources().getString(R.string.home);
                case 1:
                    return getResources().getString(R.string.category2);
                case 2:

                    return getResources().getString(R.string.shopcart);
                case 3:

                    return getResources().getString(R.string.profile);
                default:
                    return null;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            int index = super.getItemPosition(object);

            if (index == 2){
                return POSITION_NONE;
            }else {
                return super.getItemPosition(object);
            }


        }

    }
    private void showTarcUserOnlyNotice(){
        View mView = getLayoutInflater().inflate(R.layout.alert_taruseronly, null);
        final CheckBox mCheckBox = mView.findViewById(R.id.chkTarcUserOnly);
        TextView TandC=mView.findViewById(R.id.TandClink);
        TextView privacyPolicy=mView.findViewById(R.id.privacyPolicyLink);
        AlertDialog mBuilder = new AlertDialog.Builder(this)
                .setIcon(R.drawable.error)
                .setTitle(getResources().getString(R.string.importantNotice))
                .setMessage(getResources().getString(R.string.importantNoticeMsg))
                .setCancelable(false)
                .setView(mView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if(!getTarcUserOnlyAlertStatus()){
                            finish();
                        }
                    }
                })
                .create();
        mBuilder.setOnShowListener(new DialogInterface.OnShowListener() {
                                       private static final int AUTO_DISMISS_MILLIS = 8000;

                                       @Override
                                       public void onShow(final DialogInterface dialog) {
                                           defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                           defaultButton.setEnabled(false);
                                           final CharSequence positiveButtonText = defaultButton.getText();
                                           new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                                               @Override
                                               public void onTick(long millisUntilFinished) {
                                                   defaultButton.setText(String.format(
                                                           Locale.getDefault(), "%s (%d)",
                                                           positiveButtonText,
                                                           TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)  //add one so it never displays zero

                                                   ));
                                                   doneCount=false;
                                               }

                                               @Override
                                               public void onFinish() {
                                                   if (((AlertDialog) dialog).isShowing()) {
                                                       defaultButton.setText(getResources().getString(R.string.ok));
                                                       doneCount=true;
                                                       //defaultButton.setEnabled(true);
                                                   }
                                                   if(mCheckBox.isChecked()&&doneCount){
                                                       defaultButton.setEnabled(true);
                                                       storeDialogStatus(true);
                                                   }
                                               }
                                           }.start();
                                       }
                                   });
        mBuilder.show();

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()&&doneCount){
                    defaultButton.setEnabled(true);
                    storeDialogStatus(true);
                }else{
                    defaultButton.setEnabled(false);
                    storeDialogStatus(false);
                    //finish();
                }
            }
        });
        if(getTarcUserOnlyAlertStatus()){
            mBuilder.hide();
        }else{
            mBuilder.show();
        }

        TandC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, TermOfServicesActivity.class);
                startActivity(myIntent);

            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, PrivacyPolicy.class);
                startActivity(myIntent);
            }
        });
    }

    //Store preference of the alert dialog status
    private void storeDialogStatus(boolean isChecked){
        SharedPreferences mSharedPreferences = getSharedPreferences("TarcUserOnly", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putBoolean("status", isChecked);
        mEditor.putString("email",uid);
        mEditor.apply();
    }

    //Check Preference whether to show the alert message for only TAR user
    private boolean getTarcUserOnlyAlertStatus() {
        SharedPreferences mSharedPreferences = getSharedPreferences("TarcUserOnly", MODE_PRIVATE);
        String user = mSharedPreferences.getString("email", "");

        return uid.equalsIgnoreCase(user) && mSharedPreferences.getBoolean("status", false);


    }

    private void intentSearch(){
        Intent myIntent2 = new Intent(MainActivity.this, FindActivity.class);
        myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(myIntent2,123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 123:{
                if (resultCode == RESULT_OK && null != data) {
                    String newText = data.getStringExtra("checkout");
                    if(newText.equalsIgnoreCase("111")){
                        mViewPager.setCurrentItem(2);
                    }
                }
            }
        }
    }

    private void GetFirebaseAuth(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent = new Intent(MainActivity.this, SplashScreenActivity.class);
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(MainActivity.this,R.string.sessionexp,Toast.LENGTH_LONG).show();
        }else uid = firebaseAuth.getCurrentUser().getUid();
    }
}



