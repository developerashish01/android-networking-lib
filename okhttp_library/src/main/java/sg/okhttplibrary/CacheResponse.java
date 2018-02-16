package sg.okhttplibrary;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CacheResponse extends RealmObject {

    @PrimaryKey
    private int id;

    private String hash;

    private String response;

    private long time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}