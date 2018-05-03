package vn.hust.soict.lung_function.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import vn.hust.soict.lung_function.model.Profile;
import vn.hust.soict.lung_function.model.RealmProfile;

/**
 * Created by tulc on 06/10/2016.
 */
public class RealmDB {
    private Realm realm;

    public RealmDB() {
        this.realm = Realm.getDefaultInstance();
    }

    public void close() {
        realm.close();
    }

    public void updateProfile(Profile profile) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(profile.getRealmProfile());
        realm.commitTransaction();
    }

    public Profile getProfile(String id) {
        if (realm != null) {
//            RealmProfile result = realm.where(RealmProfile.class).equalTo("name", "le tu").findFirst();
            RealmProfile result = realm.where(RealmProfile.class).equalTo("id", id).findFirst();
            if (result != null)
                return new Profile(result);
        }
        return null;
    }

    public List<Profile> getProfiles() {
        if (realm != null) {
            List<Profile> profiles = new ArrayList<>();
            RealmResults<RealmProfile> results = realm.where(RealmProfile.class).findAll();

            for (RealmProfile profile : results) {
                profiles.add(new Profile(profile));
            }
            return profiles;
        }
        return null;
    }

}
