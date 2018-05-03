package vn.hust.soict.lung_function.data;

import vn.hust.soict.lung_function.model.LungFunction;

/**
 * Created by tulc on 15/03/2017.
 */
public class AppData {
    private static AppData mAppData;

    private LungFunction mLungFunction;

    public static synchronized AppData getInstance() {
        if (mAppData == null) {
            mAppData = new AppData();
        }
        return mAppData;
    }

    public LungFunction getLungFunction() {
        return mLungFunction;
    }

    public void setLungFunction(LungFunction mLungFunction) {
        this.mLungFunction = mLungFunction;
    }
}
