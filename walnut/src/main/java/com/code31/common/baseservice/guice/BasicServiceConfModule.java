package com.code31.common.baseservice.guice;

import com.code31.common.baseservice.common.xml.JAXBUtil;
import com.code31.common.baseservice.common.xml.server.Servers;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.code31.common.baseservice.common.xml.client.ServiceGroup;

import java.lang.annotation.Annotation;


public abstract class BasicServiceConfModule extends AbstractModule {


    protected synchronized void bindConfByAnnotation(Class<? extends Annotation> annotation, Servers servers, ServiceGroup serviceGroup) {

        Preconditions.checkArgument(servers != null, "servers");
        Preconditions.checkArgument(!servers.getServers().isEmpty(), "Servers.servers");
        Preconditions.checkArgument(serviceGroup != null, "serviceGroup");
        bind(Servers.class).annotatedWith(annotation).toInstance(servers);
        bind(ServiceGroup.class).annotatedWith(annotation).toInstance(serviceGroup);

    }

    protected synchronized void bindXmlConfByAnnotation(Class<? extends Annotation> annotation, String serversXml, String serviceGroupXml) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(serversXml), "serversXml");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(serviceGroupXml), "serviceGroupXml");
        this.bindConfByAnnotation(annotation, JAXBUtil.unmarshal(Servers.class, serversXml), JAXBUtil.unmarshal(ServiceGroup.class, serviceGroupXml));

    }


}
