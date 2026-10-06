/*
 * SPDX-FileCopyrightText: 2016 The CyanogenMod Project
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.setupwizard;

import static org.lineageos.setupwizard.SetupWizardApp.ACTION_EMERGENCY_DIAL;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.setupcompat.template.FooterButtonStyleUtils;
import com.google.android.setupcompat.util.SystemBarHelper;

import org.lineageos.setupwizard.util.SetupWizardUtils;

public class WelcomeActivity extends SubBaseActivity {

    private static final String ACTION_ACCESSIBILITY_SETTINGS =
            "android.settings.ACCESSIBILITY_SETTINGS_FOR_SUW";

    // The default welcome composition needs a tall window. Below this height the
    // Glif header is an empty band, because Welcome sets no header title. Leave
    // it in place at the 600dp two-pane width, where that header is the left pane.
    private static final int SHORT_WELCOME_HEIGHT_DP = 480;
    private static final int TWO_PANE_WIDTH_DP = 600;

    @Override
    protected void onStartSubactivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideEmptyHeaderOnShortWindow();
        onSetupStart();
        SystemBarHelper.setBackButtonVisible(getWindow(), false);
        setNextText(R.string.start);
        Button startButton = findViewById(R.id.start);
        Button emergButton = findViewById(R.id.emerg_dialer);
        Button skipButton = findViewById(R.id.skip);
        startButton.setOnClickListener(view -> onNextPressed());
        findViewById(R.id.launch_accessibility)
                .setOnClickListener(
                        view -> startSubactivity(new Intent(ACTION_ACCESSIBILITY_SETTINGS)));

        FooterButtonStyleUtils.applyPrimaryButtonPartnerResource(this, startButton, true);

        if (SetupWizardUtils.hasTelephony(this)) {
            setSkipText(R.string.emergency_call);
            emergButton.setOnClickListener(
                    view -> startSubactivity(new Intent(ACTION_EMERGENCY_DIAL)));

            FooterButtonStyleUtils.applySecondaryButtonPartnerResource(this, emergButton, true);
        } else {
            emergButton.setVisibility(View.GONE);
        }

        TextView welcomeTitle = findViewById(R.id.welcome_title);
        if (SetupWizardUtils.isManagedProfile(this)) {
            welcomeTitle.setText(getString(R.string.setup_managed_profile_welcome_message));
        } else {
            welcomeTitle.setText(getString(R.string.setup_welcome_message,
                    getString(R.string.os_name)));
        }

        if (Build.TYPE.equals("eng")) {
            skipButton.setVisibility(View.VISIBLE);
            skipButton.setOnClickListener(v -> {
                SetupWizardUtils.finishSetupWizard(WelcomeActivity.this);
            });
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void hideEmptyHeaderOnShortWindow() {
        Configuration config = getResources().getConfiguration();
        if (config.screenHeightDp >= SHORT_WELCOME_HEIGHT_DP
                || config.screenWidthDp >= TWO_PANE_WIDTH_DP) {
            return;
        }
        View header = findViewById(com.google.android.setupdesign.R.id.sud_layout_header);
        if (header != null) {
            header.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.welcome_activity;
    }

    @Override
    protected int getTitleResId() {
        return -1;
    }
}
