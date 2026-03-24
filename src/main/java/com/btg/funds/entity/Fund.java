package com.btg.funds.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "funds")
public class Fund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "minimum_amount", nullable = false)
    private Double minimumAmount;

    @Column(nullable = false)
    private String category;

    public Fund() {}

    public Fund(String name, Double minimumAmount, String category) {
        this.name = name;
        this.minimumAmount = minimumAmount;
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getMinimumAmount() { return minimumAmount; }
    public void setMinimumAmount(Double minimumAmount) { this.minimumAmount = minimumAmount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
