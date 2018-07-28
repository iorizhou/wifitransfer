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
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.xiaojiutech.wifitransfer.R;
import com.xiaojiutech.wifitransfer.mvp.activity.ReceiveFileActivity;
import com.xiaojiutech.wifitransfer.mvp.activity.SendFileActivity;

import static com.xiaojiutech.wifitransfer.mvp.activity.MainActivity.TAG;

public class FunctionFragment extends BaseFragment implements View.OnClickListener{
    private Button mSendBtn,mRecBtn;
    public AdView mTopBannerAd,mBottomAd1,mBottomAd2;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_function,container,false);
        mSendBtn = (Button)view.findViewById(R.id.sendFile);
        mRecBtn = (Button)view.findViewById(R.id.recFile);
        mSendBtn.setOnClickListener(this);
        mRecBtn.setOnClickListener(this);
        mTopBannerAd = (AdView)view.findViewById(R.id.top_banner_ad);
        mBottomAd1 = (AdView)view.findViewById(R.id.bottom_banner_ad1);
        mBottomAd2 = (AdView)view.findViewById(R.id.bottom_banner_ad2);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showInterstitialAds();
        showBannerAd();
        showVideoAd();
    }

    private void showBannerAd(){
        Log.i("admob","funtion loadAndShowBannerAD");
        loadBannerAd(new AdListener(){
            @Override
            public void onAdLoaded() {
                Log.i("admob","function_top_banner_ad loaded");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.i("admob","function_top_banner_ad onAdFailedToLoad = "+i);
            }
        },mTopBannerAd);

        loadBannerAd(new AdListener(){
            @Override
            public void onAdLoaded() {
                Log.i("admob","function_bottom_banner_ad1 loaded");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.i("admob","function_bottom_banner_ad1 onAdFailedToLoad = "+i);
            }
        },mBottomAd1);

        loadBannerAd(new AdListener(){
            @Override
            public void onAdLoaded() {
                Log.i("admob","function_bottom_banner_ad2 loaded");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.i("admob","function_bottom_banner_ad2 onAdFailedToLoad = "+i);
            }
        },mBottomAd2);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mInterstitialAd.isLoaded()){
            Log.i(TAG,"show interad");
            mInterstitialAd.show();
        }
    }

    public void showVideoAd(){
        loadVideoAd(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                mVideoAd.show();
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {
                Log.i("admob","onRewardedVideoCompleted");
            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Log.i("admob","onRewardedVideoAdFailedToLoad = "+i);
            }

            @Override
            public void onRewardedVideoCompleted() {
                Log.i("admob","onRewardedVideoCompleted");
            }
        });
    }

    private void showInterstitialAds(){
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

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
