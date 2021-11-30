package com.code31.common.baseservice.support.filter;


import com.alibaba.fastjson.JSONObject;
import org.jboss.resteasy.spi.DefaultOptionsMethodException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ApplicationExceptionHandler implements ExceptionMapper<Exception> {

	protected static final Logger logger = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

	@Override
	public Response toResponse(Exception exception) {

//NotAllowedException
		JSONObject response = new JSONObject();
		response.put("code", 0);

		if (exception instanceof DefaultOptionsMethodException){
			return Response.status(Status.OK).entity(response).build();
		}
		else if (exception instanceof NotAllowedException){
			return Response.status(Status.OK).entity(response).build();
		//	CoreLoggers.app_Error.error(JSONUtil.obj2Json(exception));
		}
		else{
			response.put("code", -10000l);
			response.put("msg", "服务器异常");
		}

	//	logger.error(exception.getMessage(), exception);

		logger.error(exception.getMessage(), exception);

		return Response.status(Status.OK).entity(response).build();
	}
}
