/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.config;

/**
 * ConsumerConfig  消费者缺省配置
 *
 * @export
 */
public class ConsumerConfig extends AbstractReferenceConfig {

    private static final long serialVersionUID = 2827274711143680600L;

    /**
     * 是否为缺省协议，用于多协议
     * default(false)
     *     // if it's default
     */
    private Boolean isDefault;

    /**
     * 协议的客户端实现类型，比如：dubbo协议的mina,netty等
     * default(dubbo协议缺省为netty)
     *         // networking framework client uses: netty, mina, etc.
     */
    private String client;

    /**
     * 消费者线程池类型，可选：fixed/cached/eager/limited/mock
     * default(fixed)
     *   fixed 固定大小线程池，启动时建立线程，不关闭，一直持有。(缺省)
     *   cached 缓存线程池，空闲一分钟自动删除，需要时重建。
     *   limited 可伸缩线程池，但池中的线程数只会增长不会收缩。只增长不收缩的目的是为了避免收缩时突然来了大流量引起的性能问题。
     *         // consumer thread pool type: cached, fixed, limit, eager
     */
    private String threadpool;

    /**
     * 消费者线程池核心线程大小
     *     // consumer threadpool core thread size
     */
    private Integer corethreads;

    /**
     * 消费者服务线程池大小
     *        // consumer threadpool thread size
     */
    private Integer threads;

    /**
     * 消费者线程池队列大小，当线程池满时，排队等待执行的队列大小，
     *        // consumer threadpool queue size
     */
    private Integer queues;

    @Override
    public void setTimeout(Integer timeout) {
        super.setTimeout(timeout);
        String rmiTimeout = System.getProperty("sun.rmi.transport.tcp.responseTimeout");
        if (timeout != null && timeout > 0
                && (rmiTimeout == null || rmiTimeout.length() == 0)) {
            System.setProperty("sun.rmi.transport.tcp.responseTimeout", String.valueOf(timeout));
        }
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getThreadpool() {
        return threadpool;
    }

    public void setThreadpool(String threadpool) {
        this.threadpool = threadpool;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public Integer getCorethreads() {
        return corethreads;
    }

    public void setCorethreads(Integer corethreads) {
        this.corethreads = corethreads;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Integer getQueues() {
        return queues;
    }

    public void setQueues(Integer queues) {
        this.queues = queues;
    }
}