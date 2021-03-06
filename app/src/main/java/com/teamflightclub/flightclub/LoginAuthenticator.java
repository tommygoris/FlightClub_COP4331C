package com.teamflightclub.flightclub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Tim on 11/10/2016.
 */

public class LoginAuthenticator extends AsyncTask<String,Void,String> {

    Context context;
    AlertDialog alertDialog;
    AsyncCallback callback;

    String result = "";

    LoginAuthenticator(Context contxt, AsyncCallback asyncCallback) {

        context = contxt;
        callback = asyncCallback;
    }

    @Override
    protected String doInBackground(String... params) {
        String login_url = "http://teamflightclubproject.com/login.php";
        try {
            String email = params[0];
            String password = params[1];
            Log.v("Email",email);
            Log.v("Passworo",password);
            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("user_email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&"
                    + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            Log.v("Result",result);
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
    }

    @Override
    protected void onPostExecute(String result) {
        String resultMessage = "";
        if (result.equals("")) {
            resultMessage = "Username/Password Not Found";
            alertDialog.setMessage(resultMessage);
            alertDialog.show();
            Log.v("Login failed", result);
        }

        else {
            //rowID = result;
            Log.v("myApp","Row ID = "+result);
            resultMessage = "Login Successful";
            //Intent intent = new Intent(context, ControlPanelActivity.class);
            //intent.putExtra("rowID",result);
            //context.startActivity(intent);
            alertDialog.setMessage(resultMessage);
            alertDialog.show();
            Log.v("LoginActivity", "Login SUCCESFULLLLLL");
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("userRowID",result).commit();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    alertDialog.dismiss();
                    callback.done();
                }
            },1000);

        }






    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }




}