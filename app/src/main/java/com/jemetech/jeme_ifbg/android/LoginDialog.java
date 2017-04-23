package com.jemetech.jeme_ifbg.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;

import com.jemetech.jeme_ifbg.R;

/**
 * Created by Lei on 11/27/2016.
 */

public class LoginDialog extends DialogFragment {

    private String host;
    private int port;
    private Button connectButton;
    private boolean connect = false;
    private AlertDialog dialog;
    private MainActivity activity;
    boolean initiallized;

    public boolean isConnect() {
        return connect;
    }

    public int getPort() {
        return port;
    }

    public String getTheHost() {
        return host;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loginview, null)).setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                connect = true;
                dismiss();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                connect = false;
                dismiss();
            }
        }).setTitle(R.string.title);
        AlertDialog dialog = builder.create();
        this.dialog = dialog;
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!initiallized) {
            connectButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            connectButton.setEnabled(false);
            EditText servertxt = (EditText) dialog.findViewById(R.id.serveraddr);
            servertxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    host = s.toString();
                    checkButton();
                }
            });
            EditText porttxt = (EditText) dialog.findViewById(R.id.port);
            porttxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String value = s.toString();
                    port = value.isEmpty()? -1 : Integer.parseInt(value);
                    checkButton();
                }
            });
            SharedPreferences pref = activity.getSharedPreferences(MainActivity.key, 0);
            String host = pref.getString(MainActivity.host, "");
            if (!host.isEmpty())
                servertxt.setText(host);
            int port = pref.getInt(MainActivity.port, 0);
            if(port > 0)
                porttxt.setText(String.valueOf(port));
            initiallized = true;
        }
    }

    private void checkButton() {
        if(!host.isEmpty() && port > 0 && port < 65536)
            connectButton.setEnabled(true);
        else
            connectButton.setEnabled(false);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(connect) {
            SharedPreferences.Editor editor = activity.getSharedPreferences(MainActivity.key, 0).edit();
            editor.putString(MainActivity.host, host);
            editor.putInt(MainActivity.port, port);
            editor.apply();
        }
        activity.onLoginDismiss(this);
    }

    void setActivity(MainActivity activity) {
        this.activity = activity;
    }
}
