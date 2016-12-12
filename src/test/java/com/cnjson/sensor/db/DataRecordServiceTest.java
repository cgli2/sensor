package com.cnjson.sensor.db;

import java.util.Date;
import java.util.UUID;

import com.cnjson.sensor.db.entity.DataRecord;
import com.cnjson.sensor.db.service.DataRecordService;

public class DataRecordServiceTest {

	private final DataRecordService service = new DataRecordService();

	public void addTest() {
		DataRecord record = new DataRecord();
		Date now = new Date();
		String uid = UUID.randomUUID().toString();
		record.setAcquisitionTime(now);
		record.setReportingTime(now);
		record.setChannelNO(9);
		record.setEquipmentId(uid);
		record.setProcessedData(1f);
		try {
			service.add(record);
			System.out.println(record);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getTest() {
		DataRecord record = new DataRecord();
		try {
			record.setId("0014985c9adf4a8d9840f75bbbb4a738");
			record = service.get(record);
			System.out.println("GET result is :" + record);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteTest() {
		DataRecord record = new DataRecord();
		try {
			record.setId("0014985c9adf4a8d9840f75bbbb4a738");
			int ret = service.delete(record);
			System.out.println("DELETE　RESULT is: " + ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] agrs) {
		System.out.println("----------------------------->start test !");
		DataRecordServiceTest test = new DataRecordServiceTest();
		test.addTest();
		//test.getTest();
		//test.deleteTest();
		System.out.println("------------------------------------->end!");
	}
}
