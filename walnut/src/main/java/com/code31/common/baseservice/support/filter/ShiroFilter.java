package com.code31.common.baseservice.support.filter;


import com.code31.common.baseservice.common.annotation.NoPermession;
import com.code31.common.baseservice.common.annotation.RequiresPermissions;
import com.code31.common.baseservice.common.response.ObjMsgResponse;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

public class ShiroFilter implements ContainerRequestFilter {
    protected static final Logger logger = LoggerFactory.getLogger(ShiroFilter.class);

    private static final String ENCODING_UTF_8 = "UTF-8";

    public final static Long ACCESS_DENIED_ERROR = -200000l;

    private  String _AUTHORIZATION_PROPERTY;

    private ShiroFilterHandle _filterHandle;

    private static  ServerResponse ACCESS_DENIED = new ServerResponse("权限不够", 401,//
            new Headers<Object>());


    private static ObjMsgResponse LOGINMSG = ObjMsgResponse.FAIL(ACCESS_DENIED_ERROR,
            "权限不够");

    static
    {
        ACCESS_DENIED.setEntity(LOGINMSG);

    }

    public ShiroFilter(String authorization,ShiroFilterHandle handle){
        _AUTHORIZATION_PROPERTY = authorization;
        _filterHandle = handle;

    }


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // 反射获得method
        ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext
                .getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
        Method method = methodInvoker.getMethod();
        if (method.isAnnotationPresent(NoPermession.class)) {// 无需权限认证
            return;
        }

    //    requestContext.setProperty(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY, "*/*; charset=UTF-8");
     //   requestContext.setProperty(InputPart.DEFAULT_CHARSET_PROPERTY, "UTF-8");

        //需要的权限
        RequiresPermissions requiresPermissions = method.getAnnotation(RequiresPermissions.class);
        if (requiresPermissions == null){
         //   requestContext.abortWith(ACCESS_DENIED);
            return;
        }
        String[] pers = requiresPermissions.value();
        if (pers == null || pers.length <= 0) {
            requestContext.abortWith(ACCESS_DENIED);
            return;
        }

        final MultivaluedMap<String, String> headers = requestContext.getHeaders();
        final List<String> authorization = headers.get(_AUTHORIZATION_PROPERTY);

        try {

            if (authorization != null && authorization.size() > 0){

                String authorToken = authorization.get(0);
                if (_filterHandle != null && StringUtils.isNotEmpty(authorToken)){
                    boolean isok =  _filterHandle.checkUserPermission(authorToken,pers);
                    if (!isok){
                        requestContext.abortWith(ACCESS_DENIED);//权限不够
                        //logger
                        return;
                    }
                }

            }


        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }


    }
}
