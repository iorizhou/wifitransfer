package com.xiaojiutect.wifitransfer;

import java.io.Serializable;

/**
 * date：2018/2/24 on 17:00
 * description: 文件详情
 */

public class FileBean implements Serializable {

    public static final String serialVersionUID = "6321689524634663223356";

    public String fileName;

    public int totalCount;

    public int index;

    public String filePath;

    public long fileLength;

    //MD5码：保证文件的完整性
    public String md5;

    public FileBean(String filePath, long fileLength, String md5,String fileName,int totalCount,int index) {
        this.filePath = filePath;
        this.fileLength = fileLength;
        this.md5 = md5;
        this.fileName = fileName;
        this.totalCount = totalCount;
        this.index = index;
    }
}
