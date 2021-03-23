package com.hyc.backend.controller;

import com.hyc.backend.packet.NetWorkInterface;
import com.hyc.backend.service.AttackService;
import jpcap.NetworkInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kol Huang
 * @date 2021/3/22
 */
@Controller
@RequestMapping("attack")
public class AttackController {

    @Autowired
    private AttackService attackService;

    @GetMapping(value = "/getDeviceList")
    @ResponseBody
    public List<NetWorkInterface> getDeviceList(){
        List<NetWorkInterface> devices = new ArrayList<>();
        NetworkInterface[] interfaces = attackService.getDevices();
        if(interfaces != null){
            for (NetworkInterface networkInterface : interfaces) {
                NetWorkInterface ni = new NetWorkInterface();
                ni.setName(networkInterface.name);
                ni.setDescription(networkInterface.description);
                ni.setDataLinkName(networkInterface.datalink_name);
                ni.setDataLinkDescription(networkInterface.datalink_description);
                devices.add(ni);
            }
        }
        return devices;
    }
}
