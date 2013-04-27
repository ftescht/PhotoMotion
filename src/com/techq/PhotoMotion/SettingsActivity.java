package com.techq.PhotoMotion;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.techq.PhotoMotion.data.Global;
import com.techq.PhotoMotion.data.Preferences;

public class SettingsActivity extends Activity {

    private EditText pixelThresholdEditText;
    private EditText thresholdPercentEditText;
    private TextView thresholdTextVew;
    private EditText pictureDelayEditText;
    private EditText frameDelayEditText;
    private CheckBox autostartCheckBox;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.preferences);

        pixelThresholdEditText = (EditText) findViewById(R.id.pixelThresholdEditText);
        pixelThresholdEditText.setText(Integer.toString(Preferences.PIXEL_THRESHOLD));
        pixelThresholdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    pixelThresholdEditText.setText("5");
                    return;
                }
                int value = Integer.parseInt(editable.toString());
                if (value < 5) {
                    pixelThresholdEditText.setText("5");
                    return;
                }
                Preferences.PIXEL_THRESHOLD = value;
            }
        });

        thresholdPercentEditText = (EditText) findViewById(R.id.thresholdPercentEditText);
        thresholdPercentEditText.setText(Integer.toString(Preferences.PICTURE_THRESHOLD_PERCENT));
        thresholdPercentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    thresholdPercentEditText.setText("5");
                    return;
                }
                int value = Integer.parseInt(editable.toString());
                if (value < 5) {
                    thresholdPercentEditText.setText("5");
                    return;
                }

                Preferences.PICTURE_THRESHOLD_PERCENT = value;
                thresholdTextVew.setText(Integer.toString(Preferences.PICTURE_THRESHOLD()) + "/" + Preferences.PREVIEW_SIZE() + " pixels");
            }
        });

        thresholdTextVew = (TextView) findViewById(R.id.thresholdTextView);

        pictureDelayEditText = (EditText) findViewById(R.id.pictureDelayEditText);
        pictureDelayEditText.setText(Integer.toString(Preferences.PICTURE_DELAY));
        pictureDelayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < 4) {
                    pictureDelayEditText.setText("1000");
                    return;
                }
                int value = Integer.parseInt(editable.toString());
                if (value < 1000) {
                    pictureDelayEditText.setText("1000");
                    return;
                }
                Preferences.PICTURE_DELAY = value;
            }
        });

        frameDelayEditText = (EditText) findViewById(R.id.frameDelayEditText);
        frameDelayEditText.setText(Integer.toString(Preferences.PREVIEW_DELAY));
        frameDelayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < 3) {
                    frameDelayEditText.setText("100");
                    return;
                }
                int value = Integer.parseInt(editable.toString());
                if (value < 100) {
                    frameDelayEditText.setText("100");
                    return;
                }
                Preferences.PREVIEW_DELAY = value;
            }
        });

        autostartCheckBox = (CheckBox) findViewById(R.id.autostartCheckBox);
        autostartCheckBox.setChecked(Preferences.AUTOSTART_SERVICE);
    }

    public void onDestroy() {
        super.onDestroy();
        if (Global.SavePreferences()) {
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void autostartCheckBoxOnClick(View view) {
        Preferences.AUTOSTART_SERVICE = autostartCheckBox.isChecked();
    }
}
