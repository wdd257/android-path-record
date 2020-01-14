package amap.com.Client;

import java.io.Serializable;

public class QueryParam implements Serializable {
    private Integer bathSize;
    private Integer id;
    private Integer hours;
    private String userInfo;
    private String imei;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getBathSize() {
        return bathSize;
    }

    public void setBathSize(Integer bathSize) {
        this.bathSize = bathSize;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
