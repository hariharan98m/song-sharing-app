package io.hasura.songapp;

/**
 * Created by HARIHARAN on 15-04-2018.
 */

public class ViewPagerFriends {
    String name;
    String desc;
    boolean reqORconfirm;

    public ViewPagerFriends(String name, String desc, boolean reqORconfirm) {
        this.name = name;
        this.desc = desc;
        this.reqORconfirm = reqORconfirm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean getReqORconfirm() {
        return reqORconfirm;
    }

    public void setReqORconfirm(boolean reqORconfirm) {
        this.reqORconfirm = reqORconfirm;
    }
}
