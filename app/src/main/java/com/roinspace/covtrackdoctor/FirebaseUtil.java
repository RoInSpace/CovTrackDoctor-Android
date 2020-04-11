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
