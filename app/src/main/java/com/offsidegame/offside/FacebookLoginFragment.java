package com.offsidegame.offside;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;


public class FacebookLoginFragment extends Fragment {

    private TextView fbNameTextView;
    private ProfilePictureView fbPictureImageView;
    private CallbackManager mCallbackManager;
    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {

        private ProfileTracker mProfileTracker;
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();

            //fix issue profile return as null by using Profiletracker
            //http://stackoverflow.com/questions/29642759/profile-getcurrentprofile-returns-null-after-logging-in-fb-api-v4-0

            if(Profile.getCurrentProfile() == null) {
                mProfileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        // profile2 is the new profile
                        Log.v("facebook - profile", profile2.getFirstName());
                        fbNameTextView.setText(String.format("Welcome %1$s",profile2.getName()));
                        //Uri imageUri = profile2.getProfilePictureUri(50,50);
                        //fbPictureImageView.setImageURI(imageUri);
                        fbPictureImageView.setProfileId(Profile.getCurrentProfile().getId());
                        mProfileTracker.stopTracking();
                    }
                };
                // no need to call startTracking() on mProfileTracker
                // because it is called by its constructor, internally.
            }
            else {
                Profile profile = Profile.getCurrentProfile();
                fbNameTextView.setText(String.format("Welcome %1$s",profile.getName()));
                //Uri imageUri = profile.getProfilePictureUri(50,50);
                //fbPictureImageView.setImageURI(imageUri);
                fbPictureImageView.setProfileId(Profile.getCurrentProfile().getId());
                Log.v("facebook - profile", profile.getFirstName());

            }







        }

        @Override
        public void onCancel() {
            Log.v("facebook - onCancel", "cancelled");

        }

        @Override
        public void onError(FacebookException error) {
            Log.v("facebook - onError", error.getMessage());

        }
    };


    public FacebookLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_facebook_login, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        fbNameTextView = (TextView)view.findViewById(R.id.fbNameTextView);
        fbPictureImageView =(ProfilePictureView) view.findViewById(R.id.fbPictureImageView);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager,mCallback);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

}
