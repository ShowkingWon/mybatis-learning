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
 * Base package for Datasources
 */
package org.apache.ibatis.datasource;


/**
 数据源模块

 工厂模式（Factory Pattern）属于创建型模式，它提供了一种创建对象的最佳方式。定义一个创建对象的接口，
    让其子类自己决定实例化哪一个工厂类，工厂模式使其创建过程延迟到子类进行

    工厂接口（Factory）：工厂接口是工厂方法模式的核心接口，调用者会直接和工厂接口交互用于获取具体的产品实现类；
    具体工厂类（ConcreteFactory）:是工厂接口的实现类，用于实例化产品对象，不同的具体工厂类会根据需求实例化不同的产品实现类；
    产品接口（Product）：产品接口用于定义产品类的功能，具体工厂类产生的所有产品都必须实现这个接口。调用者与产品接口直接交互，这是调用者最关心的接口；
    具体产品类（ConcreteProduct）：实现产品接口的实现类，具体产品类中定义了具体的业务逻辑；


 PooledConnection：使用动态代理封装了真正的数据库连接对象；
 PoolState：用于管理PooledConnection对象状态的组件，通过两个list分别 管理空闲状态的连接资源和活跃状态的连接资源
 PooledDataSource：一个简单，同步的、线程安全的数据库连接池





 */
