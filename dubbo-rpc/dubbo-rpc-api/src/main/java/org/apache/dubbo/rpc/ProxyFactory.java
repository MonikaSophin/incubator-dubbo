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
package org.apache.dubbo.rpc;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

/**
 * ProxyFactory. (API/SPI, Singleton, ThreadSafe)
 * 代理工厂接口。
 */
@SPI("javassist")
public interface ProxyFactory {

    /**
     * create proxy.
     *
     * 创建 Proxy ，在引用服务时调用。
     * invoker 参数，Consumer 对 Provider 调用的 Invoker 。
     * 方法的 invoker 参数，通过 Protocol 将 Service接口 创建出 Invoker 。
     * 通过创建 Service 的 Proxy ，实现我们在业务代理调用 Service 的方法时，透明的内部转换成调用 Invoker 的 #invoke(Invocation) 方法。
     *
     * @param invoker
     * @return proxy
     */
    @Adaptive({Constants.PROXY_KEY})
    <T> T getProxy(Invoker<T> invoker) throws RpcException;

    /**
     * create proxy.
     *
     * @param invoker
     * @return proxy
     */
    @Adaptive({Constants.PROXY_KEY})
    <T> T getProxy(Invoker<T> invoker, boolean generic) throws RpcException;

    /**
     * create invoker.
     *
     * 创建 Invoker ，在暴露服务时调用。
     * 该方法创建的 Invoker ，下一步会提交给 Protocol ，从 Invoker 转换到 Exporter 。
     *
     * @param <T>
     * @param proxy  Service 对象。
     * @param type   Service 接口类型。
     * @param url    Service 对应的 Dubbo URL 。
     * @return invoker
     */
    @Adaptive({Constants.PROXY_KEY})
    <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException;

}