package in.gov.bihar.sfc.biharepds.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import in.gov.bihar.sfc.biharepds.R;
import in.gov.bihar.sfc.biharepds.client.ClientManager;
import in.gov.bihar.sfc.biharepds.local.DatabaseHandler;
import in.gov.bihar.sfc.biharepds.local.InternetConnection;
import in.gov.bihar.sfc.biharepds.model.DealerInformation;


public class AcknowledgementFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1", ARG_PARAM2 = "param2";
    private OnFragmentInteractionListener mListener;
    private String mParam1, mParam2, selectedMonth, selectedYear;
    private Spinner spinnerM, spinnerY;
    private String[] month = {"Month", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private String[] year = {"Year", "2016", "2017", "2018", "2019", "2020"};
    private TextView lblAAYRice, lblAAYWheat, lblPHHRice, lblPHHWheat;
    private double lblAAYRiceValue, lblAAYWheatValue, lblPHHRiceValue, lblPHHWheatValue;
    private TableLayout displayValues;
    private EditText aayRice, aayWheat, phhRice, phhWheat;
    private String aayRiceValue, aayWheatValue, phhRiceValue, phhWheatValue;
    private Button ackbtn, clrbtn;
    private ProgressDialog progressDialog;

    public AcknowledgementFragment() {

    }

    public static AcknowledgementFragment newInstance(String param1, String param2) {
        AcknowledgementFragment fragment = new AcknowledgementFragment();
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
        Toast.makeText(getActivity(), "First Select Month & Year, For Allocation Details.", Toast.LENGTH_LONG).show();
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
        View view = inflater.inflate(R.layout.fragment_acknowledgement, container, false);
        if (InternetConnection.checkConnection(getContext())) {
            monthSpinner(view);
            yearSpinner(view);

            lblAAYRice = (TextView) view.findViewById(R.id.lblAAYRice);
            lblAAYWheat = (TextView) view.findViewById(R.id.lblAAYWheat);
            lblPHHRice = (TextView) view.findViewById(R.id.lblPHHRice);
            lblPHHWheat = (TextView) view.findViewById(R.id.lblPHHWheat);
            displayValues = (TableLayout) view.findViewById(R.id.displayValues);

            aayRice = (EditText) view.findViewById(R.id.aay_rice);
            aayWheat = (EditText) view.findViewById(R.id.aay_wheat);
            phhRice = (EditText) view.findViewById(R.id.phh_rice);
            phhWheat = (EditText) view.findViewById(R.id.phh_wheat);

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            onClickACKnowledgeBtn(view);
            onClickClearBtn(view);
        } else {
            Intent signinIntent = new Intent("in.gov.bihar.sfc.biharepds.activity.NoAccessActivity");
            startActivity(signinIntent);
        }

        return view;
    }


    public void monthSpinner(View view) {
        spinnerM = (Spinner) view.findViewById(R.id.s_month);

        ArrayAdapter<CharSequence> monthAdapter = new ArrayAdapter<CharSequence>(getActivity(),
                android.R.layout.simple_spinner_item, month);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerM.setAdapter(monthAdapter);

        spinnerM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                       long id) {
                if (!parent.getItemAtPosition(pos).toString().contains("Month")) {
                    displayLabel();
                } else {
                    displayValues.setVisibility(getView().GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void yearSpinner(View view) {
        spinnerY = (Spinner) view.findViewById(R.id.s_year);

        ArrayAdapter<CharSequence> monthAdapter = new ArrayAdapter<CharSequence>(getActivity(),
                android.R.layout.simple_spinner_item, year);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerY.setAdapter(monthAdapter);

        spinnerY.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                       long id) {
                if (!parent.getItemAtPosition(pos).toString().contains("Year")) {
                    displayLabel();
                } else {
                    displayValues.setVisibility(getView().GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void displayLabel() {
        selectedMonth = spinnerM.getSelectedItem().toString();
        selectedYear = spinnerY.getSelectedItem().toString();
        if (!selectedMonth.contains("Month") && !selectedYear.contains("Year")) {
            DatabaseHandler db = new DatabaseHandler(getActivity());
            String fpsCode = db.getDealerCode();
            String url = "http://202.65.133.69:90/BiharAndroidServer/getFPSAllocation/" + fpsCode + "/" + selectedMonth + "/" + selectedYear;
            new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);

            String url2 = "http://202.65.133.69:90/BiharAndroidServer/isacknowledged/" + fpsCode + "/" + selectedMonth + "/" + selectedYear;
            new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url2);
        }

    }

    public void onClickACKnowledgeBtn(View view) {
        ackbtn = (Button) view.findViewById(R.id.btAck);
        ackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                aayRiceValue = aayRice.getText().toString();
                aayWheatValue = aayWheat.getText().toString();
                phhRiceValue = phhRice.getText().toString();
                phhWheatValue = phhWheat.getText().toString();
                selectedMonth = spinnerM.getSelectedItem().toString();
                selectedYear = spinnerY.getSelectedItem().toString();
                if (selectedYear.contains("Year") || selectedMonth.contains("Month")) {
                    Toast.makeText(getActivity(), "Please Select Month and Year.", Toast.LENGTH_LONG).show();
                } else {
                    if (aayRiceValue.matches("") || aayWheatValue.matches("") || phhRiceValue.matches("") || phhWheatValue.matches("")) {
                        Toast.makeText(getActivity(), "Please Enter Delivered Quantity.", Toast.LENGTH_LONG).show();
                    } else {
                        if( validateFields(aayRiceValue, aayWheatValue, phhRiceValue, phhWheatValue)) {
                            DatabaseHandler db = new DatabaseHandler(getActivity());
                            DealerInformation info = db.getDealerDetails();
                            db.close();
                            String content = "('" + info.getFps_code() + "',To_number(TO_CHAR(TO_DATE('" + selectedMonth + "','Month'),'MM')),'" + selectedYear + "'," +
                                    phhRiceValue.replace(".", "vr") + "," + phhWheatValue.replace(".", "vr") + "," + aayRiceValue.replace(".", "vr") + "," + aayWheatValue.replace(".", "vr") + "," +
                                    info.getDistrict_id() + "," + info.getBlock_id() + "," + info.getPanchayat_id() + ")";

                            String url = "http://202.65.133.69:90/BiharAndroidServer/setDealerAcknowledgement/" + content;
                            new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
                            resetFields(arg0);
                        }
                    }
                }

            }
        });
    }

    public void onClickClearBtn(View view) {
        clrbtn = (Button) view.findViewById(R.id.btClr);
        clrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetFields(arg0);
                Toast.makeText(getActivity(), "Fields was reseted.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void resetFields(View view) {
        aayRice.setText("");
        aayWheat.setText("");
        phhRice.setText("");
        phhWheat.setText("");
        spinnerM.setSelection(0);
        spinnerY.setSelection(0);
        displayValues.setVisibility(getView().GONE);

        InputMethodManager inputManager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        IBinder binder = view.getWindowToken();
        inputManager.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public  boolean  validateFields(String aayRiceValue,String  aayWheatValue,String  phhRiceValue,String  phhWheatValue){
        boolean flag = true;
        lblAAYRiceValue = Double.parseDouble(lblAAYRice.getText().toString().trim());
        lblAAYWheatValue = Double.parseDouble(lblAAYWheat.getText().toString().trim());
        lblPHHRiceValue = Double.parseDouble(lblPHHRice.getText().toString().trim());
        lblPHHWheatValue = Double.parseDouble(lblPHHWheat.getText().toString().trim());

        if( (Double.parseDouble(aayRiceValue) > lblAAYRiceValue) ){
            flag = false;
            Toast.makeText(getActivity(), "Entered AAY Rice Quantity is more than Allocated Quantity.", Toast.LENGTH_LONG).show();
        }else if((Double.parseDouble(aayWheatValue) > lblAAYWheatValue)){
            flag = false;
            Toast.makeText(getActivity(), "Entered AAY Wheat Quantity is more than Allocated Quantity.", Toast.LENGTH_LONG).show();
        }else if((Double.parseDouble(phhRiceValue) > lblPHHRiceValue)){
            flag = false;
            Toast.makeText(getActivity(), "Entered PHH Rice Quantity is more than Allocated Quantity.", Toast.LENGTH_LONG).show();
        }else if((Double.parseDouble(phhWheatValue) > lblPHHWheatValue)){
            flag = false;
            Toast.makeText(getActivity(), "Entered PHH Wheat Quantity is more than Allocated Quantity.", Toast.LENGTH_LONG).show();
        }else{
            flag = true;
        }

        return flag;
    }

    private class MyAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... input) {
            return ClientManager.getWebServer(input[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result != null) {
                if (result.contains("#")) {
                    //Allocation Values Displaying
                    try {
                        String[] data = result.split("#");
                        lblAAYRice.setText(data[0]);
                        lblAAYWheat.setText(data[1]);
                        lblPHHRice.setText(data[2]);
                        lblPHHWheat.setText(data[3]);
                    } catch (Exception e) {
                        lblAAYRice.setText("Problem");
                        lblAAYWheat.setText("Problem");
                        lblPHHRice.setText("Problem");
                        lblPHHWheat.setText("Problem");
                    }
                    displayValues.setVisibility(getView().VISIBLE);
                } else if(result.contains("Acknowledgement")) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage(result)
                            .setPositiveButton("ok", null
                            ).show()
                            .getButton(DialogInterface.BUTTON_POSITIVE).
                            setBackgroundColor(getResources().getColor(R.color.navigationBarColor));
                }

            } else {

                Toast.makeText(getActivity(), "Server Down, Please try again.", Toast.LENGTH_LONG).show();
            }

        }
    }


}
