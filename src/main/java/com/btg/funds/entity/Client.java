package com.btg.funds.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private Double balance;

    @Column(name = "notification_preference", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationPreference;

    public enum NotificationType {
        EMAIL, SMS
    }

    public Client() {}

    public Client(String name, String email, String phone, Double balance, NotificationType notificationPreference) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.balance = balance;
        this.notificationPreference = notificationPreference;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    public NotificationType getNotificationPreference() { return notificationPreference; }
    public void setNotificationPreference(NotificationType notificationPreference) { this.notificationPreference = notificationPreference; }
}
