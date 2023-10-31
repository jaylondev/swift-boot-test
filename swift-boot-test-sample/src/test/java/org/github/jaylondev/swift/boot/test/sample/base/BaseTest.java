package org.github.jaylondev.swift.boot.test.sample.base;

import org.github.jaylondev.swift.boot.test.SupperSwiftBootTest;
import org.github.jaylondev.swift.boot.test.annotations.IncludeComponent;
import org.github.jaylondev.swift.boot.test.annotations.ModuleInfo;
import org.github.jaylondev.swift.boot.test.sample.utils.SpringContextUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;

/**
 * @author jaylon 2023/10/22 11:23
 */
@TestPropertySource(value = {"classpath:/config/application-native.properties"})
@EnableConfigurationProperties
@IncludeComponent({SpringContextUtils.class})
@ModuleInfo(testModule = "swift-boot-test-sample", relateModules = {"swift-boot-test-sample-core"})
public class BaseTest extends SupperSwiftBootTest {
}