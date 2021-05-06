// DataCallback.aidl
package com.example.rssreader.model.parse;


interface DataCallback {

    void onSuccess();

    void onFailure();

    void onError();
}