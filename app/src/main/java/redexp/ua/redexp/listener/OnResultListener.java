package redexp.ua.redexp.listener;

import android.content.Intent;

/**
 * Interface definition for a class that will be called
 * when fragment sends result to parent activity or fragment
 * <p/>
 */
public interface OnResultListener {
    void sendResult(int resultCode, Intent data);
}
