package com.smartration.dto;

public class RationCardResponse {

    private Long id;
    private String cardNumber;
    private String cardType;
    private Integer familyMembers;
    private Long userId;
    private String userName;
    private String mobile;
    private String shopCode;
    private String shopName;
    private String shopAddress;
    private String district;

    public RationCardResponse() {
    }

    public RationCardResponse(Long id, String cardNumber, String cardType, Integer familyMembers, Long userId, String userName, String mobile, String shopCode, String shopName, String shopAddress, String district) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.familyMembers = familyMembers;
        this.userId = userId;
        this.userName = userName;
        this.mobile = mobile;
        this.shopCode = shopCode;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.district = district;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Integer getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(Integer familyMembers) {
        this.familyMembers = familyMembers;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
