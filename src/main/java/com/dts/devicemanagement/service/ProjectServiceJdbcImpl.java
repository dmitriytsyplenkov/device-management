package com.dts.devicemanagement.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProjectServiceJdbcImpl implements ProjectService {

    private final JdbcTemplate jdbcTemplate;

    public ProjectServiceJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Object getProjectsStats() {
        String sqlQuery = "SELECT p.id, p.name as projectName, COALESCE(dc.deviceCount,0) as deviceCount, SUM(CASE WHEN ds.stableDevice THEN 1 ELSE 0 END) stableDevices, SUM(CASE WHEN ds.deviceWithError THEN 1 ELSE 0 END) deviceWithErrors FROM projects as p LEFT OUTER JOIN (SELECT d.project_id, count(d.id) as deviceCount FROM devices as d GROUP BY d.project_id) as dc ON p.id = dc.project_id LEFT OUTER JOIN (SELECT d.project_id, d.id, MAX(CASE WHEN e.type='error' OR e.type='warning' THEN 1 else 0 END) = 1 as deviceWithError, MIN(CASE WHEN e.type='event' THEN 1 else 0 END) = 1 as stableDevice FROM devices as d INNER JOIN events e on d.id = e.device_id GROUP BY d.id, d.project_id) as ds ON p.id = ds.project_id GROUP BY p.id, p.name, COALESCE(dc.deviceCount,0)";

        return jdbcTemplate.queryForList(sqlQuery).stream().map(stringObjectMap ->  {
            LinkedHashMap<Object, Object> projectMap = new LinkedHashMap<>();
            projectMap.put("id", stringObjectMap.get("id"));
            projectMap.put("projectName", stringObjectMap.get("projectName"));
            LinkedHashMap<String, Object> stats = new LinkedHashMap<>();
            stats.put("deviceCount",stringObjectMap.get("deviceCount"));
            stats.put("stableDevices",stringObjectMap.get("stableDevices"));
            stats.put("deviceWithErrors",stringObjectMap.get("deviceWithErrors"));
            projectMap.put("stats", stats);
            projectMap.put("devices",jdbcTemplate.queryForList("SELECT d.serial_number as serial_number FROM devices as d WHERE d.project_id = ?", new Object[]{stringObjectMap.get("id")}).stream().map(obj -> obj.get("serial_number")).collect(Collectors.toList()));
            return projectMap;
        }).collect(Collectors.toList());

    }

}
