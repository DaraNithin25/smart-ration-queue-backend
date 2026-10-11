package com.smartration.dto;

import jakarta.validation.constraints.NotBlank;

public class AssignCardRequest {

    @NotBlank(message = "Card number is required")
    private String cardNumber;

    @NotBlank(message = "Beneficiary mobile is required")
    private String mobile;

    @NotBlank(message = "Shop code is required")
    private String shopCode;

    private String cardType = "PHH";
    private Integer familyMembers = 4;

    public AssignCardRequest() {
    }

    public AssignCardRequest(String cardNumber, String mobile, String shopCode, String cardType, Integer familyMembers) {
        this.cardNumber = cardNumber;
        this.mobile = mobile;
        this.shopCode = shopCode;
        this.cardType = cardType;
        this.familyMembers = familyMembers;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
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
}
