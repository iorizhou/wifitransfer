package com.xiaojiutect.wifitransfer.db.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class HistoryFile {

    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String path;
    private Long time;
    private Integer type;   //0 发送的 1 接收的

    @Generated(hash = 1551805481)
    public HistoryFile(Long id, String name, String path, Long time, Integer type) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.time = time;
        this.type = type;
    }

    public HistoryFile(String name, String path, Long time, Integer type) {
        this.name = name;
        this.path = path;
        this.time = time;
        this.type = type;
    }

    @Generated(hash = 667690746)
    public HistoryFile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
