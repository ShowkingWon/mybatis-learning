/*
 *    Copyright 2009-2013 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.logging;

import java.lang.reflect.Constructor;

/**
 * 日志工厂。
 *
 * 注意是final修改的类。final类不能被继承，不能被覆盖，以及final类在执行速度方面比一般类快。
 *
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public final class LogFactory {

    /**
     * Marker to be used by logging implementations that support markers
     *  给支持marker功能的logger使用(目前有slf4j, log4j2)
     */
    public static final String MARKER = "MYBATIS";

    /**
     * 具体究竟用哪个日志框架，那个框架所对应logger的构造函数.即被选定的第三方日志组件适配器的构造方法
     * 利用反射机制 newInstance()获取日志实现的实例。
     */
    private static Constructor<? extends Log> logConstructor;

    /**
     *  设置logConstructor,一旦设上，表明找到相应的log的jar包了，那后面别的log就不找了。
     *  自动扫描日志实现，并且第三方日志插件加载优先级如下
     *  MyBatis日志使用顺序如下：
     *      SLF4J --> Apache JakartaCommonsLog --> Log4J2 --> Log4J --> JdkLog --> NoLogging
     *
     */
    static {
        //这边乍一看以为开了几个并行的线程去决定使用哪个具体框架的logging，其实不然。并没有调用线程的start()方法，只是运行run()方法。

        //slf4j  -->优先SLF4J实现
        tryImplementation(new Runnable() {
            @Override
            public void run() {
                useSlf4jLogging();
            }
        });

        //common logging
        tryImplementation(new Runnable() {
            @Override
            public void run() {
                useCommonsLogging();
            }
        });
        //log4j2
        tryImplementation(new Runnable() {
            @Override
            public void run() {
                useLog4J2Logging();
            }
        });
        //log4j
        tryImplementation(new Runnable() {
            @Override
            public void run() {
                useLog4JLogging();
            }
        });
        //jdk logging
        tryImplementation(new Runnable() {
            @Override
            public void run() {
                useJdkLogging();
            }
        });
        //没有日志
        tryImplementation(new Runnable() {
            @Override
            public void run() {
                useNoLogging();
            }
        });
    }

    /**
     * 私有构造器，不允许通过new的方式获取对象实例。
     * 这种情况常用的使用场景：1、单例模式；　　2、防止实例化。
     *
     * 单例模式类的特点：
     * 1.  一个private static的自身类型的属性，保证实例的唯一性；
     * 2.  私有构造器，防止随意实例化；
     * 3.一个public static的getInstance()得到唯一实例的方法；
     *
     * 某种情况下，我们只需要把某个类（工具类）当成“函数”使用，即只需要用到里面的static方法完成某些功能。
     * 这种情况下不需要获得实例，所以getInstance()方法可有可无。
     *
     * 利用反射机制，修改私有构造器的访问权限，也可以获得实例。
     */
    private LogFactory() {
        // disable construction
    }

    //根据传入的类来构建Log
    public static Log getLog(Class<?> aClass) {
        return getLog(aClass.getName());
    }

    //根据传入的类名来构建Log
    public static Log getLog(String logger) {
        try {
            //构造函数，参数必须是一个，为String型，指明logger的名称
            return logConstructor.newInstance(new Object[]{logger});
        } catch (Throwable t) {
            throw new LogException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
        }
    }

    //提供一个扩展功能，如果以上log都不满意，可以使用自定义的log

    public static synchronized void useCustomLogging(Class<? extends Log> clazz) {
        setImplementation(clazz);
    }

    public static synchronized void useSlf4jLogging() {
        setImplementation(org.apache.ibatis.logging.slf4j.Slf4jImpl.class);
    }

    public static synchronized void useCommonsLogging() {
        setImplementation(org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl.class);
    }

    public static synchronized void useLog4JLogging() {
        setImplementation(org.apache.ibatis.logging.log4j.Log4jImpl.class);
    }

    public static synchronized void useLog4J2Logging() {
        setImplementation(org.apache.ibatis.logging.log4j2.Log4j2Impl.class);
    }

    public static synchronized void useJdkLogging() {
        setImplementation(org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl.class);
    }

    //这个没用到

    public static synchronized void useStdOutLogging() {
        setImplementation(org.apache.ibatis.logging.stdout.StdOutImpl.class);
    }

    public static synchronized void useNoLogging() {
        setImplementation(org.apache.ibatis.logging.nologging.NoLoggingImpl.class);
    }

    private static void tryImplementation(Runnable runnable) {
        //先判断类属性 logConstructor。如已经有日志实现jar，则使用；没有，再运行。
        if (logConstructor == null) {
            try {
                //这里调用的不是start,而是run！根本就没用多线程嘛！
                runnable.run();
            } catch (Throwable t) {
                // ignore
            }
        }
    }

    /**
     * 根据指定适配器实现类加载相应的日志组件.
     * 设置实现工厂 -- logConstructor。
     *
     * @param implClass
     */
    private static void setImplementation(Class<? extends Log> implClass) {
        try {
            //获取指定适配器的构造方法
            Constructor<? extends Log> candidate = implClass.getConstructor(new Class[]{String.class});
            //实例化适配器
            Log log = candidate.newInstance(new Object[]{LogFactory.class.getName()});
            log.debug("Logging initialized using '" + implClass + "' adapter.");

            //设置logConstructor,一旦设上，表明找到相应的log的jar包了，那后面别的log就不找了。
            logConstructor = candidate;
        } catch (Throwable t) {
            throw new LogException("Error setting Log implementation.  Cause: " + t, t);
        }
    }

}
