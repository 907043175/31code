package com.code31.common.baseservice.support;

import com.code31.common.baseservice.common.annotation.NettyController;
import com.code31.common.baseservice.support.filter.ApplicationExceptionHandler;
import com.code31.common.baseservice.support.filter.ShiroFilter;
import com.code31.common.baseservice.support.filter.ShiroFilterHandle;
import com.code31.common.baseservice.common.SystemConst;
import com.google.common.collect.Lists;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public final class UndertowServer {
    protected static final Logger logger = LoggerFactory.getLogger(UndertowServer.class);

    // 主机
    private String host;

    // 端口
    private int port;

    private Injector _injector;
    ShiroFilter _shiroFilter;

    UndertowJaxrsServer _service;

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    /**
     * 默认构造函数
     *
     * @param host 监听主机
     * @param port 监听端口
     */
    public UndertowServer(String host, int port,
                          Injector injector,
                          ShiroFilterHandle shiroHandle) {
        this.host = host;
        this.port = port;
        this._injector = injector;
        this._shiroFilter = new ShiroFilter(SystemConst.AUTHORIZATION_PROPERTY,shiroHandle);
    }

    /**
     * 启动服务器
     *
     * @param appName  部署的应用名称
     * @param rootPath 根路径
     * @param appPath  应用路径
     */
    public void start(String appName, String rootPath, String appPath) {
        Undertow.Builder serverBuilder = Undertow.builder()
                .addHttpListener(port, host)

                ;

        UndertowJaxrsServer server = new UndertowJaxrsServer();
        server.start(serverBuilder);

        ResteasyDeployment deployment = new ResteasyDeploymentImpl();

        List<Object> providers = Lists.newArrayList();

        List<Object> resList = Lists.newArrayList();

        Map<Key<?>, Binding<?>> bindings = _injector.getBindings();

        for (Key<?> key : bindings.keySet()) {
            Type annotationType = key.getAnnotationType();
            if (annotationType != null && annotationType == NettyController.class) {
                Type type = key.getTypeLiteral().getType();

                resList.add(_injector.getInstance((Class<?>) type));
            }
        }
        deployment.setResources(resList);


//        {
//            ObjectMapper mapper = new ObjectMapper();
//
//            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//            mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
//            mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true) ;
//            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//
//            mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS, true) ;
//
//            ResteasyJackson2Provider resteasyJacksonProvider = new ResteasyJackson2Provider();
//            resteasyJacksonProvider.setMapper(mapper);
//
//            providers.add(resteasyJacksonProvider);
//        }

        providers.add(new ApplicationExceptionHandler());
        providers.add(_shiroFilter);

        CorsFilter filter = new CorsFilter();
        filter.setAllowedMethods("GET,POST,PUT,DELETE,OPTIONS");
        filter.getAllowedOrigins().add("*");
        String headers = filter.getAllowedHeaders();
        System.out.println(headers);
        if (StringUtils.isNotEmpty(headers)){
            headers = headers + ",X-Requested-With, Content-Type,Origin, Content-Length,Version_dd,AccessKeyId_dd,Signature_dd,"+SystemConst.AUTHORIZATION_PROPERTY;
        }else{
            headers = "X-Requested-With, Content-Type,Origin, Content-Length,Version_dd,AccessKeyId_dd,Signature_dd,"+SystemConst.AUTHORIZATION_PROPERTY;
        }
        filter.setAllowedHeaders(headers);
        filter.setAllowCredentials(true);

        providers.add(filter);

//        //SecurityInterceptor
//        providers.add(new McContainerRequestFilter());
//        //Mixin provider
//        providers.add(new McJacksonJson2Provider());
//        providers.add(new NotAcceptableExceptionMapper());
//        providers.add(new NotAllowedExceptionMapper());
//        providers.add(new NotAuthorizedExceptionMapper());
//        providers.add(new NotFoundExceptionMapper());
//        providers.add(new NotSupportedExceptionMapper());

        deployment.setProviders(providers);

		DeploymentInfo di = server.undertowDeployment(deployment);

        di.setClassLoader(UndertowServer.class.getClassLoader());

		di.setContextPath(rootPath);
		di.setDeploymentName(appName);

        _service = server.deploy(di);



    }

    public void stop(){
        try {
            _service.stop();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
    }

}
