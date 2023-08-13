package utils.crypto.pojo;

import java.util.Date;

/**
 * 注册
 *
 * @Author guofan
 * @Create 2023/8/13
 */
public class RegisterPo {
    /**
     * 交易id
     */
    private String transactionId;
    /**
     * 过期时间
     */
    private Date registrationExpiry;
    /**
     * 设备id
     */
    private String deviceId;


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getRegistrationExpiry() {
        return registrationExpiry;
    }

    public void setRegistrationExpiry(Date registrationExpiry) {
        this.registrationExpiry = registrationExpiry;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "RegisterPo{" +
                "transactionId='" + transactionId + '\'' +
                ", registrationExpiry=" + registrationExpiry +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
