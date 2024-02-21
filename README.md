<h1 align="center" style="border:none;">SwiftBootTest</h1>

![Stable Version](https://img.shields.io/badge/stable_version-2.0.0-brightgreen.svg) ![image](https://img.shields.io/badge/License-Apache%202.0-lightblue) ![Language](https://img.shields.io/badge/language-Java-orange.svg) ![Spring Boot](https://img.shields.io/badge/framework-Spring_Boot-yellowgreen.svg) ![MyBatis](https://img.shields.io/badge/persistence-MyBatis-blue.svg) ![Build](https://img.shields.io/badge/build-Maven-yellow.svg)

***

# Overview

SwiftBootTest（SBT）是一个用于提升SpringBoot单元测试代码编写和执行效率的实用组件。使用SBT执行单元测试时，SBT会自动扫描并加载本次测试所需的类到Spring上下文容器中，排除其他非必要的依赖和初始化。使我们在编写测试代码时，既能享受IOC容器管理对象的便利，避免编写侵入性代码，同时又能避免完整启动整个工程带来的时间、性能消耗及外部环境依赖的影响。

# Features

*   0、简单：开箱即用，只需少量的注解配置。
*   1、快速：可快速启动Spring上下文容器，执行Test代码，提升测试效率。
*   2、0入侵：无需侵入性代码，直接使用容器上下文进行对象管理。
*   3、低内存：只在容器中加载与当前测试类有引用关系的类
*   4、高效：全局懒加载模式，只实例化测试方法执行过程中实际调用的类。
*   5、独立：实现随时随地执行测试，无需关心项目中各种中间件、三方服务、远程配置的初始化。
*   6、数据库：集成h2内存数据库，支持dao层代码测试。无需维护测试数据库环境，不在共享库中产生测试脏数据。
*   7、mock工具：提供MockUtils配合Mockito可mock任意层级的引用实例

# Background

在Spring Boot项目的单元测试中，特别是对于具有强业务性质的系统，我们通常期望能够直接使用@Autowired注解注入一个实例，然后直接调用需要测试的方法，而无需编写任何侵入性的代码。为了实现这一目的，我们可以使用@SpringBootTest注解来启动IOC容器，并加载项目中所有Spring扫描到的类到容器中。
然而，在这个过程中，我们仅仅是想要测试某个类的某个方法，却不得不启动整个工程，初始化各种组件、数据库、中间件等资源。随着项目的发展，这一过程会变得越来越冗杂且耗时。尽管@SpringBootTest允许我们只加载当前测试需要的类到容器中，但是这仍然需要我们在测试前手动梳理被测试类的引用关系，然后将这些Class的数组标记在测试类上。
有没有一种组件能够自动帮助我们梳理当前测试方法需要使用到哪些类，并将它们加载到容器中呢？基于这个简单的需求，SwiftBootTest开始了第一个版本的迭代。

# Quick Start

## 1. 基础使用

### 1.1 pom引用

```html
<dependency>
    <groupId>io.github.jaylondev</groupId>
    <artifactId>swift-boot-test</artifactId>
    <version>2.0.0</version>
    <scope>test</scope>
</dependency>
```

### 1.2 创建测试基类

可选，一些公共的注解配置推荐标记在测试基类中

```java
@TestPropertySource(value = {"classpath:/config/application-native.properties"})
@EnableConfigurationProperties
public class BaseTest extends SupperSwiftBootTest {
}
```

### 1.3 开始测试

```java
@SwiftBootTest(targetClass = SampleService.class)
public class SampleServiceTest extends BaseTest {

    @Autowired
    private SampleService sampleService;
   
    @Test
    public void testSampleGet() {
        // run test
        SampleResp sampleResp = sampleService.sampleGet("test");
        // assert result
    }

}
```

## 2. 用于测试的临时数据库

SBT内部集成了H2内存数据库，如果要对dao层代码进行测试，只需以下两步（仅支持mybatis）

### 2.1 mapper配置

在测试类或测试基类上标记@DbTestEnviroment注解，标记mapper类路径和mapper.xml文件的路径

```java
@DbTestEnvironment(
    mapperBasePackages = "${mapper类的包路径}",
    mapperXmlLocation = "${mapper.xml的路径}"
)
public class BaseTest extends SupperSwiftBootTest {
    // ...
}
```

> 示例代码

```java
@DbTestEnvironment(
    mapperBasePackages = "io.github.jaylondev.swift.boot.test.sample.api.dal.mapper",
    mapperXmlLocation = "classpath:mapper/*.xml"
)
@TestPropertySource(value = {"classpath:/config/application-native.properties"})
@EnableConfigurationProperties
public class BaseTest extends SupperSwiftBootTest {
    // ...
}
```

### 2.2 测试方法上标记@DbTest

在需要连接数据库的测试方法上标记@DbTest注解，配置数据库表初始化ddl文件路径

> 示例代码

```java
/*
 * 在此示例中，sql文件的路径为：
 * src/test/resources/sql/sample_orders_create_ddl.sql
 */
@Test
@DbTest(sqlFile = "/sql/sample_orders_create_ddl.sql")
public void testSampleGet() {
  // run test
  SampleResp sampleResp = sampleService.sampleGet("test");
  // assert result
  // ...
}
```

> sql文件内容示例

```sql
-- 检查如果表已存在先drop
DROP TABLE sample_orders IF EXISTS;
-- 创建表
CREATE TABLE sample_orders (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  order_no varchar(100) NOT NULL COMMENT '订单号',
  sku_name varchar(100) NOT NULL COMMENT '商品名称',
  sku_count int  NOT NULL COMMENT '购买数量',
  order_status tinyint NOT NULL COMMENT '订单状态',
  create_time timestamp NOT NULL COMMENT '创建时间',
  update_time timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY(id)
);
```

## 3. 推荐的mock方法

在单元测试中，中间件、数据库、外部系统调用都应该使用Mockito替身，只专注于自己开发内容的测试。使用SBT提供的MockUtils结合Mockito框架，可以实现任意层级的mock替换。

> 如下代码片段所示，假设测试方法执行过程中会调用SampleRepositoryA的inserOne方法

```java
@Repository
public class SampleRepositoryA {

    @Autowired
    private SampleOrdersMapper sampleOrdersMapper;

    public int insertOne(SampleOrders orders) {
        if (orders == null) {
            return 0;
        }
        return sampleOrdersMapper.insert(orders);
    }
}
```

> 可参考以下代码，先注入拿到容器中SampleRepositoryA的实例，再通过@Mock注解生成一个SampleOrdersMapper的替身对象，然后使用MockUtils的mock方法将替身对象替换到SampleRepositoryA的引用中。

```java
@SwiftBootTest(targetClass = SampleService.class)
public class SampleServiceTest extends BaseTest {

    @Autowired
    private SampleService sampleService;
    /**
     * 注入需要mock改造的类
     */
    @Autowired
    private SampleRepositoryA sampleRepositoryA;
    /**
     * 利用mockito生成替身对象
     */
    @Mock
    private SampleOrdersMapper ordersMapper;

    @Test
    public void testSampleGet() {
        // 将sampleRepositoryA中的ordersMapper字段替换为mock后的替身对象
        MockUtils.mock(sampleRepositoryA, ordersMapper);
        // 定义mock行为
        Mockito.when(ordersMapper.insert(Mockito.any(SampleOrders.class))).thenReturn(1);
        // run test
        SampleResp sampleResp = sampleService.sampleGet("test");
        // assert result
        //...
    }

}
```

# 配置说明

## @SwiftBootTest

*   必选：是
*   作用：标记在测试类上，主要用于配置测试目标类，SBT会从测试目标类开始寻找需要装配到容器中的类。
*   属性：
    *   targetClass：测试目标类
    *   includeComponent：利用这个属性可以将一些没有在测试目标类的@AutoWired引用链上，但测试方法执行时需要从容器中获取的类，比如一些配置类、静态工具类等。
    *   unLazyClasses：用于标记不需要懒加载的类。SBT会在测试方法执行前，将测试类中所有的引用都设置为懒加载模式，确保于当前测试方法无关的引用类不会被初始化。如果某些类需要提前初始化，可以使用这个注解进行标记。

## @IncludeComponent

*   必选：否
*   作用：利用这个注解可以将一些没有在测试目标类的@AutoWired引用链上，但测试方法执行时需要从容器中获取的类，比如一些配置类、静态工具类等。
*   ps：这个注解和@SwiftBootTest注解中的includeComponent属性的作用相同，区别在于includeComponent属性标记在测试类上，用于配置当前测试类的测试方法中需要单独加载的类，而@Includ

## @ModuleInfo

*   必选：否
*   作用：在多module工程中，推荐用此注解标记测试类所在的module和其他关联的module。
*   示例：
    ```java
    @ModuleInfo(
        testModule = "swift-boot-test-sample-api", 
        relateModules = {"swift-boot-test-sample-dal"}
    )
    ```
*   ps：为什么需要标记测试类所在的module和其关联module？
    > 为了应对如下场景：SBT在搜集当前测试需要注册到容器中的类时，如果被@AutoWired注解引用的对象是一个接口，则会到本地的/target/classes目录下去扫描该接口的所有实现类，并注册到容器中。如果实现类在其他module中，测试类所在module的/target目录下就无法找到此实现类，所以需要通过此注解标记module信息，方便SBT到其他module的target文件中找到接口的所有实现类。
*   ps2：测试类所在module与项目中其他module不在同一层级应该怎么配置？
    `假设工程结构如下，测试类在module：sample-test中`

    > ——sample-api
    > ——sample-dal
    > ——sample-core
    > ——support-dev
    > ————sample-test
    > ——————src
    > ————————test
    > ——————————com.jaylondev.test1.class

    `在这样的项目结构中,@ModuleInfo正确配置如下`

    ```java
    @ModuleInfo(
        testModule = "support-dev/sample-test",
        relateModules = {"sample-api","sample-dal","sample-core"}
    )
    ```

## @DbTestEnvironment

*   必选：否
*   作用：标记在测试基类或测试类上，标记mapper路径信息，如果执行的测试方法上带有@DbTest注解则会连接测试内存数据库
*   属性：
    *   mapperBasePackages：mybatis中mapper接口的全路径名
    *   mapperXmlLocation：mapper的xml文件路径
*   示例：
    ```java
    @DbTestEnvironment(
            mapperBasePackages = "io.github.jaylondev.swift.boot.test.sample.api.dal.mapper",
            mapperXmlLocation = "classpath:mapper/*.xml"
    )
    ```

## @DbTest

*   必选：否
*   作用：标记在测试方法上，当测试方法中需要连接数据库时，使用此注解，可以提供一个内存数据库
*   示例：
    @DbTest(sqlFile = "/sql/sample\_orders\_create\_ddl.sql")
    public void testXXX() {
    // ...
    }

# FAQ

> **Q：为什么测试方法执行时没有成功注入我需要的类，报了NoSuchBeanDefinitionException和空指针异常？**

1.  SBT会从测试目标类开始，递归地扫描并收集所有通过`@Autowired`注解引用的类，以及这些类所引用的其他类。收集完成后将这些类以懒加载模式注册到Spring的IOC容器中。通过其他方式引用的类可能不会被自动注册到容器中。若需要手动将特定类注入容器，可以使用`@IncludeComponent`注解或`@SpringBootTest`注解的`includeComponent`属性，详见配置说明。
    示例代码:

```java
@IncludeComponent({SpringContextUtils.class})
public class BaseTest extends SupperSwiftBootTest {
}
```

1.  多module工程推荐在测试类或测试基类中通过@ModuleInfo注解配置项目的module名，详见配置说明。
    示例代码:

```java
/*
 * testModule:测试类所在的module
 * relateModules：其他关联module
 */
@ModuleInfo(testModule = "swift-boot-test-sample-api", relateModules = {"swift-boot-test-sample-dal"})
public class BaseTest extends SupperSwiftBootTest {
}
```

> **Q：被测试类中需要读取配置文件数据如何支持？**

在测试类或测试基类中添加@TestPropertySource和@EnableConfigurationProperties注解
示例代码：

    @TestPropertySource(value = {"classpath:/config/application-native.properties"})
    @EnableConfigurationProperties
    public class BaseTest extends SupperSwiftBootTest {
        // ...
    }

# 示例代码
[swift-boot-test-sample](https://github.com/jaylondev/swift-boot-test/tree/main/swift-boot-test-sample)
