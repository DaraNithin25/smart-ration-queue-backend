package com.smartration.dto;

public class DealerResponse {

    private Long id;
    private String shopCode;
    private String shopName;
    private String ownerName;
    private String mobile;
    private String address;
    private String district;
    private Integer dailyCapacity;
    private Long userId;
    private String userName;

    public DealerResponse() {
    }

    public DealerResponse(Long id, String shopCode, String shopName, String ownerName, String mobile, String address, String district, Integer dailyCapacity, Long userId, String userName) {
        this.id = id;
        this.shopCode = shopCode;
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.mobile = mobile;
        this.address = address;
        this.district = district;
        this.dailyCapacity = dailyCapacity;
        this.userId = userId;
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Integer getDailyCapacity() {
        return dailyCapacity;
    }

    public void setDailyCapacity(Integer dailyCapacity) {
        this.dailyCapacity = dailyCapacity;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
