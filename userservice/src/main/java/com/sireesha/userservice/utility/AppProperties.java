package com.sireesha.userservice.utility;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {
    @Value("${app.password-reset.expiry-minutes}")
    private Long passwordResetExpiryMinutes;
    @Value("${app.login.max-failed-attempts}")
    private Integer maxFailedLoginAttempts;
    @Value("${app.login.lock-duration-minutes}")
    private Integer lockDurationInMinutes;
}
