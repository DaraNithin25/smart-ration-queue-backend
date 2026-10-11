package com.smartration.dto;

import jakarta.validation.constraints.NotBlank;

public class LinkShopRequest {

    @NotBlank(message = "Shop code is required")
    private String shopCode;

    @NotBlank(message = "Dealer mobile number is required")
    private String mobile;

    public LinkShopRequest() {
    }

    public LinkShopRequest(String shopCode, String mobile) {
        this.shopCode = shopCode;
        this.mobile = mobile;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
