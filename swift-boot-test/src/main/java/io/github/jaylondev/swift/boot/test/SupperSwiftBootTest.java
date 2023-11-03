package io.github.jaylondev.swift.boot.test;

import io.github.jaylondev.swift.boot.test.core.SwiftBootTestBootstrapper;
import org.junit.runner.RunWith;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@BootstrapWith(SwiftBootTestBootstrapper.class)
public abstract class SupperSwiftBootTest {
}
