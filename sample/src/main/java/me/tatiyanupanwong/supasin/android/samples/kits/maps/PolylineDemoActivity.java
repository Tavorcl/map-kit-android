/*
 * Copyright (C) 2020 Supasin Tatiyanupanwong
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.tatiyanupanwong.supasin.android.samples.kits.maps;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

import me.tatiyanupanwong.supasin.android.libraries.kits.maps.MapFragment;
import me.tatiyanupanwong.supasin.android.libraries.kits.maps.MapKit;
import me.tatiyanupanwong.supasin.android.libraries.kits.maps.model.Cap;
import me.tatiyanupanwong.supasin.android.libraries.kits.maps.model.Dash;
import me.tatiyanupanwong.supasin.android.libraries.kits.maps.model.Dot;
import me.tatiyanupanwong.supasin.android.libraries.kits.maps.model.Gap;
import me.tatiyanupanwong.supasin.android.libraries.kits.maps.model.JointType;
import me.tatiyanupanwong.supasin.android.libraries.kits.maps.model.LatLng;
import me.tatiyanupanwong.supasin.android.libraries.kits.maps.model.MapClient;
import me.tatiyanupanwong.supasin.android.libraries.kits.maps.model.PatternItem;
import me.tatiyanupanwong.supasin.android.libraries.kits.maps.model.Polyline;

/**
 * This shows how to draw polylines on a map.
 */
