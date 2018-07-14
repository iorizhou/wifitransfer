package com.xiaojiutech.wifitransfer.mvp.activity.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.xiaojiutech.wifitransfer.R;
import com.xiaojiutech.wifitransfer.db.model.HistoryFile;
import com.xiaojiutech.wifitransfer.db.model.HistoryFileDao;
import com.xiaojiutech.wifitransfer.db.util.DBUtil;
import com.xiaojiutech.wifitransfer.utils.FileListViewAdapter;
import com.xiaojiutech.wifitransfer.utils.FileOpenIntentUtil;
import com.xiaojiutech.wifitransfer.utils.PullListView;

import java.util.List;

public class FileHistoryRecvFragment extends BaseFragment{

    private List<HistoryFile> mRecvList;
    private HistoryFileDao mFileDao;
    private PullListView mListView;
    private FileListViewAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_filehistory,container,false);
        mListView = (PullListView)view.findViewById(R.id.pullListView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFileDao = DBUtil.getInstance(getActivity()).getDaoSession().getHistoryFileDao();
        mRecvList = getFileList(1);
        mAdapter = new FileListViewAdapter(getActivity(),mRecvList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HistoryFile file = mRecvList.get(i);
                if (file!=null){
                    FileOpenIntentUtil.openFile(file.getPath());
                }
            }
        });
        mAdapter.notifyDataSetChanged();

    }

    private List<HistoryFile> getFileList(int type){
        return mFileDao.queryBuilder().where(HistoryFileDao.Properties.Type.eq(type)).orderDesc(HistoryFileDao.Properties.Time).list();
    }
}
