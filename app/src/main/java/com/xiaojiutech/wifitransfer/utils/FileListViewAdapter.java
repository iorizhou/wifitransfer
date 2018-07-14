package com.xiaojiutech.wifitransfer.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaojiutech.wifitransfer.R;
import com.xiaojiutech.wifitransfer.db.model.HistoryFile;

import java.util.List;

public class FileListViewAdapter extends BaseAdapter {
    private List<HistoryFile> mDatas;
    private Context mContext;
    private LayoutInflater mInflater;
    public FileListViewAdapter(Context ctx,List<HistoryFile> datas){
        this.mContext = ctx;
        mInflater = LayoutInflater.from(this.mContext);
        this.mDatas = datas;
    }

    public void setDatas(List<HistoryFile> data){
        this.mDatas = data;
    }

    @Override
    public int getCount() {
        return this.mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null){
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_file,null);
            viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
            viewHolder.name = (TextView)view.findViewById(R.id.name);
            viewHolder.path = (TextView)view.findViewById(R.id.path);
            viewHolder.time = (TextView)view.findViewById(R.id.time);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }
        HistoryFile file = (HistoryFile) getItem(i);
        viewHolder.time.setText(DateUtil.long2DateString(file.getTime()));
        viewHolder.path.setText(file.getPath().substring(0,file.getPath().lastIndexOf("/")));
        viewHolder.name.setText(file.getName());
        viewHolder.icon.setImageResource(R.drawable.lfile_back_bg);
        return view;
    }

    public class ViewHolder{
        public ImageView icon;
        public TextView name;
        public TextView path;
        public TextView time;
    }
}
