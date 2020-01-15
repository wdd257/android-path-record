package amap.client.record;

import java.io.Serializable;
import java.util.Date;

public class TraceRecordDTO implements Serializable {
    private Integer id;

    private String amLocationStr;

    private Date createTime;

    private String userInfo;

    private String imei;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAmLocationStr() {
        return amLocationStr;
    }

    public void setAmLocationStr(String amLocationStr) {
        this.amLocationStr = amLocationStr == null ? null : amLocationStr.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo == null ? null : userInfo.trim();
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei == null ? null : imei.trim();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TraceRecordDTO other = (TraceRecordDTO) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getAmLocationStr() == null ? other.getAmLocationStr() == null : this.getAmLocationStr().equals(other.getAmLocationStr()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUserInfo() == null ? other.getUserInfo() == null : this.getUserInfo().equals(other.getUserInfo()))
                && (this.getImei() == null ? other.getImei() == null : this.getImei().equals(other.getImei()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAmLocationStr() == null) ? 0 : getAmLocationStr().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUserInfo() == null) ? 0 : getUserInfo().hashCode());
        result = prime * result + ((getImei() == null) ? 0 : getImei().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", amLocationStr=").append(amLocationStr);
        sb.append(", createTime=").append(createTime);
        sb.append(", userInfo=").append(userInfo);
        sb.append(", imei=").append(imei);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}