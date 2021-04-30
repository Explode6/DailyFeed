// DataCallback.aidl
package com.example.testapplication.model.parse;


interface DataCallback {

    void onSuccess();

    void onFailure();

    void onError();
}