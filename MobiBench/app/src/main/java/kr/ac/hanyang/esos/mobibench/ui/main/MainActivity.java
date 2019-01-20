package kr.ac.hanyang.esos.mobibench.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import kr.ac.hanyang.esos.mobibench.BR;
import kr.ac.hanyang.esos.mobibench.R;
import kr.ac.hanyang.esos.mobibench.databinding.ActivityMainBinding;
import kr.ac.hanyang.esos.mobibench.ui.setting.SettingActivity;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);

        viewModel = ViewModelProviders.of(this)
                .get(MainViewModel.class);
        binding.setVariable(BR.viewModel, viewModel);
        binding.setVariable(BR.clickHandler, new ClickHandler());

        viewModel.getCheckSQLite().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                checkAll();
            }
        });
        viewModel.getCheckFileIO().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                checkAll();
            }
        });
    }

    private void checkAll() {
        if (viewModel.getCheckFileIO().getValue() != null && viewModel.getCheckFileIO().getValue()
                && viewModel.getCheckSQLite().getValue() != null && viewModel.getCheckSQLite().getValue()) {
            // true, true
            binding.mainTvButtonAll.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_checkbox_true), null, null, null);
        } else {
            binding.mainTvButtonAll.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_checkbox_false), null, null, null);
        }
    }

    public class ClickHandler {
        public void onClickAll(View view) {
            if (viewModel.getCheckFileIO().getValue() != null && viewModel.getCheckFileIO().getValue()
                    && viewModel.getCheckSQLite().getValue() != null && viewModel.getCheckSQLite().getValue()) {
                // true, true
                viewModel.getCheckFileIO().setValue(false);
                viewModel.getCheckSQLite().setValue(false);
            } else {
                viewModel.getCheckFileIO().setValue(true);
                viewModel.getCheckSQLite().setValue(true);
            }
        }

        public void onClickSetting(View view) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        }

        public void onClickDrawer(View view) {
            binding.drawerLayout.openDrawer(binding.navigationView);
        }
    }
}
