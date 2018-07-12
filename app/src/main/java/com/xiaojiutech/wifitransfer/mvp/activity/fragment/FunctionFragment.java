package com.xiaojiutech.wifitransfer.mvp.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.xiaojiutech.wifitransfer.R;
import com.xiaojiutech.wifitransfer.mvp.activity.MainActivity;
import com.xiaojiutech.wifitransfer.mvp.activity.ReceiveFileActivity;
import com.xiaojiutech.wifitransfer.mvp.activity.SendFileActivity;

public class FunctionFragment extends BaseFragment implements View.OnClickListener{
    private Button mSendBtn,mRecBtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main,container,false);
        mSendBtn = (Button)view.findViewById(R.id.sendFile);
        mRecBtn = (Button)view.findViewById(R.id.recFile);
        mSendBtn.setOnClickListener(this);
        mRecBtn.setOnClickListener(this);
        mBannerAd = (AdView)view.findViewById(R.id.adView);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showInterstitialAds();
        loadAndShowBannerAD();
    }

    private void loadAndShowBannerAD(){
        loadBannerAd(new AdListener(){
            @Override
            public void onAdLoaded() {
                Log.i("banner_ad","loaded");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.i("banner_ad","onAdFailedToLoad = "+i);
            }
        });
    }
    private void showInterstitialAds(){
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.e("admob","onAdFailedToLoad = "+i);
            }
        });
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sendFile:
                sendFile(view);
                break;
            case R.id.recFile:
                receiveFile(view);
                break;
        }
    }

    public void sendFile(View v){
        startActivity(new Intent(getActivity(),SendFileActivity.class));
    }

    public void receiveFile(View v){
        startActivity(new Intent(getActivity(),ReceiveFileActivity.class));
    }
}
