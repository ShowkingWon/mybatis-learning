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
package org.apache.ibatis.datasource;

import java.util.Properties;
import javax.sql.DataSource;

/**
 * 数据源工厂。
 *  工厂模式：创建型模式。工厂接口（Factory)、具体工厂类（
 * 常见的数据源组件都实现了sun的javax.sql.Datasource接口。
 *
 * 有三种内建的数据源类型 UNPOOLED POOLED JNDI
 *
 * @author Clinton Begin
 */
public interface DataSourceFactory {


    /**
     * 设置属性,被XMLConfigBuilder所调用
     * @param props
     */
    void setProperties(Properties props);


    /**
     * 生产数据源,直接得到javax.sql.DataSource
     *
     * @return
     */
    DataSource getDataSource();

}
