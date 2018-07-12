package com.xiaojiutech.wifitransfer;

import java.io.Serializable;

public class TaskBean implements Serializable{

    public int totalCount;

    public TaskBean(int count){
        this.totalCount = count;
    }
}
