package com.code31.common.baseservice.guice;

import com.code31.common.baseservice.common.annotation.NettyController;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.code31.common.baseservice.utils.PackageUtil;

import javax.ws.rs.Path;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

public class NettyControllerPackagesModule extends AbstractModule {

    private List<String> _packageList = Lists.newArrayList();
    public NettyControllerPackagesModule(String ... packages){
        _packageList.addAll(Lists.newArrayList(packages));
    }


    protected void bindNettyController( Set<Class<?>> classSet) {

        // 绑定api接口
        for (Class<?> clazz : classSet) {

            if ( Modifier.isAbstract(clazz.getModifiers()))
                continue;

            if (!clazz.isAnnotationPresent(NettyController.class))
                continue;

            if (!clazz.isAnnotationPresent(Path.class))
                continue;

            Key key = Key.get(clazz);

            bind(clazz).annotatedWith(NettyController.class).to(key);
        }

    }

    @Override
    protected void configure() {

        // 绑定api接口
        for (String pk:_packageList){
            Set<Class<?>> classSet = PackageUtil.getPackageClasses(pk, null);
            bindNettyController(classSet);
        }

    }

}
