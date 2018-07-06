package com.example.aimert.sockettest;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.testEditText);
        btnSend = findViewById(R.id.testBtnSend);

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnSend.setEnabled(editText.getText().toString().trim().length() != 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btnSend.setOnClickListener((v) -> {

            switch (v.getId()) {
                case R.id.testBtnSend:
                    sendMessage();
                    break;
            }
        });

        btnSend.setEnabled(false);
    }

    private void sendMessage() {
        new SendMessageTask(this).execute();
    }


    private static class SendMessageTask extends AsyncTask<String, Void, String> {

        private WeakReference<Activity> activityReference;

        SendMessageTask(Activity activity) {
            activityReference = new WeakReference<>(activity);
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Socket s = new Socket("84.120.70.121", 13813);

                OutputStream os = s.getOutputStream();
                PrintWriter writer = new PrintWriter(os);

                EditText editText = activityReference.get().findViewById(R.id.testEditText);

                writer.println(editText.getText());
                writer.flush();

                InputStream is = s.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String response = reader.readLine();

                writer.close();
                os.close();

                reader.close();
                is.close();

                return response;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            TextView responseTextView = activityReference.get().findViewById(R.id.testResponseText);
            if (response == null) {
                responseTextView.setText("Oops! An error ocurred!");
            } else if (response.trim().length() != 0) {
                responseTextView.setText(response);
            }
        }
    }
}
