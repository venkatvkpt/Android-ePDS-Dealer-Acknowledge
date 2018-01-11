package in.gov.bihar.sfc.biharepds.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import in.gov.bihar.sfc.biharepds.R;
import in.gov.bihar.sfc.biharepds.local.InternetConnection;

public class NoAccessActivity extends AppCompatActivity {
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_access);
        imageView =(ImageView)findViewById(R.id.try_again);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

        imageView.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view) {
                if (InternetConnection.checkConnection(getBaseContext())) {
                    Intent signinIntent = new Intent(NoAccessActivity.this,MainActivity.class);
                    startActivity(signinIntent);
                    finish();
                }else{
                    Toast.makeText(NoAccessActivity.this,"No Connection Found.",Toast.LENGTH_LONG ).show();
                }
            }});
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
