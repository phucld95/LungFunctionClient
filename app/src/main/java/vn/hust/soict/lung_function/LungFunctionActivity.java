package vn.hust.soict.lung_function;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import vn.hust.soict.lung_function.data.AppData;
import vn.hust.soict.lung_function.model.LungFunction;
import vn.hust.soict.lung_function.utils.FontUtils;
import vn.hust.soict.lung_function.utils.Prompt;

public class LungFunctionActivity extends BaseActivity {

    private LungFunction mLungFunction;

    private TextView mPEF;
    private TextView mFEV1;
    private TextView mFVC;
    private TextView mFEV1divFVC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lung_function);

        initContext();
        initView();
        updateUI();
    }

    private void updateUI() {
        mLungFunction = AppData.getInstance().getLungFunction();
        if (mLungFunction == null) {
            Prompt.show(mContext, R.string.msg_no_found_lung_function_exception);
            finish();
        }

        String format = "%.2f";
        mPEF.setText(String.format(format, mLungFunction.getPEF()));
        mFEV1.setText(String.format(format, mLungFunction.getFEV1()));
        mFVC.setText(String.format(format, mLungFunction.getFVC()));
        mFEV1divFVC.setText(String.format(format, (mLungFunction.getFEV1()) / mLungFunction.getFVC()));
    }

    @Override
    protected void initView() {
        mPEF = (TextView) findViewById(R.id.tvPEFValue);
        mFEV1 = (TextView) findViewById(R.id.tvFEV1Value);
        mFVC = (TextView) findViewById(R.id.tvFVCValue);
        mFEV1divFVC = (TextView) findViewById(R.id.tvFEV1divFVCValue);

        FontUtils.setFont(mPEF);
        FontUtils.setFont(mFEV1);
        FontUtils.setFont(mFVC);
        FontUtils.setFont(mFEV1divFVC);
        FontUtils.setFont(findViewById(R.id.tvPEF));
        FontUtils.setFont(findViewById(R.id.tvFEV1));
        FontUtils.setFont(findViewById(R.id.tvFVC));
        FontUtils.setFont(findViewById(R.id.tvFEV1divFVC));
    }
}
