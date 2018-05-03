package vn.hust.soict.lung_function;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import vn.hust.soict.lung_function.adapter.PatientAdapter;
import vn.hust.soict.lung_function.config.AppConstant;
import vn.hust.soict.lung_function.data.RealmDB;
import vn.hust.soict.lung_function.file.TempFile;
import vn.hust.soict.lung_function.file.WavFile;
import vn.hust.soict.lung_function.model.LungFunction;
import vn.hust.soict.lung_function.model.Profile;
import vn.hust.soict.lung_function.net.RestRequest;
import vn.hust.soict.lung_function.net.WebGlobal;
import vn.hust.soict.lung_function.utils.FontUtils;
import vn.hust.soict.lung_function.utils.MSharedPreferences;
import vn.hust.soict.lung_function.utils.Prompt;

public class MainActivity extends BaseActivity {

    private Dialog mProgressRecording;
    private TextView mDialogMessage;


    private Dialog mDialogSelectPatient;


    private RealmDB mRealmDB;

    private Button btnLungFunction;
    private EditText edFullName;
    private EditText edBirthDay;
    private EditText edGender;
    private EditText edWeight;
    private EditText edHeight;
    private EditText edRegion;
    private EditText edSmoking;

    private Profile mProfile;
    private String mProfileId;

    private Handler mHandler;

    // Record Field
    private int bufferSize;
    private boolean recording;

    private AudioRecord recorder = null;

    private TempFile mTempFile;
    private WavFile mWavFile;

    private RestRequest mRestRequest;


    private MenuItem menuActionSelectPatient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initContext();
        initView();
//        initData();


        requestRecordAudioAccessNetworkPermission();

