package com.cnjson.sensor.db.dao;

import com.cnjson.sensor.db.entity.DataRecord;

public interface IDataRecordDao extends IBaseDao<DataRecord> {
	
	/**
	 * 此接口有特殊的要求，需要批量执行一个方法
	 * @param sql 完整的SQL语句。
	 * @return if success true otherwise false.
	 */
	public boolean executeBatch(String sql);

}
