package com.drew.truelayerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "credentials_id")
    private String credentialsId;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "refreshed_at")
    private OffsetDateTime refreshedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getCredentialsId() { return credentialsId; }
    public void setCredentialsId(String credentialsId) { this.credentialsId = credentialsId; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getRefreshedAt() { return refreshedAt; }
    public void setRefreshedAt(OffsetDateTime refreshedAt) { this.refreshedAt = refreshedAt; }
}
