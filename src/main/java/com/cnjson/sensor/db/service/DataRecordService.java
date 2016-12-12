package com.cnjson.sensor.db.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.cnjson.sensor.db.dao.AbstractDao;
import com.cnjson.sensor.db.dao.IDataRecordDao;
import com.cnjson.sensor.db.entity.DataRecord;
import com.cnjson.sensor.db.utils.DaoUtils;

public class DataRecordService extends AbstractDao<DataRecord> implements IDataRecordDao {

	/**
	 * 批量添加采集数据
	 * 
	 * @param records
	 */
	public void addBatch(List<DataRecord> records) {
		StringBuilder sb = new StringBuilder();
		String sDate = getCurrentDate();
		if (records != null && records.size() > 0) {
			sb.append("INSERT INTO col_data_record(id,equipment_id,channel_NO,check_item_id,processed_data,"
					+ "acquisition_time,reporting_time)values");
			for (DataRecord record : records) {
				getSubSql(record, sb, sDate);
				sb.append(",");
			}
			sb.setLength(sb.length() - 1);
			sb.append(";");
			this.executeBatch(sb.toString());
			// DataBase.executeBatch(sb.toString());
			sb.setLength(0);
		}
	}

	private void getSubSql(DataRecord record, StringBuilder sb, String sDate) {
		sb.append("(");
		sb.append("'" + uuid() + "',");
		sb.append("'" + record.getEquipmentId() + "',");
		sb.append("'" + record.getChannelNO() + "',");
		sb.append("'" + record.getCheckItemId() + "',");
		sb.append("'" + record.getProcessedData() + "',");
		// sb.append("'" + record.getDataUnit() + "',");
		sb.append("'" + record.getAcquisitionTime() + "',");
		sb.append("'" + sDate + "')");
	}

	private String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	private String getCurrentDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sDate = sdf.format(date);
		return sDate;
	}

	/**
	 * 通过网络地址获取设备ID
	 * 
	 * @param address
	 * @return
	 */
	public String getEquipmentIdByAddress(String address) {
		String sql = "Select equipment_id from col_network where location=?";
		Object ret = DaoUtils.getValue(sql, address);
		if (ret != null) {
			return ret.toString();
		}
		return address;
	}

	/**
	 * 通过设备份ID获取通道对应的检测项目列表数据。 注意通道数据位必须要与实际的检测项目对应起来。
	 * 
	 * @param equipmentId
	 * @return
	 */
	public List<Object> getCheckItemIds(String equipmentId, int num) {
		String sql = "SELECT check_item_id from col_reporting_channel where equipment_id=? order by channel_NO asc limit "
				+ num;// '" + equipmentId"'
		return DaoUtils.queryFirstColumns(sql, equipmentId);
	}

	@Override
	public DataRecord get(String id) throws Exception {
		DataRecord entity = new DataRecord();
		entity.setId(id);
		return get(entity);
	}

	@Override
	public boolean executeBatch(String sql) {
		return DaoUtils.execute(sql);
	}
}
