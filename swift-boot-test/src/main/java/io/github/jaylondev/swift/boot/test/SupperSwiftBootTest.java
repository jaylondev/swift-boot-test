package io.github.jaylondev.swift.boot.test;

import io.github.jaylondev.swift.boot.test.annotations.listenner.DbTestExecutionListener;
import io.github.jaylondev.swift.boot.test.core.SwiftBootTestBootstrapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@BootstrapWith(SwiftBootTestBootstrapper.class)
@ExtendWith(SpringExtension.class)
@TestExecutionListeners(
        value = { DbTestExecutionListener.class, DependencyInjectionTestExecutionListener.class },
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public abstract class SupperSwiftBootTest {
}
