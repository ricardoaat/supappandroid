package com.brosoft.supapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.special.ResideMenu.ResideMenu;

import java.util.Arrays;
import java.util.List;


/**
 * Created by ricar on 2/06/2016.
 */
public class HomeFragment extends Fragment {
    private View parentView;
    private ResideMenu resideMenu;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private TextView txTitle;
    private TextView txProfileDetail;
    private ShareDialog shareDialog;
    //Facebook login button
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            updateProfile(profile);
            Toast.makeText(parentView.getContext(), "Login in", Toast.LENGTH_LONG).show();
            System.out.println("SUCCESS");
        }
        @Override
        public void onCancel() {
            Toast.makeText(parentView.getContext(), "On Cancel ", Toast.LENGTH_LONG).show();
            System.out.println("CANCEL");
        }
        @Override
        public void onError(FacebookException e) {
            Toast.makeText(parentView.getContext(), "Error "+e.getMessage(), Toast.LENGTH_LONG).show();
            System.out.println("ERROR");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.home, container, false);
        setUpViews();
        facebookInit();
        return parentView;
    }

    private void setUpViews() {
        MainActivity parentActivity = (MainActivity) getActivity();
        resideMenu = parentActivity.getResideMenu();
        shareDialog = new ShareDialog(this);

        parentView.findViewById(R.id.btn_open_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });

        parentView.findViewById(R.id.btn_post_fb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Grability Home")
                            .setImageUrl(Uri.parse("http://www.grability.com/wp-content/uploads/2016/06/Grability-Logo-Normal_rszd.png"))
                            .setContentDescription(
                                    "Grability Mobile Retail reinvented")
                            .setContentUrl(Uri.parse("http://www.grability.com"))
                            .build();
                    shareDialog.show(linkContent);  // Show facebook ShareDialog
                }
                /*
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://developers.facebook.com"))
                        .build();
                ShareApi.share(content,null);
                */
            }
        });

        // add gesture operation's ignored views
        FrameLayout ignored_view = (FrameLayout) parentView.findViewById(R.id.ignored_view);
        resideMenu.addIgnoredView(ignored_view);
        txTitle = (TextView) parentView.findViewById(R.id.txTitle);
        txProfileDetail = (TextView) parentView.findViewById(R.id.txProfileDetail);
    }


    private void facebookInit(){
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                Toast.makeText(parentView.getContext(), "Profile change", Toast.LENGTH_LONG).show();
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        LoginButton loginButton = (LoginButton)parentView.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        List<String> permissionNeeds = Arrays.asList("publish_actions");
        loginButton.setPublishPermissions(permissionNeeds);
        //loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, callback);
    }

    private void updateProfile(Profile profile){
        if(profile!=null){
            Toast.makeText(parentView.getContext(), "Profile: "+profile.getLastName(), Toast.LENGTH_LONG).show();
            txTitle.setText(profile.getFirstName());
            txProfileDetail.setText(profile.getId()+"\n"+profile.getLinkUri()+"\n"+profile.getLastName());
        } else {
            System.out.println("**********************************IS FUCKN NULL");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        updateProfile(profile);
    }
    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(parentView.getContext());
    }

    public void onStop() {
        super.onStop();
        //Facebook login
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        //Facebook login
        callbackManager.onActivityResult(requestCode, responseCode, intent);
    }
}
