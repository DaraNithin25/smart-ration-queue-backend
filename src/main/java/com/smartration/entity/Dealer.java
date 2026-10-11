package com.smartration.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dealers")
public class Dealer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_code", nullable = false, unique = true)
    private String shopCode;

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "address")
    private String address;

    @Column(name = "district")
    private String district;

    @Column(name = "daily_capacity")
    private Integer dailyCapacity;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public Dealer() {
    }

    public Dealer(Long id, String shopCode, String shopName, String ownerName, String mobile, String address, String district, Integer dailyCapacity, User user) {
        this.id = id;
        this.shopCode = shopCode;
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.mobile = mobile;
        this.address = address;
        this.district = district;
        this.dailyCapacity = dailyCapacity;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
