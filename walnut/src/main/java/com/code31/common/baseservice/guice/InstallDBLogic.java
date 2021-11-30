package com.code31.common.baseservice.guice;

import com.code31.common.baseservice.db.annotation.Dao;
import com.code31.common.baseservice.service.ServiceModule;
import com.code31.common.baseservice.db.mybatis.IEntityLogic;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.code31.common.baseservice.utils.PackageUtil;

import java.util.List;
import java.util.Set;


public class InstallDBLogic extends ServiceModule {
    private List<String> _packageList = Lists.newArrayList();

    public InstallDBLogic( List<String> packageList) {
        super();
        _packageList.addAll(Lists.newArrayList(packageList));
    }


    void bindService(Dao dao){
        if (dao == null)
            return;

        try {
            Class<?>logicClass = dao.implClass();
            if (!IEntityLogic.class.isAssignableFrom(logicClass)){
                return;
            }

            Class<? extends IEntityLogic>logicClass1 = (Class<? extends IEntityLogic>)logicClass;

            Class<?>[]interfacesList = logicClass1.getInterfaces();
            if (interfacesList == null || interfacesList.length < 1)
                return;

            Class<IEntityLogic>superClasss1 = null;

            for (Class<?>interfaces:interfacesList){
                if (IEntityLogic.class.isAssignableFrom(interfaces)){
                    superClasss1 = (Class<IEntityLogic>)interfaces;
                    break;
                }
            }
            this.bindService(superClasss1, logicClass1);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void configure() {

        Set<Class<?>> allClass = Sets.newHashSet();

        for (String pk:_packageList){
            Set<Class<?>> classSet = PackageUtil.getPackageClasses(pk, null);
            allClass.addAll(classSet);
        }

        List<Class<? extends IEntityLogic>> logicClassList = Lists.newArrayList();

        for (Class<?>classz:allClass){

            if (!classz.isAnnotationPresent(Dao.class))
                continue;

            Dao dao = classz.getAnnotation(Dao.class);

            Class<? extends IEntityLogic>logicClass1 = dao.implClass();

           bindService(dao);

        }

        System.out.println();

    }
}
