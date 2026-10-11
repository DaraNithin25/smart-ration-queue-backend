package com.smartration.dto;

public class AuthResponse {

    private String token;
    private String role;
    private String name;
    private String mobile;
    private Long userId;
    private String shopCode;

    public AuthResponse() {
    }

    public AuthResponse(String token, String role, String name, String mobile, Long userId, String shopCode) {
        this.token = token;
        this.role = role;
        this.name = name;
        this.mobile = mobile;
        this.userId = userId;
        this.shopCode = shopCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }
}
