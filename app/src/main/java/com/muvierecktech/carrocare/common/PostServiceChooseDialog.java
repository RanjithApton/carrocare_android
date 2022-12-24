package com.muvierecktech.carrocare.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.muvierecktech.carrocare.R;

public class PostServiceChooseDialog extends DialogFragment {
    AlertPositiveListener alertPositiveListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            alertPositiveListener = (AlertPositiveListener) activity;
        } catch (ClassCastException e) {
            // throw new ClassCastException(activity.toString() + " must implement AlertPositiveListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        arrayAdapter.add(getString(R.string.action_apartment));
        arrayAdapter.add(getString(R.string.action_doorstep));
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    alertPositiveListener.apartmentSevice();
                } else if (which != 1) {
                    alertPositiveListener.doorstepService();
                } else {
                    alertPositiveListener.doorstepService();
                }
            }
        });
        return builderSingle.create();
    }

    public interface AlertPositiveListener {
        void apartmentSevice();

        void doorstepService();
    }
}
