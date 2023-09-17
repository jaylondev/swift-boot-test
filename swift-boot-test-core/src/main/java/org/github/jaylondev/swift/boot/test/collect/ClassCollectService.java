package org.github.jaylondev.swift.boot.test.collect;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jaylon 2023/8/7 22:24
 */
public class ClassCollectService {

    /**
     * 收集启动单元测试需要在容器中装载的class
     */
    public static Set<Class<?>> collect(CollectContext collectContext) {
        List<ICollectHandler> handlerList = getSubHandlerSortedList();
        handlerList.forEach(handler -> handler.collect(collectContext));
        return collectContext.getInjectClassList();
    }

    /**
     * 获取类收集器
     */
    private static List<ICollectHandler> getSubHandlerSortedList() {
        ServiceLoader<ICollectHandler> services = ServiceLoader.load(ICollectHandler.class);
        List<ICollectHandler> sortedServices = new ArrayList<>();
        for (ICollectHandler service : services) {
            sortedServices.add(service);
        }
        return sortedServices.stream()
                .sorted(Comparator.comparingInt(ICollectHandler::getOrder))
                .collect(Collectors.toList());
    }
}
