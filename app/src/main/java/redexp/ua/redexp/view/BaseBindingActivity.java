package redexp.ua.redexp.view;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import redexp.ua.redexp.BR;

import redexp.ua.redexp.R;
import redexp.ua.redexp.viewmodel.BaseViewModel;

public abstract class BaseBindingActivity<DB extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity {

    private static final String TAG = "BaseBindingActivity";

    protected Toolbar mToolbar;
    protected DB mBinding;
    protected VM mViewModel;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        mLogger.debug(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, getLayoutResId());
        mBinding.setVariable(BR.viewModel, mViewModel = getViewModel());
        initToolbar();
    }

    protected abstract
    @LayoutRes
    int getLayoutResId();

    protected abstract VM getViewModel();

    protected
    @IdRes
    int getContainerResId() {
        return 0;
    }

    public void replaceFragment(final Fragment _fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(getContainerResId(), _fragment)
                .commit();
    }

    public Fragment getFragmentByTag(final String _tag) {
        return getSupportFragmentManager().findFragmentByTag(_tag);
    }

    public void setTitle(final String _title) {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(_title);
        super.setTitle(_title);
    }

    protected void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    protected void setDisplayHomeAsUpEnabledInToolbar(final boolean _b) {
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(_b);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item != null) {
            if (item.getItemId() == android.R.id.home) {
                onBackPressed();
                return true;
            } else return super.onOptionsItemSelected(item);
        } else return super.onOptionsItemSelected(item);
    }

}
