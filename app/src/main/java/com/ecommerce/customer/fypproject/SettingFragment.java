package com.ecommerce.customer.fypproject;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by Eric on 22-Nov-17.
 */

public class SettingFragment extends PreferenceFragment {
    private String versionName = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_screen);

        GetFirebaseAuth();

        Preference version = findPreference("version_key");
        Preference Feedback = findPreference("feedback_key");
        Preference Logout = findPreference("logout_key");
        Preference About = findPreference("about_key");
        Preference ChangePassword = findPreference("change_password_key");
        Preference PrivacyPolicy = findPreference("privacypolicy_key");
        Preference TOS = findPreference("tos_key");
        Preference ContactUs=findPreference("contact_key");
        
        try {
            PackageInfo packageInfo = getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version.setSummary(versionName);

        ChangePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent myIntent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(myIntent);
                return true;
            }
        });

        version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getActivity(), versionName, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        Feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent myIntent = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(myIntent);
                return true;
            }
        });

        Logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                String topic= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
                FirebaseAuth.getInstance().signOut();

                try {
                        FirebaseInstanceId.getInstance().deleteInstanceId();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // user is now signed out
                    Intent myIntent = new Intent(getActivity(), SplashScreenActivity.class);
                    myIntent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myIntent);
                return true;
            }
        });

        About.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent myIntent = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(myIntent);
                return true;
            }
        });

        PrivacyPolicy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent myIntent = new Intent(getActivity(), PrivacyPolicy.class);
                startActivity(myIntent);
                return true;
            }
    });
        TOS.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent myIntent = new Intent(getActivity(), TermOfServicesActivity.class);
                startActivity(myIntent);
                return true;
            }
        });
        ContactUs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent myIntent = new Intent(getActivity(), ContactUsActivity.class);
                startActivity(myIntent);
                return true;
            }
        });
    }


    private void GetFirebaseAuth(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent = new Intent(getActivity().getApplicationContext(), SplashScreenActivity.class);
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(getActivity(),R.string.sessionexp,Toast.LENGTH_LONG).show();
        }
    }
}
