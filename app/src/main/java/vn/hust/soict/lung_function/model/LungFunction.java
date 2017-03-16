package vn.hust.soict.lung_function.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.hust.soict.lung_function.net.RestRequest;

/**
 * Created by tulc on 15/03/2017.
 */
public class LungFunction {
    private double mPEF;
    private double mFEV1;
    private double mFVC;
    private double mDeltaVolume;
    private List<Double> mFlowVolume;

    public LungFunction() {
        mFlowVolume = new ArrayList<>();
    }

    public double getPEF() {
        return mPEF;
    }

    public void setPEF(double mPEF) {
        this.mPEF = mPEF;
    }

    public double getFEV1() {
        return mFEV1;
    }

    public void setFEV1(double mFEV1) {
        this.mFEV1 = mFEV1;
    }

    public double getFVC() {
        return mFVC;
    }

    public void setFVC(double mFVC) {
        this.mFVC = mFVC;
    }

    public double getDeltaVolume() {
        return mDeltaVolume;
    }

    public void setDeltaVolume(float mDeltaVolume) {
        this.mDeltaVolume = mDeltaVolume;
    }

    public List<Double> getFlowVolume() {
        return mFlowVolume;
    }

    public void setFlowVolume(List<Double> mFlowVolume) {
        this.mFlowVolume = mFlowVolume;
    }

    public void parse(JSONObject jsonObject) {
        try {
            Log.e(">>>>", jsonObject.toString());
            mPEF = (float) jsonObject.getDouble("PEF");
            mFEV1 = (float) jsonObject.getDouble("FEV1");
            mFVC = (float) jsonObject.getDouble("FVC");
            mDeltaVolume = (float) jsonObject.getDouble("DeltaVolume");
            JSONArray data = jsonObject.getJSONArray("FlowVolume");

            if (data == null || data.length() == 0) return;

            for (int i = 0; i < data.length(); i++) {
                mFlowVolume.add(data.getDouble(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
