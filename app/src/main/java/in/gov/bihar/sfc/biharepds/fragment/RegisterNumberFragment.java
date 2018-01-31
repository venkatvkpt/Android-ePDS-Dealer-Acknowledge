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
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import in.gov.bihar.sfc.biharepds.R;
import in.gov.bihar.sfc.biharepds.client.ClientManager;
import in.gov.bihar.sfc.biharepds.local.DatabaseHandler;
import in.gov.bihar.sfc.biharepds.local.InternetConnection;
import in.gov.bihar.sfc.biharepds.model.DealerInformation;

/**
 * By VR
 */
public class RegisterNumberFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String ARG_PARAM1 = "param1", ARG_PARAM2 = "param2";
    private String mParam1, mParam2;
    private DealerInformation info = new DealerInformation();
    private EditText name, number, email;
    private Button submitbtn, clrbtn;
    private String nameValue, numberValue, emailValue;
    private ProgressDialog progressDialog;

    public RegisterNumberFragment() {
    }

    public static RegisterNumberFragment newInstance(String param1, String param2) {
        RegisterNumberFragment fragment = new RegisterNumberFragment();
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
        View view = inflater.inflate(R.layout.fragment_registernumber, container, false);
        if (InternetConnection.checkConnection(getContext())) {
            initViewIds(view);
            onClickStatusBtn(view);
            onClickClearBtn(view);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        } else {
            Intent signinIntent = new Intent("in.gov.bihar.sfc.biharepds.activity.NoAccessActivity");
            startActivity(signinIntent);
        }
        return view;
    }

    public void initViewIds(View view) {
        name = (EditText) view.findViewById(R.id.c_name);
        number = (EditText) view.findViewById(R.id.c_number);
        email = (EditText) view.findViewById(R.id.c_email);

    }

    public void onClickStatusBtn(View view) {
        submitbtn = (Button) view.findViewById(R.id.btReg);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                nameValue = name.getText().toString();
                numberValue = number.getText().toString();
                emailValue = email.getText().toString();
                String validEmail = "", validMobile = "";
                boolean isValide = true;
                if (nameValue.equals("")) {
                    Toast.makeText(getActivity(), "Please Enter Beneficiary Name.", Toast.LENGTH_LONG).show();
                } else if (numberValue.equals("")) {
                    Toast.makeText(getActivity(), "Please Enter Beneficiary Mobile Number.", Toast.LENGTH_LONG).show();
                }
                if (!(emailValue.equals(""))) {
                    if (Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
                        //checking email address here valid
                        validEmail = emailValue;
                    } else {
                        //checking email address here not valid
                        isValide = false;
                        Toast.makeText(getActivity(), "Please Enter Valid Email Address", Toast.LENGTH_LONG).show();
                    }
                }
                if (!(numberValue.equals(""))) {
                    if ((Patterns.PHONE.matcher(numberValue).matches()) && (numberValue.length() == 10)) {
                        //checking mobile numer here valid
                        validMobile = numberValue;

                    } else {
                        //checking mobile numer here not valid
                        isValide = false;
                        Toast.makeText(getActivity(), "Please Enter Valid Mobile Number", Toast.LENGTH_LONG).show();
                    }
                }

                if (isValide) {
                    DatabaseHandler db = new DatabaseHandler(getActivity());
                    DealerInformation info = db.getDealerDetails();
                    db.close();
                    String content = "(10," + info.getDistrict_id() + "," + info.getBlock_id() + "," + info.getPanchayat_id() + ",27,'" + validMobile.trim() + "','" + info.getFps_code() + "','" + nameValue.replace(" ","s9ac3") + "',SYSDATE,0,'Y',1,'" + validEmail.replace(".", "d0t") + "')";
                    String url = "http://202.65.133.69:90/BiharAndroidServer/setRegisterNumber/" + content;
                    new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
                    resetFields(arg0);
                }


            }
        });
    }

    public void onClickClearBtn(View view) {
        clrbtn = (Button) view.findViewById(R.id.btCler);
        clrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetFields(arg0);
                Toast.makeText(getActivity(), "Fields was reseted.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void resetFields(View view) {
        name.setText("");
        number.setText("");
        email.setText("");

        InputMethodManager inputManager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        IBinder binder = view.getWindowToken();
        inputManager.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
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
                    new AlertDialog.Builder(getActivity())
                            .setMessage(result)
                            .setPositiveButton("ok", null
                            ).show()
                            .getButton(DialogInterface.BUTTON_POSITIVE).
                            setBackgroundColor(getResources().getColor(R.color.navigationBarColor));

            } else {

                Toast.makeText(getActivity(), "Server Down, Please try again.", Toast.LENGTH_LONG).show();
            }

        }
    }
}
