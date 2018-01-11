package in.gov.bihar.sfc.biharepds.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import in.gov.bihar.sfc.biharepds.R;
import in.gov.bihar.sfc.biharepds.client.ClientManager;
import in.gov.bihar.sfc.biharepds.local.DatabaseHandler;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SigninActivity extends AppCompatActivity {
    private EditText username, password;
    private String usernameValue, passwordValue;
    private ProgressDialog progressDialog;
    private DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getWindow().setBackgroundDrawableResource(R.drawable.background);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack));
        }

        onclickSignin();
    }

    public void onclickSignin() {
        Button Signinbutton = (Button) findViewById(R.id.signin_btn);

        Signinbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                username = (EditText) findViewById(R.id.username);
                password = (EditText) findViewById(R.id.password);
                usernameValue = username.getText().toString().trim();
                passwordValue = password.getText().toString().trim();

                progressDialog = new ProgressDialog(SigninActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");

                if (usernameValue.matches("")) {
                    Toast.makeText(SigninActivity.this, "You did not enter a username", Toast.LENGTH_LONG).show();
                } else if (passwordValue.matches("")) {
                    Toast.makeText(SigninActivity.this, "You did not enter a password", Toast.LENGTH_LONG).show();

                } else {
                    SigninActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    progressDialog.show();
                    new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, usernameValue, passwordValue);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
        super.onBackPressed();
    }

    private class MyAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... inputs) {
            String url = "http://202.65.133.69:90/BiharAndroidServer/authenticateUser/" + inputs[0] + "/" + inputs[1];
            return ClientManager.getWebServer(url);
        }

        @Override
        protected void onPostExecute(String result) {
            final String output = result;

            if (output != null) {
                if (!(output.equals("Invalid"))) {

                    new Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    db.insertSigninDealer(output);
                                    db.close();
                                    Intent mainIntent = new Intent("in.gov.bihar.sfc.biharepds.activity.NavigationActivity");
                                    startActivity(mainIntent);
                                    progressDialog.dismiss();
                                }
                            }, 3000);

                } else if (output.equals("Invalid")) {
                    resetFields();
                    Toast.makeText(SigninActivity.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                }

            } else {
                resetFields();
                Toast.makeText(SigninActivity.this, "Server Down, Please try again.", Toast.LENGTH_LONG).show();
            }


        }
    }

    public void resetFields() {
        username.setText("");
        password.setText("");
        progressDialog.dismiss();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}