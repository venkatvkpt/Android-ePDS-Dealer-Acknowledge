package in.gov.bihar.sfc.biharepds.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import in.gov.bihar.sfc.biharepds.R;
import in.gov.bihar.sfc.biharepds.local.DatabaseHandler;
import in.gov.bihar.sfc.biharepds.local.InternetConnection;

public class MainActivity extends AppCompatActivity {
    private boolean isCorrect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        if (InternetConnection.checkConnection(getBaseContext())) {

            new Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            DatabaseHandler db = new DatabaseHandler(MainActivity.this);
                            isCorrect = db.isAnyDealer();
                            db.close();
                            if (isCorrect) {
                                Intent naviIntent = new Intent("in.gov.bihar.sfc.biharepds.activity.NavigationActivity");
                                startActivity(naviIntent);
                            } else if (isCorrect == false) {
                                //sign in page integration
                                Intent signinIntent = new Intent("in.gov.bihar.sfc.biharepds.activity.SigninActivity");
                                startActivity(signinIntent);
                            }
                        }
                    }, 3000);
        } else {
            Intent signinIntent = new Intent("in.gov.bihar.sfc.biharepds.activity.NoAccessActivity");
            startActivity(signinIntent);
        }

    }

}
