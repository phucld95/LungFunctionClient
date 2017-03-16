package vn.hust.soict.lung_function;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.Calendar;
import java.util.Date;

import vn.hust.soict.lung_function.config.AppConstant;
import vn.hust.soict.lung_function.data.RealmDB;
import vn.hust.soict.lung_function.model.Profile;
import vn.hust.soict.lung_function.utils.FontUtils;
import vn.hust.soict.lung_function.utils.MSharedPreferences;
import vn.hust.soict.lung_function.utils.Prompt;

public class AddPatientActivity extends BaseActivity {

    private EditText mFullName;
    private EditText mWeight;
    private EditText mHeight;

    private DatePicker mBirthDay;

    private RadioGroup mGender;
    private RadioGroup mRegion;
    private RadioGroup mSmoking;

    private Profile mProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        initContext();

        initView();
        initController();
    }


    @Override
    protected void initContext() {
        super.initContext();
        mProfile = new Profile();
    }

    @Override
    protected void initController() {
        findViewById(R.id.layoutFullName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFocus(mFullName);
            }
        });
        findViewById(R.id.layoutWeight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFocus(mWeight);
            }
        });
        findViewById(R.id.layoutHeight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFocus(mHeight);
            }
        });
    }

    @Override
    protected void initView() {
        mFullName = (EditText) findViewById(R.id.edFullName);
        mWeight = (EditText) findViewById(R.id.edWeight);
        mHeight = (EditText) findViewById(R.id.edHeight);

        mBirthDay = (DatePicker) findViewById(R.id.datePickerBirthDay);
        Calendar birthday = Calendar.getInstance();
        birthday.set(1994, 0, 1);
        mBirthDay.updateDate(birthday.get(Calendar.YEAR), birthday.get(Calendar.MONTH), birthday.get(Calendar.DAY_OF_MONTH));

        mGender = (RadioGroup) findViewById(R.id.rgGender);
        mRegion = (RadioGroup) findViewById(R.id.rgRegion);
        mSmoking = (RadioGroup) findViewById(R.id.rgSmoking);

        FontUtils.setFont(mBirthDay);

        FontUtils.setFont(findViewById(R.id.edFullName));
        FontUtils.setFont(findViewById(R.id.edWeight));
        FontUtils.setFont(findViewById(R.id.edHeight));

        FontUtils.setFont(findViewById(R.id.tvFullNameHint));
        FontUtils.setFont(findViewById(R.id.tvBirthDayHint));
        FontUtils.setFont(findViewById(R.id.tvGenderHint));
        FontUtils.setFont(findViewById(R.id.tvWeightHint));
        FontUtils.setFont(findViewById(R.id.tvHeightHint));
        FontUtils.setFont(findViewById(R.id.tvRegionHint));
        FontUtils.setFont(findViewById(R.id.tvSmokingHint));
        FontUtils.setFont(findViewById(R.id.rbMale));
        FontUtils.setFont(findViewById(R.id.rbFemale));
        FontUtils.setFont(findViewById(R.id.rbRegionNorthen));
        FontUtils.setFont(findViewById(R.id.rbRegionCentral));
        FontUtils.setFont(findViewById(R.id.rbRegionSouth));
        FontUtils.setFont(findViewById(R.id.rbSmokingYes));
        FontUtils.setFont(findViewById(R.id.rbSmokingNo));
    }

    private void addPatient() {
        String strTmp = mFullName.getText().toString();
        int intTmp;
        if (strTmp.equals("")) {
            Prompt.show(mContext, R.string.msg_input_name);
            getFocus(mFullName);
            return;
        }

        mProfile.setName(strTmp);

        String birthday = String.format("%02d/%02d/%04d", mBirthDay.getDayOfMonth(), (mBirthDay.getMonth() + 1), mBirthDay.getYear());
        mProfile.setBirthDay(birthday);

        switch (mGender.getCheckedRadioButtonId()) {
            case R.id.rbMale:
                mProfile.setMale(true);
                break;
            case R.id.rbFemale:
                mProfile.setMale(false);
                break;
            default:
                Prompt.show(mContext, R.string.msg_input_gender);
                return;
        }

        strTmp = mWeight.getText().toString();

        if (strTmp.equals("")) {
            Prompt.show(mContext, R.string.msg_input_weight);
            getFocus(mWeight);
            return;
        }
        intTmp = Integer.parseInt(strTmp);

        mProfile.setWeight(intTmp);

        strTmp = mHeight.getText().toString();

        if (strTmp.equals("")) {
            Prompt.show(mContext, R.string.msg_input_height);
            getFocus(mHeight);
            return;
        }
        intTmp = Integer.parseInt(strTmp);

        mProfile.setHeight(intTmp);

        switch (mRegion.getCheckedRadioButtonId()) {
            case R.id.rbRegionNorthen:
                mProfile.setRegion(Profile.REGION_NORTHEN);
                break;
            case R.id.rbRegionCentral:
                mProfile.setRegion(Profile.REGION_CENTRAL);
                break;
            case R.id.rbRegionSouth:
                mProfile.setRegion(Profile.REGION_SOUTH);
                break;
            default:
                Prompt.show(mContext, R.string.msg_input_region);
                return;
        }


        switch (mSmoking.getCheckedRadioButtonId()) {
            case R.id.rbSmokingYes:
                mProfile.setSmoking(true);
                break;
            case R.id.rbSmokingNo:
                mProfile.setSmoking(false);
                break;
            default:
                Prompt.show(mContext, R.string.msg_input_smoking);
                return;
        }

        MSharedPreferences.getInstance(mContext).putString(AppConstant.KEY_ID_PATIENT_SELECTED, mProfile.getID());

        RealmDB realmDB = new RealmDB();
        realmDB.updateProfile(mProfile);
        realmDB.close();

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_patient_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addPatient:
                addPatient();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
