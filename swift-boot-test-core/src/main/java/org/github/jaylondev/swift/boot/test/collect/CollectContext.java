package org.github.jaylondev.swift.boot.test.collect;

import lombok.Data;
import org.github.jaylondev.swift.boot.test.annotations.SwiftBootTest;

import java.util.Set;

/**
 * @author jaylon 2023/8/7 22:22
 */
@Data
public class CollectContext {

    private final Set<Class<?>> injectClassList;

    private Class<?> testClass;

    private Class<?> targetClass;

    private SwiftBootTest swiftBootTest;

}