        initController();
    }


    private void requestRecordAudioAccessNetworkPermission() {
        //check API version, do nothing if API version < 23!
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE}, 1);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("Activity", "Granted!");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("Activity", "Denied!");
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hidenKeyboard();
        if (mProfile == null) {
            mProfileId = MSharedPreferences.getInstance(mContext).getString(AppConstant.KEY_ID_PATIENT_SELECTED, "");
            if (mProfileId.equals("")) {
                // show viewAdd
                findViewById(R.id.layoutAddPatient).setVisibility(View.VISIBLE);
                if (menuActionSelectPatient != null) {
                    menuActionSelectPatient.setVisible(false);
                }
            } else {
                findViewById(R.id.layoutAddPatient).setVisibility(View.GONE);
                if (menuActionSelectPatient != null) {
                    menuActionSelectPatient.setVisible(true);
                }
                mProfile = new RealmDB().getProfile(mProfileId);
                if (mProfile != null) {
                    updateUIProfile();
                } else {
                    showDialogSelectPatient();
                }
            }
        } else {
            String id = MSharedPreferences.getInstance(mContext).getString(AppConstant.KEY_ID_PATIENT_SELECTED, "");
            if (!mProfileId.equals(id)) {
                mProfileId = id;
                mProfile = new RealmDB().getProfile(mProfileId);
                if (mProfile != null) {
                    updateUIProfile();
                } else {
                    showDialogSelectPatient();
                }
            }
        }
    }

    private void record() {
        if (recording) return;
        try {
            mTempFile = new TempFile(mContext);
            recording = true;
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    AppConstant.RECORDER_SAMPLERATE, AppConstant.RECORDER_CHANNELS,
                    AppConstant.RECORDER_AUDIO_ENCODING, bufferSize);

            mProgressRecording.show();
            recorder.startRecording();
            new Thread(hanldeRecording).start();
            mHandler.postDelayed(handlePostRecording, AppConstant.TIME_RECORD_DEFAULT);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Prompt.show(mContext, R.string.msg_file_no_found_exception);
        }
    }

    private Runnable hanldeRecording = new Runnable() {

        @Override
        public void run() {
            try {
                byte data[] = new byte[bufferSize];

                while (recording) {
                    recorder.read(data, 0, bufferSize);
                    if (mTempFile != null) {
                        mTempFile.write(data);
                    } else {
                        Prompt.show(mContext, R.string.msg_temp_file_null_point);
                        break;
                    }
                }
                recorder.release();
                recorder = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable handlePostRecording = new Runnable() {
        @Override
        public void run() {
            try {
                // Dung qua trinh ghi am
                recording = false;
                // Luu file tam lai
                mTempFile.close();
                mWavFile = new WavFile(mContext,
                        mTempFile.getPathTempFile(),
                        AppConstant.RECORDER_SAMPLERATE,
                        AppConstant.NUMBER_CHANNELS,
                        AppConstant.BIT_PER_SAMPLE);
                // Luu file wave
                mWavFile.saveWaveFile();

//                mRestRequest = new RestRequest();
                mRestRequest.setHeaders();

                // Ket thuc ghi am
                String URL = MSharedPreferences.getInstance(mContext).getString(AppConstant.KEY_SERVER_URL, WebGlobal.URL);
                LungFunction lungFunction = mRestRequest.postResponse(URL, mWavFile.getFileName(),mProfile);
//                LungFunction lungFunction = mRestRequest.postResponse(URL, "/storage/emulated/0/Music/RECORD.wav");

                // Xoa file tam thoi
                mTempFile.deleteFile();
                // Xoa file wave
                mWavFile.deleteFile();
                mProgressRecording.dismiss();

                if (lungFunction != null) {
//                    AppData.getInstance().setLungFunction(lungFunction);
//                    Intent intent = new Intent(mContext, LungFunctionActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                    MainActivity.this.finish();
                } else {
                    Prompt.show(mContext, "LungFunction is NULL");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                mProgressRecording.dismiss();
                Prompt.show(mContext, R.string.msg_file_no_found_exception);
            } catch (IOException e) {
                e.printStackTrace();
                mProgressRecording.dismiss();
                Prompt.show(mContext, R.string.msg_file_io_exception);
            }
        }
    };

    protected void initContext() {
        super.initContext();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);
        }

        recording = false;
        if (bufferSize <= AppConstant.BUFFER_SIZE_DEFAULT) {
            bufferSize = AppConstant.BUFFER_SIZE_DEFAULT;
        }
        mHandler = new Handler();
        mRestRequest = new RestRequest();
        mRealmDB = new RealmDB();
    }

    protected void initController() {
        findViewById(R.id.ivSetUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSetupUrl();
            }
        });

        btnLungFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProfile == null) {
                    Prompt.show(mContext, R.string.msg_text_request_add_patient);
                }
                record();
            }
        });

        findViewById(R.id.ivAddPatient).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddPatientActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initDatga() {
        mProfile = new Profile();
        mProfile.setName("LE CONG Tu");
        mProfile.setBirthDay("07/02/1994");
        mProfile.setMale(true);
        mProfile.setHeight(160);
        mProfile.setWeight(60);
        mProfile.setRegion(Profile.REGION_NORTHEN);
        mProfile.setSmoking(false);
    }

    private void updateUIProfile() {
        if (mProfile != null) {
            edFullName.setText(mProfile.getName());
            edBirthDay.setText(mProfile.getBirthDay());
            edGender.setText(mProfile.isMale() ? R.string.gender_male : R.string.gender_female);
            edWeight.setText(mProfile.getWeight() + "");
            edHeight.setText(mProfile.getHeight() + "");
            switch (mProfile.getRegion()) {
                case Profile.REGION_NORTHEN:
                    edRegion.setText(R.string.region_northen);
                    break;
                case Profile.REGION_CENTRAL:
                    edRegion.setText(R.string.region_central);
                    break;
                case Profile.REGION_SOUTH:
                    edRegion.setText(R.string.region_south);
                    break;
                default:
                    edRegion.setText("");
            }
            edSmoking.setText(mProfile.isSmoking() ? R.string.smoking_yes : R.string.smoking_no);
        }
    }

    protected void initView() {
        btnLungFunction = (Button) findViewById(R.id.btnLungFunction);
        edFullName = (EditText) findViewById(R.id.edName);
        edBirthDay = (EditText) findViewById(R.id.edBirthDay);
        edGender = (EditText) findViewById(R.id.edGender);
        edWeight = (EditText) findViewById(R.id.edWeight);
        edHeight = (EditText) findViewById(R.id.edHeight);
        edRegion = (EditText) findViewById(R.id.edRegion);
        edSmoking = (EditText) findViewById(R.id.edSmoking);

        mProgressRecording = new Dialog(this, android.R.style.Theme_Translucent);
        mProgressRecording.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressRecording.setContentView(R.layout.dialog_recording);
        mProgressRecording.setCancelable(false);
        mProgressRecording.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        mDialogMessage = (TextView) mProgressRecording.findViewById(R.id.tvMessage);

        FontUtils.setFont(mDialogMessage);
        FontUtils.setFont(findViewById(R.id.inputName));
        FontUtils.setFont(findViewById(R.id.inputBirthDay));
        FontUtils.setFont(findViewById(R.id.inputGender));
        FontUtils.setFont(findViewById(R.id.inputWeight));
        FontUtils.setFont(findViewById(R.id.inputHeight));
        FontUtils.setFont(findViewById(R.id.inputRegion));
        FontUtils.setFont(findViewById(R.id.inputSmoking));

        FontUtils.setFont(findViewById(R.id.tvAddPatient), FontUtils.TYPE_NORMAL);

        FontUtils.setFont(btnLungFunction, FontUtils.TYPE_NORMAL);
    }

    private void showDialogSetupUrl() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_set_url);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        final EditText edUrl = (EditText) dialog.findViewById(R.id.edInputUrl);

        edUrl.setText(MSharedPreferences.getInstance(mContext).getString(AppConstant.KEY_SERVER_URL, WebGlobal.URL));

        FontUtils.setFont(dialog.findViewById(R.id.title), FontUtils.TYPE_NORMAL);
        FontUtils.setFont(dialog.findViewById(R.id.inputUrl));
        FontUtils.setFont(dialog.findViewById(R.id.btnCancel), FontUtils.TYPE_NORMAL);
        FontUtils.setFont(dialog.findViewById(R.id.btnOK), FontUtils.TYPE_NORMAL);

        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidenKeyboard();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.rootView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidenKeyboard();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = edUrl.getText().toString().trim();
                if (url.equals("")) {
                    Prompt.show(mContext, R.string.msg_input_url);
                } else {
                    hidenKeyboard();
                    dialog.dismiss();
                    MSharedPreferences.getInstance(mContext).putString(AppConstant.KEY_SERVER_URL, url);
                }
            }
        });

        dialog.show();

    }

    private void showDialogSelectPatient() {
        if (mDialogSelectPatient != null) {
            mDialogSelectPatient.dismiss();
        }

        mDialogSelectPatient = new Dialog(this, android.R.style.Theme_Translucent);
        mDialogSelectPatient.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogSelectPatient.setContentView(R.layout.dialog_list_patient);
        mDialogSelectPatient.setCancelable(true);
        mDialogSelectPatient.setCanceledOnTouchOutside(true);
        mDialogSelectPatient.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        FontUtils.setFont(mDialogSelectPatient.findViewById(R.id.title), FontUtils.TYPE_NORMAL);
        FontUtils.setFont(mDialogSelectPatient.findViewById(R.id.textPatient));

        RecyclerView recyclerView = (RecyclerView) mDialogSelectPatient.findViewById(R.id.rvPatient);

        final List<Profile> profiles = mRealmDB.getProfiles();

        PatientAdapter.OnClickItemListener itemPatientListener = new PatientAdapter.OnClickItemListener() {
            @Override
            public void onClick(int position) {
                mDialogSelectPatient.dismiss();
                mProfile = profiles.get(position);
                if (menuActionSelectPatient != null) {
                    menuActionSelectPatient.setVisible(true);
                }
                if (mProfile != null)
                    MSharedPreferences.getInstance(mContext).putString(AppConstant.KEY_ID_PATIENT_SELECTED, mProfile.getID());
                updateUIProfile();
            }
        };
        PatientAdapter patientAdapter = new PatientAdapter(mContext, profiles, itemPatientListener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(patientAdapter);


        mDialogSelectPatient.findViewById(R.id.addPatient).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogSelectPatient.dismiss();
                Intent intent = new Intent(mContext, AddPatientActivity.class);
                startActivity(intent);
            }
        });

        mDialogSelectPatient.findViewById(R.id.rootView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogSelectPatient.dismiss();
            }
        });

        mDialogSelectPatient.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        menuActionSelectPatient = menu.findItem(R.id.selectPatient);
        if (menuActionSelectPatient != null) {
            if (mProfile == null) {
                menuActionSelectPatient.setVisible(false);
            } else {
                menuActionSelectPatient.setVisible(true);
            }
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        mRealmDB.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.selectPatient:
                showDialogSelectPatient();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
