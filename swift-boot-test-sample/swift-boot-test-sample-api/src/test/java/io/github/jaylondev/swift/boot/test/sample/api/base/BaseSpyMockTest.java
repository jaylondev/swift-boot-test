package io.github.jaylondev.swift.boot.test.sample.api.base;

import io.github.jaylondev.swift.boot.test.SpyMockTest;
import io.github.jaylondev.swift.boot.test.annotations.IgnoreSpy;
import io.github.jaylondev.swift.boot.test.annotations.ModuleInfo;

@ModuleInfo(testModule = "swift-boot-test-sample-api", relateModules = {"swift-boot-test-sample-dal"})
@IgnoreSpy(classSuffixs = {"Mapper"})
public class BaseSpyMockTest extends SpyMockTest {
}
