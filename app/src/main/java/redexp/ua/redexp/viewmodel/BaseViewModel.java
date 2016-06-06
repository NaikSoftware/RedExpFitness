package redexp.ua.redexp.viewmodel;

import android.content.Context;
import android.support.annotation.CallSuper;

public abstract class BaseViewModel implements ViewModel {

    private Context mContext;

    public BaseViewModel(Context context) {
        this.mContext = context;
    }

    @CallSuper
    @Override
    public void onDestroy() {
        mContext = null;
    }

    protected Context getContext() {
        return mContext;
    }

    protected String getString(int resId) {
        return mContext.getResources().getString(resId);
    }
}
