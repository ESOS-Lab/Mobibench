package kr.ac.hanyang.esos.mobibench.common;

import android.arch.lifecycle.MutableLiveData;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import kr.ac.hanyang.esos.mobibench.R;

public class BindingAdapter {

    @android.databinding.BindingAdapter(value = "onClickCheckbox")
    public static void setCheckbox(TextView view, final MutableLiveData<Boolean> liveData) {
        if (liveData == null) {
            return;
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liveData.getValue() == null || !liveData.getValue()) {
                    liveData.setValue(true);
                } else {
                    liveData.setValue(false);
                }
            }
        });
    }

    @android.databinding.BindingAdapter(value = "imageCheckBox")
    public static void setImageCheckbox(TextView view, final MutableLiveData<Boolean> liveData) {
        if (liveData.getValue() == null || !liveData.getValue()) {
            view.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_checkbox_false), null, null, null);
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_checkbox_true), null, null, null);
        }
    }
}
