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
 * Base package for caching stuff
 *
 *
 *  MyBatis缓存模块
 *
 *  Mybatis缓存的实现是基于Map的，从缓存里面读写数据是缓存模块的核心基础功能；
 *  除核心功能之外，有很多额外的附加功能，如：防止缓存击穿，添加缓存清空策略（fifo、lru）、序列化功能、日志能力、定时清空能力等；
 *  附加功能可以以任意的组合附加到核心基础功能之上；

 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.apache.ibatis.cache;
