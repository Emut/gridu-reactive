package com.example.reactordemo;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReactorDemoApplicationTests {

    @Test
    void contextLoads() {
        Assertions.assertThat(true).isTrue();
    }

}
