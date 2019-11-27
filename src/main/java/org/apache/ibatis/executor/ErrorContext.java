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
package org.apache.ibatis.executor;

/**
 * 错误上下文.
 * MyBatis 异常涵盖的信息总结为一点就是：异常是由谁在做什么的时候在哪个资源文件中发生的，执行的 SQL 是哪个，以及 java 详细的异常信息。
 *
 * @author Clinton Begin
 */
public class ErrorContext {

  /**
   *  获得 \n 不同的操作系统不一样
   */
  private static final String LINE_SEPARATOR = System.getProperty("line.separator","\n");

  /**
   * 每个线程给开一个错误上下文，防止多线程问题
   *
   * ThreadLocal 是本地线程存储，它的作用是为变量在每个线程中创建一个副本，每个线程内部都可以使用该副本，线程之间互不影响.
   * 使用 ThreadLocal 来管理 ErrorContext：
   * 保证了在多线程环境中，每个线程内部可以共用一份 ErrorContext，但多个线程持有的 ErrorContext 互不影响，保证了异常日志的正确输出
   *
   * 关于ThreadLocal<T>的一个警告：“一定要确保在执行完毕后清空ThreadLocal，避免产生意料之外的问题。
   */
  private static final ThreadLocal<ErrorContext> LOCAL = new ThreadLocal<ErrorContext>();

  private ErrorContext stored;

  /**
   * 存储异常存在于哪个资源文件中.
   *    如：### The error may exist in mapper/AuthorMapper.xml
   */
  private String resource;

  /**
   * 存储异常是做什么操作时发生的.
   *    如：### The error occurred while setting parameters
   */
  private String activity;

  /**
   * 存储哪个对象操作时发生异常.
   *    如：### The error may involve defaultParameterMap
   *
   */
  private String object;

  /**
   * 存储异常的概览信息.
   *    如：### Error querying database. Cause: java.sql.SQLSyntaxErrorException: Unknown column 'id2' in 'field list'
   */
  private String message;

  /**
   * 存储发生日常的 SQL 语句.
   *    如：### SQL: select id2, name, sex, phone from author where name = ?
   */
  private String sql;

  /**
   * 存储详细的 Java 异常日志.
   */
  private Throwable cause;
 

  /**
   * 单例模式，私有构造器
   */
  private ErrorContext() {
  }

  /**
   * 工厂方法，得到一个实例
   * @return
   */
  public static ErrorContext instance() {
      //因为是多线程，所以用了ThreadLocal  线程安全
    ErrorContext context = LOCAL.get();
      //懒汉 单例模式
    if (context == null) {
      context = new ErrorContext();
      LOCAL.set(context);
    }
    return context;
  }

  /**
   * stored 变量充当一个中介，在调用 store() 方法时将当前 ErrorContext 保存下来.
   *
   *
   * @return ErrorContext
   */
  public ErrorContext store() {
    stored = this;
    LOCAL.set(new ErrorContext());
    return LOCAL.get();
  }

  /**
   * 和store相对应的方法，store是存储起来，recall是召回，两个方法成组使用！
   *
   * 调用 recall() 方法时将该 ErrorContext （stored） 实例传递给 LOCAL, 再将stored清空。
   * @return ErrorContext
   */
  public ErrorContext recall() {
    if (stored != null) {
      LOCAL.set(stored);
      stored = null;
    }
    return LOCAL.get();
  }

  /**
   * 建造者模式。
   * @param resource
   * @return ErrorContext
   */
  public ErrorContext resource(String resource) {
    this.resource = resource;
    return this;
  }

  public ErrorContext activity(String activity) {
    this.activity = activity;
    return this;
  }

  public ErrorContext object(String object) {
    this.object = object;
    return this;
  }

  public ErrorContext message(String message) {
    this.message = message;
    return this;
  }

  public ErrorContext sql(String sql) {
    this.sql = sql;
    return this;
  }

  public ErrorContext cause(Throwable cause) {
    this.cause = cause;
    return this;
  }

  /**
   * 全部清空重置
   *
   * Mybatis采用 try-catch-finally 的机制， 在可能执行出错时候获取到ErrorContext实例里存储的信息来协助使用者快速排错，而最终又保证了清理工作能如期执行。
   *
   * @return  ErrorContext
   */
  public ErrorContext reset() {
    resource = null;
    activity = null;
    object = null;
    message = null;
    sql = null;
    cause = null;
    // 清空LOCAL
    LOCAL.remove();
    return this;
  }

  /**
   * 打印信息供人阅读
   * ErrorContext.toString()方法只在 org.apache.ibatis.exceptions.ExceptionFactory类中得到调用, 也正是因为使用了ThreadLocal<T>,
   * 我们就能直接取到之前执行本SQL的线程上的信息, 也就很方便的构建出异常发生时的上下文，快速排错.
   * 关于ThreadLocal<T>的一个警告：“一定要确保在执行完毕后清空ThreadLocal，避免产生意料之外的问题。
   *
   * @return String
   */
  @Override
  public String toString() {
    StringBuilder description = new StringBuilder();

    // message 自定义信息
    if (this.message != null) {
      description.append(LINE_SEPARATOR);
      description.append("### ");
      description.append(this.message);
    }

    // resource
    if (resource != null) {
      description.append(LINE_SEPARATOR);
      description.append("### The error may exist in ");
      description.append(resource);
    }

    // object
    if (object != null) {
      description.append(LINE_SEPARATOR);
      description.append("### The error may involve ");
      description.append(object);
    }

    // activity
    if (activity != null) {
      description.append(LINE_SEPARATOR);
      description.append("### The error occurred while ");
      description.append(activity);
    }

    // sql
    if (sql != null) {
      description.append(LINE_SEPARATOR);
      description.append("### SQL: ");
      //把sql压缩到一行里
      description.append(sql.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').trim());
    }

    // cause
    if (cause != null) {
      description.append(LINE_SEPARATOR);
      description.append("### Cause: ");
      description.append(cause.toString());
    }

    return description.toString();
  }

}
