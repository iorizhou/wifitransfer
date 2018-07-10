package com.xiaojiutect.wifitransfer;

import java.io.Serializable;

public class TransferInfoBean implements Serializable{

    public int progress;
    public String fileName;
    public int index;
    public int totalCount;

    public TransferInfoBean(int progress, String fileName, int index, int totalCount) {
        this.progress = progress;
        this.fileName = fileName;
        this.index = index;
        this.totalCount = totalCount;
    }
}
