/*
 *    Copyright 2009-2012 the original author or authors.
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

/**
 * TODO fillme.
 */
package org.apache.ibatis.logging;


/**
    日志模块需求
    MyBatis没有提供日志的实现类，需要接入第三方的日志组件，但第三方日志组件都有各自的Log级别，且各不相同，
    MyBatis统一提供了trace、debug、warn、error四个级别；

    自动扫描日志实现，并且第三方日志插件加载优先级如下：
    slf4J → commonsLoging → Log4J2 → Log4J → JdkLog;

    日志的使用要优雅的嵌入到主体功能中；


    适配器模式
    适配器模式（Adapter Pattern）是作为两个不兼容的接口之间的桥梁，将一个类的接口转换成客户希望的另外一个接口。
    适配器模式使得原本由于接口不兼容而不能一起工作的那些类可以一起工作；
        Target： 目标角色,期待得到的接口.
        Adaptee：适配者角色,被适配的接口.         实际的Logger。如SLF4J、log4j等
        Adapter：适配器角色,将源接口转换成目标接口. Mybatis的Log
    适用场景：当调用双方都不太容易修改的时候，为了复用现有组件可以使用适配器模式；在系统中接入第三方组件的时候经常被使用到；
    注意：如果系统中存在过多的适配器，会增加系统的复杂性，设计人员应考虑对系统进行重构；

    代理模式
    定义：给目标对象提供一个代理对象，并由代理对象控制对目标对象的引用；
    目的：
        （1）通过引入代理对象的方式来间接访问目标对象，防止直接访问目标对象给系统带来的不必要复杂性；
        （2）通过代理对象对原有的业务增强；
    组成
        Subject
        Proxy
        RealSubject


    ConnectionLogger：负责打印连接信息和SQL语句，并创建PreparedStatementLogger；
    PreparedStatementLogger：负责打印参数信息，并创建ResultSetLogger
    ResultSetLogger：负责打印数据结果信息；



 */
