package vn.hust.soict.lung_function;

import android.app.Application;

import vn.hust.soict.lung_function.utils.FontUtils;

/**
 * Created by tulc on 15/03/2017.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FontUtils.loadFont(getApplicationContext(), "Roboto-Light.ttf");
        FontUtils.loadFontNormal(getApplicationContext(), "Roboto-Regular.ttf");

    }
}
