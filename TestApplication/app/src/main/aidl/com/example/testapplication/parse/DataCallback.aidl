// DataCallback.aidl
package com.example.testapplication.parse;


interface DataCallback {

    void onSuccess();

    void onFailure();

    void onError();
}