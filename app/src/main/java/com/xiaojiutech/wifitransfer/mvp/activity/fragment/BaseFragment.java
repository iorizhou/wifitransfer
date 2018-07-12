package com.xiaojiutech.wifitransfer.mvp.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.xiaojiutech.wifitransfer.utils.AdmobConstants;

public class BaseFragment extends Fragment {
    public InterstitialAd mInterstitialAd;
    public AdView mBannerAd;
    public RewardedVideoAd mVideoAd;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initInterstitialAd();
        super.onViewCreated(view, savedInstanceState);
    }

    private void  initInterstitialAd(){
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(AdmobConstants.INTERSTIALAD);
    }

    public void loadBannerAd(AdListener listener){
        AdRequest request = new AdRequest.Builder().build();
//        mBannerAd.setAdUnitId(AdmobConstants.BANNERAD);
//        mBannerAd.setAdSize(AdSize.SMART_BANNER);
        mBannerAd.setAdListener(listener);
        mBannerAd.loadAd(request);
    }

    public void loadVideoAd(RewardedVideoAdListener listener){
        mVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mVideoAd.setRewardedVideoAdListener(listener);
        mVideoAd.loadAd(AdmobConstants.REWARDAD,new AdRequest.Builder().build());
    }
}
