package com.xiaojiutect.wifitransfer.mvp.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xiaojiutect.wifitransfer.R;
import com.xiaojiutect.wifitransfer.mvp.activity.ReceiveFileActivity;
import com.xiaojiutect.wifitransfer.mvp.activity.SendFileActivity;

public class FunctionFragment extends BaseFragment implements View.OnClickListener{
    private Button mSendBtn,mRecBtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,container,false);
        mSendBtn = (Button)view.findViewById(R.id.sendFile);
        mRecBtn = (Button)view.findViewById(R.id.recFile);
        mSendBtn.setOnClickListener(this);
        mRecBtn.setOnClickListener(this);
        return view;
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
