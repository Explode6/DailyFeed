package com.example.rssreader.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @ClassName:  WXEntryActivity
 * @Author:  Von
 * @Date:  2021/5/7
 * @Description:  处理分享后微信返回的信息，注意包名和类名不能改！！
*/

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IWXAPI api = WXAPIFactory.createWXAPI(this, WXShareUtil.APP_ID, false);
        //将接收到的Intent及实现了IWXAPIEventHandler接口的对象传递给IWXAPI接口的handleIntent方法
        api.handleIntent(getIntent(),this);
        finish();
    }

    //处理微信发送来的信息
    @Override
    public void onReq(BaseReq baseReq) {

    }

    //处理微信响应并回调的信息
    @Override
    public void onResp(BaseResp baseResp) {
        String result;
        switch (baseResp.errCode){
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = null;
                break;
            case BaseResp.ErrCode.ERR_OK:
                result = "分享成功";
                break;
            default:
                result = "分享失败";
                break;
        }
        if (result != null){
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        }
    }
}