package com.cnjson.sensor.db.entity;

import com.cnjson.sensor.db.annotation.FieldMeta;
import com.cnjson.sensor.db.annotation.TableName;

@TableName("col_data_record")
public class DataRecord extends BaseEntity<DataRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3782778440146823066L;

	@FieldMeta(name = "equipment_id", description = "所属设备编号")
	private String equipmentId; // 所属设备编号,设备地址

	@FieldMeta(name = "channel_NO", description = "通道索引位置号")
	private java.lang.Integer channelNO; // 所属通道索引位置号

	// private Float rawData; // 原始采集的数据

	@FieldMeta(name = "processed_data", description = "处理后的数据")
	private Float processedData; // 处理后的数据

	protected String dataUnit; // 数据单位

	@FieldMeta(name = "acquisition_time", description = "采集时间")
	private java.util.Date acquisitionTime; // 采集时间

	@FieldMeta(name = "reporting_time", description = "上报时间")
	private java.util.Date reportingTime; // 上报时间

	@FieldMeta(name = "check_item_id", description = "检测项目引用ID")
	private String checkItemId;// 检测项目引用ID

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public java.lang.Integer getChannelNO() {
		return channelNO;
	}

	public void setChannelNO(java.lang.Integer channelNO) {
		this.channelNO = channelNO;
	}

	public Float getProcessedData() {
		return processedData;
	}

	public void setProcessedData(Float processedData) {
		this.processedData = processedData;
	}

	public String getDataUnit() {
		return dataUnit;
	}

	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	public java.util.Date getAcquisitionTime() {
		return acquisitionTime;
	}

	public void setAcquisitionTime(java.util.Date acquisitionTime) {
		this.acquisitionTime = acquisitionTime;
	}

	public java.util.Date getReportingTime() {
		return reportingTime;
	}

	public void setReportingTime(java.util.Date reportingTime) {
		this.reportingTime = reportingTime;
	}

	public String getCheckItemId() {
		return checkItemId;
	}

	public void setCheckItemId(String checkItemId) {
		this.checkItemId = checkItemId;
	}

	@Override
	public String toString() {
		return "DataRecord [equipmentId=" + equipmentId + ", channelNO=" + channelNO + ", processedData="
				+ processedData + ", dataUnit=" + dataUnit + ", acquisitionTime=" + acquisitionTime + ", reportingTime="
				+ reportingTime + ", checkItemId=" + checkItemId + "]";
	}

}
