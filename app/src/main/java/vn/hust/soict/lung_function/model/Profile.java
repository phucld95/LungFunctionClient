package vn.hust.soict.lung_function.model;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;

import vn.hust.soict.lung_function.config.AppConstant;

/**
 * Created by tulc on 15/03/2017.
 */
public class Profile {
    public static final int REGION_NORTHEN = 0;
    public static final int REGION_CENTRAL = 1;
    public static final int REGION_SOUTH = 2;

    private String name;
    private boolean isMale;
    private String birthDay;
    private int weight;
    private int height;
    private int region;
    private boolean isSmoking;

    public Profile() {

    }

    public Profile(RealmProfile realmProfile) {
        name = realmProfile.getName();
        isMale = realmProfile.isMale();
        birthDay = realmProfile.getBirthDay();
        weight = realmProfile.getWeight();
        height = realmProfile.getHeight();
        region = realmProfile.getRegion();
        isSmoking = realmProfile.isSmoking();
    }

    public void parse(RealmProfile realmProfile) {
        name = realmProfile.getName();
        isMale = realmProfile.isMale();
        birthDay = realmProfile.getBirthDay();
        weight = realmProfile.getWeight();
        height = realmProfile.getHeight();
        region = realmProfile.getRegion();
        isSmoking = realmProfile.isSmoking();
    }

    public RealmProfile getRealmProfile() {
        RealmProfile realmProfile = new RealmProfile();
        realmProfile.setId(getID());
        realmProfile.setName(name);
        realmProfile.setBirthDay(birthDay);
        realmProfile.setMale(isMale);
        realmProfile.setHeight(height);
        realmProfile.setWeight(weight);
        realmProfile.setRegion(region);
        realmProfile.setSmoking(isSmoking);
        return realmProfile;
    }

    public String getID() {
        String rawID = name + birthDay + isMale;
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(rawID.getBytes());
            byte messageDigest[] = digest.digest();

            String id = Base64.encodeToString(messageDigest, Base64.DEFAULT).trim();

            return id;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return rawID;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public int getAge() {
        try {
            Date birthDay = AppConstant.dateFormat.parse(this.birthDay);
            Date now = new Date();
            int age = (now.getYear() - birthDay.getYear());
            if ((birthDay.getMonth() > now.getMonth()) ||
                    (birthDay.getMonth() == now.getMonth() && birthDay.getDate() > now.getDate())) {
                age -= 1;
            }
            return age;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public boolean isSmoking() {
        return isSmoking;
    }

    public void setSmoking(boolean smoking) {
        isSmoking = smoking;
    }
}
