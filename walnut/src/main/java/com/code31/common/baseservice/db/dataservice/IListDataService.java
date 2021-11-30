package com.code31.common.baseservice.db.dataservice;

import java.util.List;


public interface IListDataService<T> {

    /**
     * 增加一条数据
     *
     * @param entity
     * @return
     */
    public T add(T entity);

    /**
     * 快速通过onwerId，targetId增加一条纪录
     *
     * @param ownerId
     * @param targetId
     * @return
     */
    public T add(long ownerId, long targetId);

    /**
     * 判断targetId是否存在
     *
     * @param ownerId
     * @param targetId
     * @return
     */
    public boolean isExist(final long ownerId, final long targetId);

    /**
     * 根据ownerId和targetId删除
     *
     * @param ownerId
     * @param targetId
     * @return
     */
    public boolean delete(long ownerId, long targetId);

    /**
     * 获取总数
     *
     * @param ownerId
     * @return
     */
    public int getCount(long ownerId);

    /**
     * 获得列表数据，按照preCursor和nextCursor
     * <p/>
     * 如果preCursor > 0 , 查询上一页
     * 如果nextCursor > 0 , 查询下一页
     * 如果pre,next都为0, 查询第一页
     *
     * @param ownerId
     * @param limit
     * @param preCursor
     * @param nextCursor
     * @return
     */
    public List<Long> getListByCursor(long ownerId, int limit, long preCursor, long nextCursor);

}
