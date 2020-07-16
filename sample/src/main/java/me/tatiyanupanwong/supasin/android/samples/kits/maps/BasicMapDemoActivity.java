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

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import me.tatiyanupanwong.supasin.android.libraries.kits.maps.MapFragment;
import me.tatiyanupanwong.supasin.android.libraries.kits.maps.MapKit;
import me.tatiyanupanwong.supasin.android.libraries.kits.maps.model.MapClient;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
public class BasicMapDemoActivity extends AppCompatActivity implements
        MapClient.Factory.OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_demo);

        MapFragment mapFragment =
                (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Africa.
     */
    @Override
    public void onMapReady(@NonNull MapClient map) {
        map.addMarker(
                MapKit.getFactory().newMarkerOptions()
                        .position(MapKit.getFactory().newLatLng(0, 0))
                        .title("Marker"));
    }

}
