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

import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.serialize.Serialization;
import org.apache.dubbo.common.status.StatusChecker;
import org.apache.dubbo.common.threadpool.ThreadPool;
import org.apache.dubbo.config.support.Parameter;
import org.apache.dubbo.remoting.Codec;
import org.apache.dubbo.remoting.Dispatcher;
import org.apache.dubbo.remoting.Transporter;
import org.apache.dubbo.remoting.exchange.Exchanger;
import org.apache.dubbo.remoting.telnet.TelnetHandler;
import org.apache.dubbo.rpc.Protocol;

import java.util.Map;

/**
 * ProtocolConfig  服务提供者协议配置
 *
 * @export
 */
public class ProtocolConfig extends AbstractConfig {

    private static final long serialVersionUID = 6913423882496634749L;

    /**<protocol>
     * 协议名称
     * default(dubbo)
     *     // protocol name
     */
    private String name;

    /**<host>
     * -服务主机名，多网卡选择或指定VIP及域名时使用，为空则自动查找本机IP，
     * -建议不要配置，让Dubbo自动获取本机IP
     * default(自动查找本机IP)
     *     // service IP address (when there are multiple network cards available)
     */
    private String host;

    /**<port>
     * 服务端口
     *          dubbo协议缺省端口为20880，
     *          rmi协议缺省端口为1099，
     *          http和hessian协议缺省端口为80；
     *          如果配置为-1 或者 没有配置port，则会分配一个没有被占用的端口。
     *          Dubbo 2.4.0+，分配的端口在协议缺省端口的基础上增长，确保端口段可控。
     *     // service port
     */
    private Integer port;

    /**contextpath
     *  default("")
     *      // context path
     */
    private String contextpath;

    /**threadpool
     * 线程池类型，可选：fixed/cached/eager/limited/mock
     * default(fixed)
     *   fixed 固定大小线程池，启动时建立线程，不关闭，一直持有。(缺省)
     *   cached 缓存线程池，空闲一分钟自动删除，需要时重建。
     *   limited 可伸缩线程池，但池中的线程数只会增长不会收缩。只增长不收缩的目的是为了避免收缩时突然来了大流量引起的性能问题。
     *     // thread pool
     */
    private String threadpool;

    /**
     * 核心线程池数量
     *     // thread pool core thread size
     */
    private Integer corethreads;

    /**threads
     * 服务线程池大小(固定大小)
     * fault(100)
     *  // thread pool size (fixed size)
     */
    private Integer threads;

    /**iothreads
     * io线程池大小(固定大小)	 default(cpu个数+1)
     *     // IO thread pool size (fixed size)
     */
    private Integer iothreads;

    /**queues
     * 线程池队列大小，当线程池满时，排队等待执行的队列大小，建议不要设置，
     * 当线程程池时应立即失败，重试其它服务提供机器，而不是排队，除非有特殊需求。
     * default（0）
     *     // thread pool's queue length
     */
    private Integer queues;

    /**accepts
     * 服务提供方最大可接受连接数
     * default(0)
     *     // max acceptable connections
     */
    private Integer accepts;

    /**codec
     * 协议编码方式 default(dubbo	)
     *     // protocol codec
     */
    private String codec;

    /**serialization
     * 协议序列化方式，当协议支持多种序列化方式时使用，
     * 比如：dubbo协议的dubbo,hessian2,java,compactedjava，以及http协议的json等
     * default:
     * dubbo协议缺省为hessian2，rmi协议缺省为java，http协议缺省为json
     *     // serialization
     */
    private String serialization;

    /**charset
     * 序列化编码
     * default(UTF-8)
     *     // charset
     */
    private String charset;

    /**payload
     * 请求及响应数据包大小限制，单位：字节
     * default(88388608(=8M))
     *     // payload max length
     */
    private Integer payload;

    /**buffer
     * 网络读写缓冲区大小 单位：字节
     * default(8192)
     *     // buffer size
     */
    private Integer buffer;

    /**heartbeat
     * 心跳间隔，对于长连接，当物理层断开时，
     * 比如拔网线，TCP的FIN消息来不及发送，对方收不到断开事件，此时需要心跳来帮助检查连接是否已断开
     * default(0)
     *     // heartbeat interval
     */
    private Integer heartbeat;

    /**accesslog
     * 设为true，将向logger中输出访问日志，也可填写访问日志文件路径，直接把访问日志输出到指定文件
     *     // access log
     */
    private String accesslog;

    /**transporter
     * 协议的服务端和客户端实现类型，比如：dubbo协议的mina,netty等，可以分拆为server和client配置
     * default(dubbo协议缺省为netty)
     *     // transfort
     */
    private String transporter;

    // how information is exchanged
    private String exchanger;

    /**dispatcher
     * 协议的消息派发方式，用于指定线程模型，
     * 比如：dubbo协议的all, direct, message, execution, connection等
     *   all 所有消息都派发到线程池，包括请求，响应，连接事件，断开事件，心跳等。
     *   direct 所有消息都不派发到线程池，全部在 IO 线程上直接执行。
     *   message 只有请求响应消息派发到线程池，其它连接断开事件，心跳等消息，直接在 IO 线程上执行。
     *   execution 只请求消息派发到线程池，不含响应，响应和其它连接断开事件，心跳等消息，直接在 IO 线程上执行。
     *   connection 在 IO 线程上，将连接断开事件放入队列，有序逐个执行，其它消息派发到线程池。
     *  default(dubbo协议缺省为all)
     *     // thread dispatch mode
     */
    private String dispatcher;

    // networker
    private String networker;

