package com.xiaojiutech.wifitransfer.mvp.activity.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.xiaojiutech.wifitransfer.R;
import com.xiaojiutech.wifitransfer.db.model.HistoryFile;
import com.xiaojiutech.wifitransfer.db.model.HistoryFileDao;
import com.xiaojiutech.wifitransfer.db.util.DBUtil;
import com.xiaojiutech.wifitransfer.utils.AlertDialogUtil;
import com.xiaojiutech.wifitransfer.utils.FileListViewAdapter;
import com.xiaojiutech.wifitransfer.utils.FileOpenIntentUtil;
import com.xiaojiutech.wifitransfer.utils.PullListView;

import java.util.List;

public class FileHistorySendFragment extends BaseFragment implements View.OnClickListener{

    private List<HistoryFile> mSendList;
    private HistoryFileDao mFileDao;
    private PullListView mListView;
    private FileListViewAdapter mAdapter;
    public AdView mTopBannerAd,mFooterBannerAd;
    private View mHeaderView,mFooterView;
    private TextView mTitle;
    private Button mClearDb;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_filehistory,container,false);
        mTitle = (TextView)view.findViewById(R.id.title);
        mTitle.setText("发送记录");
        mClearDb = (Button)view.findViewById(R.id.btn_clear_db);
        mClearDb.setOnClickListener(this);
        mListView = (PullListView)view.findViewById(R.id.pullListView);
        mHeaderView = inflater.inflate(R.layout.send_fragment_listview_headerview,null,false);
        mFooterView = inflater.inflate(R.layout.send_fragment_listview_headerview,null,false);
        mTopBannerAd = mHeaderView.findViewById(R.id.top_banner_ad);
        mFooterBannerAd = mFooterView.findViewById(R.id.top_banner_ad);
        mListView.addHeaderView(mHeaderView);
        mListView.addFooterView(mFooterView);
        return view;
    }


    public void showBannerAd(){
        loadBannerAd(new AdListener(){
            @Override
            public void onAdLoaded() {
                Log.i("admob","send_top_banner_ad loaded");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.i("admob","send_top_banner_ad onAdFailedToLoad = "+i);
            }
        },mTopBannerAd);
        loadBannerAd(new AdListener(){
            @Override
            public void onAdLoaded() {
                Log.i("admob","send_top_banner_ad loaded");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.i("admob","send_top_banner_ad onAdFailedToLoad = "+i);
            }
        },mFooterBannerAd);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFileDao = DBUtil.getInstance(getActivity()).getDaoSession().getHistoryFileDao();
        mSendList = getFileList(0);
        mAdapter = new FileListViewAdapter(getActivity(),mSendList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HistoryFile file = mSendList.get(i);
                if (file!=null){
                    FileOpenIntentUtil.openFile(file.getPath());
                }
            }
        });
        mAdapter.notifyDataSetChanged();
        showBannerAd();
    }

    @Override
    public void onResume() {
        mFileDao = DBUtil.getInstance(getActivity()).getDaoSession().getHistoryFileDao();
        mSendList = getFileList(0);
        mAdapter.setDatas(mSendList);
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    private List<HistoryFile> getFileList(int type){
        return mFileDao.queryBuilder().where(HistoryFileDao.Properties.Type.eq(type)).orderDesc(HistoryFileDao.Properties.Time).list();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_clear_db:
                new AlertDialogUtil(getActivity()).showAlertDialog(getString(R.string.app_name), "确定清空所有文件发送记录吗?", "确 定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mFileDao.deleteInTx(mSendList);
                        mSendList.clear();
                        mAdapter.setDatas(mSendList);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(),"已清空所有文件发送记录",Toast.LENGTH_SHORT).show();
                    }
                },"取 消",null,true);
                break;
        }
    }
}
