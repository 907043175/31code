package com.code31.common.baseservice.common;

import java.util.concurrent.TimeUnit;

public class SystemConst {

    public final static String CACHE_DB_GROUP_ENTITY = "cache_group_entity";

    public final static String PRE_CACHE_KEY = "mp.";

    public static final String AUTHORIZATION_PROPERTY = "Authorization";

    public static final String PASSPOD_SALT = "2&32@13dUehfadfdds;dfaf";


    public final static Long DEFAULT_CORP_ID = 1l;

    /**
     * 每页最大显示个数
     */
    public final static Integer PAGE_MAX_SIZE = 10;


    //
    public final static Long TOKEN_ERROR = -100000l;

    public final static Long ACCESS_DENIED_ERROR = -200000l; //权限不够


    public final static Long RESPONSE_CODE_SYS_ERROR = -1111111L;


    public static final String DEFAULT_CHARSET = "utf-8";

    //系统默认超级管理员角色ID
    public final static long SUPPER_ADMIN_ROLE_ID = 10000;
    public final static long SUPPER_ADMIN_SYSUSER_ID = 10000;

    public final static int  DAY_SECONDS = (int)(TimeUnit.DAYS.toMillis(1) / TimeUnit.SECONDS.toMillis(1));


}
