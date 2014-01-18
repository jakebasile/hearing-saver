/*
 * Copyright 2010-2012 Jake Basile and Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jakebasile.android.hearingsaver;

import java.lang.reflect.Method;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * Handles setup for the hearing saver app, and will start the registration service.
 * @author Jake Basile
 */
public final class SetupActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final VolumeSettings settings = new VolumeSettings(this);
        final Intent serviceIntent = new Intent();
        serviceIntent.setClassName(getPackageName(), RegistrationService.class.getName());
        startService(serviceIntent);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogLayout = getLayoutInflater().inflate(R.layout.activity_setup, null);
        final SeekBar pluggedBar =
            (SeekBar)dialogLayout.findViewById(R.id.activity_setup_seekplugged);
        final SeekBar unpluggedBar =
            (SeekBar)dialogLayout.findViewById(R.id.activity_setup_seekunplugged);
        final CheckBox ringerBox =
            (CheckBox)dialogLayout.findViewById(R.id.activity_setup_checkringer);
        final CheckBox btBox =
            (CheckBox)dialogLayout.findViewById(R.id.activity_setup_btenabled);
        final RadioGroup radioGroup =
            (RadioGroup)dialogLayout.findViewById(R.id.radio_group);
        final CheckBox saveUnpluggedVolCheckBox =
            (CheckBox)dialogLayout.findViewById(R.id.check_box_save_unplug_vol);
        final RadioButton radioMedia = 
            (RadioButton)dialogLayout.findViewById(R.id.radio_button_media);
        final RadioButton radioRing = 
            (RadioButton)dialogLayout.findViewById(R.id.radio_button_ring);
        saveUnpluggedVolCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked)
            {
                settings.setSaveUnplugLevel(isChecked);
                unpluggedBar.setEnabled(!isChecked);
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch(checkedId)
                {
                    case R.id.radio_button_media:
                    {
                        settings.setPluggedLevelRinger(pluggedBar.getProgress() / 100f);
                        settings.setUnpluggedLevelRinger(unpluggedBar.getProgress() / 100f);
                        pluggedBar.setProgress((int)(settings.getPluggedLevel() * 100));
                        unpluggedBar.setProgress((int)(settings.getUnpluggedLevel() * 100));
                        break;
                    }
                    case R.id.radio_button_ring:
                    {
                        settings.setPluggedLevel(pluggedBar.getProgress() / 100f);
                        settings.setUnpluggedLevel(unpluggedBar.getProgress() / 100f);
                        pluggedBar.setProgress((int)(settings.getPluggedLevelRinger() * 100));
                        unpluggedBar
                            .setProgress((int)(settings.getUnpluggedLevelRinger() * 100));
                        break;
                    }
                    default:
                    {
                        throw new IllegalStateException("Wrong id for radio button: " +
                            checkedId);
                    }
                }
            }
        });
        
        pluggedBar.setProgress((int)(settings.getPluggedLevel() * 100));
        unpluggedBar.setProgress((int)(settings.getUnpluggedLevel() * 100));
        saveUnpluggedVolCheckBox.setChecked(settings.getSaveUnplugLevel());
        ringerBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton btn, boolean isChecked)
            {
                radioRing.setEnabled(isChecked);
                if(!isChecked && radioRing.isChecked())
                {
                    radioMedia.setChecked(true);
                }
            }
        });
        ringerBox.setChecked(settings.getEnableRingerControl());
        radioRing.setEnabled(settings.getEnableRingerControl());
        btBox.setChecked(settings.getBluetoothDetectionEnabled());
        /*
         * Needs to be scrolable for lower resolution screens
         */
        ScrollView scrollingView = new ScrollView(getApplicationContext());        
        scrollingView.addView(dialogLayout);
        
        builder.setView(scrollingView);
        builder.setPositiveButton(R.string.set_levels, new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(radioGroup.getCheckedRadioButtonId() == R.id.radio_button_ring)
                {
                    settings.setPluggedLevelRinger(pluggedBar.getProgress() / 100f);
                    settings.setUnpluggedLevelRinger(unpluggedBar.getProgress() / 100f);
                }
                else
                {
                    settings.setPluggedLevel(pluggedBar.getProgress() / 100f);
                    settings.setUnpluggedLevel(unpluggedBar.getProgress() / 100f);
                }
                settings.setEnableRingerControl(ringerBox.isChecked());
                settings.setBluetoothDetectionEnabled(btBox.isChecked());
                settings.setEnabled(true);
                callDataChanged();
                startService(serviceIntent);
                Toast.makeText(SetupActivity.this, R.string.enabled_toast,
                    Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setOnCancelListener(new OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                finish();
            }
        });
        builder.setNegativeButton(R.string.disable, new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                settings.setEnableRingerControl(ringerBox.isChecked());
                settings.setBluetoothDetectionEnabled(btBox.isChecked());
                settings.setEnabled(false);
                callDataChanged();
                startService(serviceIntent);
                Toast.makeText(SetupActivity.this, R.string.disabled_toast,
                    Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.create().show();
    }

    private void callDataChanged()
    {
        try
        {
            Class<?> bmgr = Class.forName("android.app.backup.BackupManager");
            if(bmgr != null)
            {
                Method dataChanged = bmgr.getMethod("dataChanged", String.class);
                if(dataChanged != null)
                {
                    dataChanged.invoke(null, "com.jakebasile.android.hearingsaver");
                }
            }
        }
        catch(Exception e)
        {
            Log.e("Hearing Saver", e.getMessage());
        }
    }
}
