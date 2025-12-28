package com.nova.yeobaek.domain.user.repository.deviceToken;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.user.domain.DeviceToken;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
}
