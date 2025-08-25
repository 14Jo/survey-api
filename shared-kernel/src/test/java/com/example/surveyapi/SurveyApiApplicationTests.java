package com.example.surveyapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestConfig.class)
class SurveyApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
