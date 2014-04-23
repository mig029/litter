package com.migliori.litter.start;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by macbook on 4/22/14.
 */ public  class PlaceholderFragment extends Fragment {
    private static Twitter twitter;
    Button twitFeed;
    Button updateStatus;
    EditText status;
    Thread thread;
    public static boolean loginState = false;
    private static SharedPreferences mSharedPreferences;
    public  EditText statusText;
    Bitmap image;
    Button login;

    FragmentTransaction ft;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {

        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;

    }

    public PlaceholderFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        if(Consts.login == false)
            loginToTwitter();

        login = (Button) getActivity().findViewById(R.id.loginButton);


        // TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        login = (Button) getActivity().findViewById(R.id.loginButton);
        if(Consts.login == true) {
            login.setText(R.string.logout);
        }
        if(Consts.login == false) {
            login.setText(R.string.login);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Consts.login == false)
                    loginToTwitter();
                else {
                    Toast.makeText(getActivity(), "D'oh", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
*/

    private void loginToTwitter() {
        GetRequestTokenTask getRequestTokenTask = new GetRequestTokenTask();
        getRequestTokenTask.execute();
        loginState = true;
    }

    private class GetRequestTokenTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            twitter = TwitterFactory.getSingleton();
            twitter.setOAuthConsumer(
                    getString(R.string.TWITTER_CONSUMER_KEY),
                    getString(R.string.TWITTER_CONSUMER_SECRET));
            try {
                RequestToken requestToken = twitter.getOAuthRequestToken(
                        getString(R.string.TWITTER_CALLBACK_URL));
                launchLoginWebView(requestToken);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void launchLoginWebView(RequestToken requestToken) {
        Intent intent = new Intent(getActivity(), LoginToTwitter.class);
        intent.putExtra(Consts.AUTHENTICATION_URL_KEY, requestToken.getAuthenticationURL());
        startActivityForResult(intent, Consts.LOGIN_TO_TWITTER_REQUEST);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Consts.LOGIN_TO_TWITTER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                getAccessToken(data.getStringExtra(LoginToTwitter.CALLBACK_URL_KEY));
            }
        }
    }


    private void getAccessToken(String callbackUrl) {
        Uri uri = Uri.parse(callbackUrl);
        String verifier = uri.getQueryParameter("oauth_verifier");
        GetAccessTokenTask getAccessTokenTask = new GetAccessTokenTask();
        getAccessTokenTask.execute(verifier);
    }


    private class GetAccessTokenTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String verifier = strings[0];
            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(verifier);
                Log.d(MainActivity.class.getSimpleName(), accessToken.getToken());
                Consts.login = true;
            } catch (Exception e) {

            }
            return null;
        }
    }
}




