package in.gov.bihar.sfc.biharepds.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.txusballesteros.widgets.AnimationMode;
import com.txusballesteros.widgets.FitChart;
import com.txusballesteros.widgets.FitChartValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import in.gov.bihar.sfc.biharepds.R;
import in.gov.bihar.sfc.biharepds.client.ClientManager;
import in.gov.bihar.sfc.biharepds.local.DatabaseHandler;
import in.gov.bihar.sfc.biharepds.local.InternetConnection;
import in.gov.bihar.sfc.biharepds.model.DealerInformation;

/**
 * By VR
 */
public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String ARG_PARAM1 = "param1", ARG_PARAM2 = "param2";
    private String mParam1, mParam2;
    private DealerInformation info = new DealerInformation();
    private TextView ackNav, statusNav, dealerName, fpsLicense, fpsLocation, setPercent, lblQty, setChartDesc;
    private FitChart fitChart;
    private ProgressBar progressBar;
    private ScrollView scrollView;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        if (InternetConnection.checkConnection(getContext())) {
            initViewIds(view);
            displayTitleCard();
            clickFooter(view);
            Calendar cal = Calendar.getInstance();
            String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
            setChartDesc.setText(monthName.toLowerCase() +" distribution status");
            new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Intent signinIntent = new Intent("in.gov.bihar.sfc.biharepds.activity.NoAccessActivity");
            startActivity(signinIntent);
        }
        return view;
    }

    public void initViewIds(View view) {
        setPercent = (TextView) view.findViewById(R.id.set_percent);
        lblQty = (TextView) view.findViewById(R.id.lbl_qty);
        fitChart = (FitChart) view.findViewById(R.id.fitChart);
        progressBar = (ProgressBar) view.findViewById(R.id.home_progressBar);
        scrollView = (ScrollView) view.findViewById(R.id.home_scroll);
        dealerName = (TextView) view.findViewById(R.id.dealer_name);
        fpsLicense = (TextView) view.findViewById(R.id.dealer_license_number);
        fpsLocation = (TextView) view.findViewById(R.id.dealer_location);
        setChartDesc = (TextView) view.findViewById(R.id.setChartDesc);
    }

    public void displayTitleCard() {
        DatabaseHandler db = new DatabaseHandler(getActivity());
        info = db.getDealerinfo();
        dealerName.setText(info.getFps_dealer_name());
        fpsLicense.setText(info.getFps_license_auth_number());
        fpsLocation.setText(info.getLocality_market_name());
    }

    public void processChart(float percent) {
        fitChart.setMinValue(0f);
        fitChart.setMaxValue(100f);
        fitChart.setAnimationMode(AnimationMode.OVERDRAW);
        fitChart.animate();
        Resources resources = getResources();
        Collection<FitChartValue> values = new ArrayList<>();
        values.add(new FitChartValue(percent, resources.getColor(R.color.colorPrimary)));
        fitChart.setValues(values);
    }

    public void clickFooter(View view) {
        ackNav = (TextView) view.findViewById(R.id.nav_ack);
        ackNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Loading...", Toast.LENGTH_SHORT).show();
            }
        });

        statusNav = (TextView) view.findViewById(R.id.nav_status);
        statusNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Loading...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... inputs) {
            DatabaseHandler db = new DatabaseHandler(getActivity());
            String fpsCode = db.getDealerCode();
            db.close();
            String url = "http://202.65.133.69:90/BiharAndroidServer/getHomeInfo/" + fpsCode;
            return ClientManager.getWebServer(url);
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                try {
                    String[] data = result.split("#");
                    float value = Float.parseFloat(data[0]);
                    processChart(value);
                    setPercent.setText(value + "%");
                    lblQty.setText(data[1]);

                } catch (Exception e) {
                    setPercent.setText("N/A");
                    processChart(0.0f);
                    lblQty.setText("You submitted delivered quantity is more than allocated quantity.You need any information please contact BSFC.");
                }
                progressBar.setVisibility(View.INVISIBLE);
                scrollView.setVisibility(View.VISIBLE);
            }

        }
    }
}
