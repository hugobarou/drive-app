package com.drive.app.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.drive.app.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

   private EditText emailInput;
   private EditText passwordInput;
   private Button signInButton;
   private TextView registerTextView;
   private final String url = "";
   private RequestQueue queue;
   private String token;
   private String drive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInput = findViewById(R.id.activity_main_email_input);
        passwordInput = findViewById(R.id.activity_main_password_input);
        signInButton = findViewById(R.id.activity_main_sign_in_btn);
        registerTextView = findViewById(R.id.activity_main_register_txt);
        queue = Volley.newRequestQueue(this);
        token = "";


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                if(validate(new EditText[] { emailInput, passwordInput })){
                    signIn();
                }
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpActivityIntent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(signUpActivityIntent);
            }
        });
    }

    public void signIn(){
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);

        JsonObjectRequest req = new JsonObjectRequest(url + "api/user/signin", new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    token = (String) response.get("token");
                    drive = (String) response.get("email");
                    Intent driveActivityIntent = new Intent(MainActivity.this, DriveActivity.class);
                    driveActivityIntent.putExtra("token", token);
                    driveActivityIntent.putExtra("drive", drive);
                    startActivity(driveActivityIntent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(req);
    }

    public boolean validate(EditText[] fields){
        boolean valid = true;
        for(int i = 0; i < fields.length; i++){
            EditText currentField = fields[i];
            if(currentField.getText().toString().length() <= 0){
                currentField.setError("Field cannot be empty");
                valid = false;
            }
        }
        return valid;
    }

    public void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}