package com.flamingolive.live.bean;

import com.flamingolive.common.bean.UserBean;

/**
 * Created by cxf on 2019/4/27.
 */

public class LiveShutUpBean extends UserBean {
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
        this.id = uid;
    }
}
