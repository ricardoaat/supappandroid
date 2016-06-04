package com.brosoft.supapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ricar on 2/06/2016.
 */
public class ProfileFragment extends Fragment {
    // Progress Dialog Object
    ProgressDialog prgDialog;
    private TextView textField;
    private View parentView;
    private Button callApiButton;
    private EditText idTicketField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.profile, container, false);
        textField = (TextView)parentView.findViewById(R.id.textView2);
        callApiButton = (Button) parentView.findViewById(R.id.button);
        idTicketField = (EditText) parentView.findViewById(R.id.eTticketId);

        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(parentView.getContext());
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        initView();
        return parentView;
    }

    private void initView(){
        callApiButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String ticketId = "/"+idTicketField.getText();
                String url = "http://192.168.1.101:8000/api/v1/tickets"+ticketId;
                invokeWS(url);
                Toast.makeText(getActivity(), "Brought API "+url, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Method that performs RESTful webservice invocations
     *
     */
    public void invokeWS(String url){
        // Show Progress Dialog
        prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept","application/json");
        client.get(url,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                prgDialog.hide();
                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(new String(responseBody));
                    textField.setText(obj.getString("subject"));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(parentView.getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Hide Progress Dialog
                prgDialog.hide();
                textField.setText(error.getMessage());
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(parentView.getContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(parentView.getContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(parentView.getContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
