package com.example.surveyapi.global.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Component("myCustomHealth")
public class HealthController implements HealthIndicator {

	@Override
	public Health health() {
		boolean isHealthy = checkSomething();

		if (isHealthy) {
			return Health.up().withDetail("service", "정상적으로 이용 가능합니다.").build();
		}

		return Health.down().withDetail("service", "현재 서비스에 접근할 수 없습니다.").build();
	}

	private boolean checkSomething() {
		// 실제 체크 로직
		return true;
	}
}
