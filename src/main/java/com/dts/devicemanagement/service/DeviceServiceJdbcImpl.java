package com.dts.devicemanagement.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceServiceJdbcImpl implements DeviceService{

    private final JdbcTemplate jdbcTemplate;

    public DeviceServiceJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Object getProjectDevices(String projectId) {

        String sqlQuery = "SELECT d.id as id, d.serial_number as serialNumber, d.project_id as projectId, e.errorCount > 0 as hasErrors, COALESCE(e.errorCount,0) as errorCount, COALESCE(e.eventCount) as eventCount, COALESCE(e.warningCount) as warningCount  FROM devices as d LEFT OUTER JOIN (SELECT device_id, SUM(CASE WHEN type='error' THEN 1 ELSE 0 END) as errorCount, SUM(CASE WHEN type='event' THEN 1 ELSE 0 END) as eventCount, SUM(CASE WHEN type='warning' THEN 1 ELSE 0 END) as warningCount FROM events GROUP BY device_id) as e ON d.id = e.device_id WHERE d.project_id = ?";

        return jdbcTemplate.queryForList(sqlQuery, new Object[] {Long.valueOf(projectId)}).stream().map((obj) -> {
            LinkedHashMap<String, Object> summaryInfo = new LinkedHashMap<>();
            summaryInfo.put("eventCount", obj.get("eventCount"));
            summaryInfo.put("warningCount", obj.get("warningCount"));
            summaryInfo.put("errorCount", obj.get("errorCount"));
            LinkedHashMap<String, Object> deviceInfo = new LinkedHashMap<>();
            deviceInfo.put("id", obj.get("id"));
            deviceInfo.put("serialNumber", obj.get("serialNumber"));
            deviceInfo.put("projectId", obj.get("projectId"));
            deviceInfo.put("hasErrors", obj.get("hasErrors"));
            deviceInfo.put("summaryInfo", summaryInfo);
            LinkedHashMap<Object, Object> serialNumberProperties = new LinkedHashMap<>();
            serialNumberProperties.put(obj.get("serialNumber"), deviceInfo);
            return serialNumberProperties;
        }).collect(Collectors.toList());
    }

}