public class PolylineDemoActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        SeekBar.OnSeekBarChangeListener,
        MapClient.Factory.OnMapReadyCallback {

    // City locations for mutable polyline.
    private static final LatLng ADELAIDE = MapKit.getFactory().newLatLng(-34.92873, 138.59995);
    private static final LatLng DARWIN = MapKit.getFactory().newLatLng(-12.4258647, 130.7932231);
    private static final LatLng MELBOURNE = MapKit.getFactory().newLatLng(-37.81319, 144.96298);
    private static final LatLng PERTH = MapKit.getFactory().newLatLng(-31.95285, 115.85734);

    // Airport locations for geodesic polyline.
    private static final LatLng AKL = MapKit.getFactory().newLatLng(-37.006254, 174.783018);
    private static final LatLng JFK = MapKit.getFactory().newLatLng(40.641051, -73.777485);
    private static final LatLng LAX = MapKit.getFactory().newLatLng(33.936524, -118.377686);
    private static final LatLng LHR = MapKit.getFactory().newLatLng(51.471547, -0.460052);

    private static final int MAX_WIDTH_PX = 100;
    private static final int MAX_HUE_DEGREES = 360;
    private static final int MAX_ALPHA = 255;
    private static final int CUSTOM_CAP_IMAGE_REF_WIDTH_PX = 50;
    private static final int INITIAL_STROKE_WIDTH_PX = 5;

    private static final int PATTERN_DASH_LENGTH_PX = 50;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final Dot DOT = MapKit.getFactory().newDot();
    private static final Dash DASH = MapKit.getFactory().newDash(PATTERN_DASH_LENGTH_PX);
    private static final Gap GAP = MapKit.getFactory().newGap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_DOTTED = Arrays.asList(DOT, GAP);
    private static final List<PatternItem> PATTERN_DASHED = Arrays.asList(DASH, GAP);
    private static final List<PatternItem> PATTERN_MIXED = Arrays.asList(DOT, GAP, DOT, DASH, GAP);

    private Polyline mMutablePolyline;
    private SeekBar mHueBar;
    private SeekBar mAlphaBar;
    private SeekBar mWidthBar;
    private Spinner mStartCapSpinner;
    private Spinner mEndCapSpinner;
    private Spinner mJointTypeSpinner;
    private Spinner mPatternSpinner;
    private CheckBox mClickabilityCheckbox;

    // These are the options for polyline caps, joints and patterns. We use their
    // string resource IDs as identifiers.

    private static final int[] CAP_TYPE_NAME_RESOURCE_IDS = {
            R.string.cap_butt, // Default
            R.string.cap_round,
            R.string.cap_square,
            R.string.cap_image,
    };

    private static final int[] JOINT_TYPE_NAME_RESOURCE_IDS = {
            R.string.joint_type_default, // Default
            R.string.joint_type_bevel,
            R.string.joint_type_round,
    };

    private static final int[] PATTERN_TYPE_NAME_RESOURCE_IDS = {
            R.string.pattern_solid, // Default
            R.string.pattern_dashed,
            R.string.pattern_dotted,
            R.string.pattern_mixed,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polyline_demo);

        mHueBar = findViewById(R.id.hueSeekBar);
        mHueBar.setMax(MAX_HUE_DEGREES);
        mHueBar.setProgress(0);

        mAlphaBar = findViewById(R.id.alphaSeekBar);
        mAlphaBar.setMax(MAX_ALPHA);
        mAlphaBar.setProgress(MAX_ALPHA);

        mWidthBar = findViewById(R.id.widthSeekBar);
        mWidthBar.setMax(MAX_WIDTH_PX);
        mWidthBar.setProgress(MAX_WIDTH_PX / 2);

        mStartCapSpinner = findViewById(R.id.startCapSpinner);
        mStartCapSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(CAP_TYPE_NAME_RESOURCE_IDS)));

        mEndCapSpinner = findViewById(R.id.endCapSpinner);
        mEndCapSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(CAP_TYPE_NAME_RESOURCE_IDS)));

        mJointTypeSpinner = findViewById(R.id.jointTypeSpinner);
        mJointTypeSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(JOINT_TYPE_NAME_RESOURCE_IDS)));

        mPatternSpinner = findViewById(R.id.patternSpinner);
        mPatternSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(PATTERN_TYPE_NAME_RESOURCE_IDS)));

        mClickabilityCheckbox = findViewById(R.id.toggleClickability);

        MapFragment mapFragment =
                (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private String[] getResourceStrings(int[] resourceIds) {
        String[] strings = new String[resourceIds.length];
        for (int i = 0; i < resourceIds.length; i++) {
            strings[i] = getString(resourceIds[i]);
        }
        return strings;
    }

    @Override
    public void onMapReady(@NonNull MapClient map) {

        // Override the default content description on the view, for accessibility mode.
        map.setContentDescription(getString(R.string.polyline_demo_description));

        // A geodesic polyline that goes around the world.
        map.addPolyline(MapKit.getFactory().newPolylineOptions()
                .add(LHR, AKL, LAX, JFK, LHR)
                .width(INITIAL_STROKE_WIDTH_PX)
                .color(Color.BLUE)
                .geodesic(true)
                .clickable(mClickabilityCheckbox.isChecked()));

        // A simple polyline across Australia. This polyline will be mutable.
        int color = Color.HSVToColor(
                mAlphaBar.getProgress(), new float[]{mHueBar.getProgress(), 1, 1});
        mMutablePolyline = map.addPolyline(MapKit.getFactory().newPolylineOptions()
                .color(color)
                .width(mWidthBar.getProgress())
                .clickable(mClickabilityCheckbox.isChecked())
                .add(MELBOURNE, ADELAIDE, PERTH, DARWIN));

        mHueBar.setOnSeekBarChangeListener(this);
        mAlphaBar.setOnSeekBarChangeListener(this);
        mWidthBar.setOnSeekBarChangeListener(this);

        mStartCapSpinner.setOnItemSelectedListener(this);
        mEndCapSpinner.setOnItemSelectedListener(this);
        mJointTypeSpinner.setOnItemSelectedListener(this);
        mPatternSpinner.setOnItemSelectedListener(this);

        mMutablePolyline.setStartCap(
                getSelectedCap(mStartCapSpinner.getSelectedItemPosition()));
        mMutablePolyline.setEndCap(
                getSelectedCap(mEndCapSpinner.getSelectedItemPosition()));
        mMutablePolyline.setJointType(
                getSelectedJointType(mJointTypeSpinner.getSelectedItemPosition()));
        mMutablePolyline.setPattern(
                getSelectedPattern(mPatternSpinner.getSelectedItemPosition()));

        // Move the map so that it is centered on the mutable polyline.
        map.moveCamera(MapKit.getFactory().getCameraUpdateFactory().newLatLngZoom(MELBOURNE, 3));

        // Add a listener for polyline clicks that changes the clicked polyline's color.
        map.setOnPolylineClickListener(new MapClient.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(@NonNull Polyline polyline) {
                // Flip the values of the red, green and blue components of the polyline's color.
                polyline.setColor(polyline.getColor() ^ 0x00ffffff);
            }
        });
    }

    private Cap getSelectedCap(int pos) {
        switch (CAP_TYPE_NAME_RESOURCE_IDS[pos]) {
            case R.string.cap_butt:
                return MapKit.getFactory().newButtCap();
            case R.string.cap_square:
                return MapKit.getFactory().newSquareCap();
            case R.string.cap_round:
                return MapKit.getFactory().newRoundCap();
            case R.string.cap_image:
                return MapKit.getFactory().newCustomCap(
                        MapKit.getFactory().getBitmapDescriptorFactory()
                                .fromResource(R.drawable.chevron), CUSTOM_CAP_IMAGE_REF_WIDTH_PX);
        }
        return null;
    }

    private int getSelectedJointType(int pos) {
        switch (JOINT_TYPE_NAME_RESOURCE_IDS[pos]) {
            case R.string.joint_type_bevel:
                return JointType.BEVEL;
            case R.string.joint_type_round:
                return JointType.ROUND;
            case R.string.joint_type_default:
                return JointType.DEFAULT;
        }
        return 0;
    }

    private List<PatternItem> getSelectedPattern(int pos) {
        switch (PATTERN_TYPE_NAME_RESOURCE_IDS[pos]) {
            case R.string.pattern_dotted:
                return PATTERN_DOTTED;
            case R.string.pattern_dashed:
                return PATTERN_DASHED;
            case R.string.pattern_mixed:
                return PATTERN_MIXED;
            default:
                return null;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch (parent.getId()) {
            case R.id.startCapSpinner:
                mMutablePolyline.setStartCap(getSelectedCap(pos));
                break;
            case R.id.endCapSpinner:
                mMutablePolyline.setEndCap(getSelectedCap(pos));
                break;
            case R.id.jointTypeSpinner:
                mMutablePolyline.setJointType(getSelectedJointType(pos));
                break;
            case R.id.patternSpinner:
                mMutablePolyline.setPattern(getSelectedPattern(pos));
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Don't do anything here.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mMutablePolyline == null) {
            return;
        }

        if (seekBar == mHueBar) {
            mMutablePolyline.setColor(Color.HSVToColor(
                    Color.alpha(mMutablePolyline.getColor()), new float[]{progress, 1, 1}));
        } else if (seekBar == mAlphaBar) {
            float[] prevHSV = new float[3];
            Color.colorToHSV(mMutablePolyline.getColor(), prevHSV);
            mMutablePolyline.setColor(Color.HSVToColor(progress, prevHSV));
        } else if (seekBar == mWidthBar) {
            mMutablePolyline.setWidth(progress);
        }
    }

    /**
     * Toggles the clickability of the polyline based on the state of the View that triggered this
     * call.
     * This callback is defined on the CheckBox in the layout for this Activity.
     */
    public void toggleClickability(View view) {
        if (mMutablePolyline != null) {
            mMutablePolyline.setClickable(((CheckBox) view).isChecked());
        }
    }

}
