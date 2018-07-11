package com.xiaojiutect.wifitransfer.mvp.activity.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.xiaojiutect.wifitransfer.R;
import com.xiaojiutect.wifitransfer.db.model.HistoryFile;
import com.xiaojiutect.wifitransfer.db.model.HistoryFileDao;
import com.xiaojiutect.wifitransfer.db.util.DBUtil;
import com.xiaojiutect.wifitransfer.utils.FileListViewAdapter;
import com.xiaojiutect.wifitransfer.utils.FileOpenIntentUtil;
import com.xiaojiutect.wifitransfer.utils.PullListView;

import java.util.List;

public class FileHistoryRecvFragment extends BaseFragment{

    private List<HistoryFile> mRecvList;
    private HistoryFileDao mFileDao;
    private PullListView mListView;
    private FileListViewAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filehistory,container,false);
        mListView = (PullListView)view.findViewById(R.id.pullListView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
        super.onViewCreated(view, savedInstanceState);
    }

    private List<HistoryFile> getFileList(int type){
        return mFileDao.queryBuilder().where(HistoryFileDao.Properties.Type.eq(type)).orderDesc(HistoryFileDao.Properties.Time).list();
    }
}
