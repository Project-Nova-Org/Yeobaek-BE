package com.nova.yeobaek.global.auth.scheduler;

import com.nova.yeobaek.domain.user.service.UserCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCleanupScheduler {

    private final UserCleanupService cleanupService;

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanup() {
        cleanupService.hardDeleteExpiredUsers();
    }
}
