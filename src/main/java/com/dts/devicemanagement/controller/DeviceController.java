package com.dts.devicemanagement.controller;


import com.dts.devicemanagement.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/project/{projectId}/devices")
    public ResponseEntity<Object> projectDevices(@PathVariable String projectId) {

        return ResponseEntity.ok(deviceService.getProjectDevices(projectId));
    }


}
