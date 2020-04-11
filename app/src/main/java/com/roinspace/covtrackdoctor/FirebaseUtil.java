/*
 * CovTrack - an app logging Bluetooth devices in your vicinity to monitor infection progress of COVID-19
 * Copyright (C) 2020  Romanian InSpace Engineering

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package com.roinspace.covtrackdoctor;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUtil {

    public static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://covid-c1101.firebaseio.com/");

    public static void addPatient(Patient patient) {
        DatabaseReference rootRef = firebaseDatabase.getReference();
        DatabaseReference patientsRef = rootRef.child("patients").push();

        String key = "mac_addr";
        Map<String, Object> map = new HashMap<>();
        map.put(key, patient.macAddress);
        patientsRef.updateChildren(map);
    }

    public static class Patient {
        private String macAddress;

        public Patient(String macAddress) {
            setName("unimplemented");
            setMacAddress(macAddress);
        }

        public String getMacAddress() {
            return this.macAddress;
        }

        public void setMacAddress(String address) {
            this.macAddress = address;
        }

        public String getName() {
            return null;
        }

        public void setName(String name) {
        }

        @Override
        public String toString() {
            return getMacAddress();
        }

    }
}