    /**server
     * 协议的服务器端实现类型，比如：dubbo协议的mina,netty等，http协议的jetty,servlet等
     * default(dubbo协议缺省为netty，http协议缺省为servlet)
     *     // sever impl
     */
    private String server;

    /**client
     * 协议的客户端实现类型，比如：dubbo协议的mina,netty等
     * default(dubbo协议缺省为netty)
     *     // client impl
     */
    private String client;

    /**telnet
     * 所支持的telnet命令，多个命令用逗号分隔
     *     // supported telnet commands, separated with comma.
     */
    private String telnet;

    // command line prompt
    private String prompt;

    // status check
    private String status;

    /**register
     * 该协议的服务是否注册到注册中心
     * default(true)
     *     // whether to register
     */
    private Boolean register;

    // parameters
    // 是否长连接
    // TODO add this to provider config
    private Boolean keepAlive;

    // TODO add this to provider config
    private String optimizer;

    private String extension;

    // parameters
    private Map<String, String> parameters;

    // if it's default
    private Boolean isDefault;

    public ProtocolConfig() {
    }

    public ProtocolConfig(String name) {
        setName(name);
    }

    public ProtocolConfig(String name, int port) {
        setName(name);
        setPort(port);
    }

    @Parameter(excluded = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        checkName("name", name);
        this.name = name;
        if (id == null || id.length() == 0) {
            id = name;
        }
    }

    @Parameter(excluded = true)
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        checkName("host", host);
        this.host = host;
    }

    @Parameter(excluded = true)
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Deprecated
    @Parameter(excluded = true)
    public String getPath() {
        return getContextpath();
    }

    @Deprecated
    public void setPath(String path) {
        setContextpath(path);
    }

    @Parameter(excluded = true)
    public String getContextpath() {
        return contextpath;
    }

    public void setContextpath(String contextpath) {
        checkPathName("contextpath", contextpath);
        this.contextpath = contextpath;
    }

    public String getThreadpool() {
        return threadpool;
    }

    public void setThreadpool(String threadpool) {
        checkExtension(ThreadPool.class, "threadpool", threadpool);
        this.threadpool = threadpool;
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

    public Integer getIothreads() {
        return iothreads;
    }

    public void setIothreads(Integer iothreads) {
        this.iothreads = iothreads;
    }

    public Integer getQueues() {
        return queues;
    }

    public void setQueues(Integer queues) {
        this.queues = queues;
    }

    public Integer getAccepts() {
        return accepts;
    }

    public void setAccepts(Integer accepts) {
        this.accepts = accepts;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        if ("dubbo".equals(name)) {
            checkMultiExtension(Codec.class, "codec", codec);
        }
        this.codec = codec;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        if ("dubbo".equals(name)) {
            checkMultiExtension(Serialization.class, "serialization", serialization);
        }
        this.serialization = serialization;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Integer getPayload() {
        return payload;
    }

    public void setPayload(Integer payload) {
        this.payload = payload;
    }

    public Integer getBuffer() {
        return buffer;
    }

    public void setBuffer(Integer buffer) {
        this.buffer = buffer;
    }

    public Integer getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(Integer heartbeat) {
        this.heartbeat = heartbeat;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        if ("dubbo".equals(name)) {
            checkMultiExtension(Transporter.class, "server", server);
        }
        this.server = server;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        if ("dubbo".equals(name)) {
            checkMultiExtension(Transporter.class, "client", client);
        }
        this.client = client;
    }

    public String getAccesslog() {
        return accesslog;
    }

    public void setAccesslog(String accesslog) {
        this.accesslog = accesslog;
    }

    public String getTelnet() {
        return telnet;
    }

    public void setTelnet(String telnet) {
        checkMultiExtension(TelnetHandler.class, "telnet", telnet);
        this.telnet = telnet;
    }

    @Parameter(escaped = true)
    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        checkMultiExtension(StatusChecker.class, "status", status);
        this.status = status;
    }

    public Boolean isRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }

    public String getTransporter() {
        return transporter;
    }

    public void setTransporter(String transporter) {
        checkExtension(Transporter.class, "transporter", transporter);
        this.transporter = transporter;
    }

    public String getExchanger() {
        return exchanger;
    }

    public void setExchanger(String exchanger) {
        checkExtension(Exchanger.class, "exchanger", exchanger);
        this.exchanger = exchanger;
    }

    /**
     * typo, switch to use {@link #getDispatcher()}
     *
     * @deprecated {@link #getDispatcher()}
     */
    @Deprecated
    @Parameter(excluded = true)
    public String getDispather() {
        return getDispatcher();
    }

    /**
     * typo, switch to use {@link #getDispatcher()}
     *
     * @deprecated {@link #setDispatcher(String)}
     */
    @Deprecated
    public void setDispather(String dispather) {
        setDispatcher(dispather);
    }

    public String getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(String dispatcher) {
        checkExtension(Dispatcher.class, "dispacther", dispatcher);
        this.dispatcher = dispatcher;
    }

    public String getNetworker() {
        return networker;
    }

    public void setNetworker(String networker) {
        this.networker = networker;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(Boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getOptimizer() {
        return optimizer;
    }

    public void setOptimizer(String optimizer) {
        this.optimizer = optimizer;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void destroy() {
        if (name != null) {
            ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(name).destroy();
        }
    }

    /**
     * Just for compatibility.
     * It should be deleted in the next major version, say 2.7.x.
     */
    @Deprecated
    public static void destroyAll() {
        DubboShutdownHook.getDubboShutdownHook().destroyAll();
    }
}