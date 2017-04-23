package com.jemetech.jeme_ifbg;

/**
 * Created by Lei on 11/27/2016.
 */

public interface ConnectionListerner {
    void onSuccess();
    void onFail(Reason reason);
    static enum Reason {
        TIMEOUT, REFUSED
    }
}
