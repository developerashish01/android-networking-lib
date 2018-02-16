package sg.okhttplibrary;


import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.util.Log;

import io.realm.Realm;


public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    public void refresh() {
        realm.refresh();
    }

    public void clearAll() {
        realm.beginTransaction();
        realm.clear(CacheResponse.class);
        realm.commitTransaction();
    }

    public CacheResponse getResponse(String hash) {
        return realm.where(CacheResponse.class).equalTo("hash", hash).findFirst();
    }

    public CacheResponse getCachedResponse(String hash) {

        return realm.where(CacheResponse.class)
                .contains("hash", hash)
                .findFirst();
    }

    public void size() {

        int size = realm.where(CacheResponse.class).findAll().size();
        Log.e("Size", String.valueOf(size));
    }

}