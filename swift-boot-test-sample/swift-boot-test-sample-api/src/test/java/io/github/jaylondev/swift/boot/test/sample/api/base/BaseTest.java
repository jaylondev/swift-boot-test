package io.github.jaylondev.swift.boot.test.sample.api.base;

import io.github.jaylondev.swift.boot.test.SupperSwiftBootTest;
import io.github.jaylondev.swift.boot.test.annotations.IncludeComponent;
import io.github.jaylondev.swift.boot.test.annotations.ModuleInfo;
import io.github.jaylondev.swift.boot.test.sample.api.utils.SpringContextUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;

/**
 * @author jaylon 2023/10/22 11:23
 */
@TestPropertySource(value = {"classpath:/config/application-native.properties"})
@EnableConfigurationProperties
@IncludeComponent({SpringContextUtils.class})
@ModuleInfo(testModule = "swift-boot-test-sample-api", relateModules = {"swift-boot-test-sample-dal"})
public class BaseTest extends SupperSwiftBootTest {
}
