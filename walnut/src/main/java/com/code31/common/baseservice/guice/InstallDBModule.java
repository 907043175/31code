package com.code31.common.baseservice.guice;

import com.code31.common.baseservice.db.annotation.Dao;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.PrivateModule;
import com.google.inject.name.Names;
import com.code31.common.baseservice.utils.PackageUtil;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.guice.MyBatisModule;
import org.mybatis.guice.datasource.builtin.PooledDataSourceProvider;
import org.mybatis.guice.datasource.helper.JdbcHelper;

import java.util.List;
import java.util.Properties;
import java.util.Set;


public class InstallDBModule extends PrivateModule {
    private  static String _path;

    private List<String> _packageList = Lists.newArrayList();


    public InstallDBModule(String configFile,List<String> packageList) {
        super();
        _path = configFile;
        _packageList.addAll(Lists.newArrayList(packageList));
    }


    private void exposeDB(Set<Class<?>> classSet){
        for (Class<?> clazz : classSet) {
            if (!clazz.isAnnotationPresent(Dao.class))
                continue;

            Dao dao = clazz.getAnnotation(Dao.class);

            Class daoImpl = dao.implClass();
            expose(daoImpl);
        }

    }


    @Override
    protected void configure() {

        Set<Class<?>> allClass = Sets.newHashSet();
        for (String pk:_packageList){
            Set<Class<?>> classSet = PackageUtil.getPackageClasses(pk, null);
            allClass.addAll(classSet);
        }




        install(new MyBatisModule() {

            @Override
            protected void initialize() {

                bindDataSourceProviderType(PooledDataSourceProvider.class); //DruidDataSourceProvider PooledDataSourceProvider
                bindTransactionFactoryType(JdbcTransactionFactory.class);

                install(JdbcHelper.MySQL);

                Properties connectionProps = new Properties();

                try {

                    connectionProps.load(Resources.getResourceAsStream(_path));

                }catch (Exception e){
                    e.printStackTrace();
                }

                Names.bindProperties(binder(), connectionProps);


                for (Class<?> clazz : allClass) {
                    if (!clazz.isAnnotationPresent(Dao.class))
                        continue;

                    Dao dao = clazz.getAnnotation(Dao.class);

                    addMapperClass(clazz);

                    Class daoImpl = dao.implClass();
                    bind(daoImpl);
                }


            }
        });

        exposeDB(allClass);

        expose(SqlSessionFactory.class);
    }

}
