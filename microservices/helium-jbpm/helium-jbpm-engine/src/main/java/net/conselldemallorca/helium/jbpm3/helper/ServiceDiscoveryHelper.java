package net.conselldemallorca.helium.jbpm3.helper;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import org.springframework.stereotype.Component;

@Component
public class ServiceDiscoveryHelper {

//    private static ApplicationInfoManager applicationInfoManager;
//    private static EurekaClient eurekaClient;

//    private static synchronized ApplicationInfoManager initializeApplicationInfoManager(EurekaInstanceConfig instanceConfig) {
//        if (applicationInfoManager == null) {
//            InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
//            applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
//        }
//
//        return applicationInfoManager;
//    }
//
//    private static synchronized EurekaClient initializeEurekaClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig clientConfig) {
//        if (eurekaClient == null) {
//            eurekaClient = new DiscoveryClient(applicationInfoManager, clientConfig);
//        }
//
//        return eurekaClient;
//    }

    public String getAddress(String serviceName) {

        DiscoveryManager.getInstance().initComponent(new MyDataCenterInstanceConfig(), new DefaultEurekaClientConfig());

        InstanceInfo nextServerInfo = null;
        try {
            nextServerInfo = DiscoveryManager.getInstance()
                    .getEurekaClient()
                    .getNextServerFromEureka(serviceName, false);
        } catch (Exception e) {
            System.err.println("Cannot get an instance of example service to talk to from eureka");
            System.exit(-1);
        }

        System.out.println("Found an instance of example service to talk to from eureka: "
                + nextServerInfo.getVIPAddress() + ":" + nextServerInfo.getPort());

        System.out.println("healthCheckUrl: " + nextServerInfo.getHealthCheckUrl());
        System.out.println("override: " + nextServerInfo.getOverriddenStatus());

        System.out.println("Server Host Name " + nextServerInfo.getHostName() + " at port " + nextServerInfo.getPort());

        return nextServerInfo.getVIPAddress() + ":" + nextServerInfo.getPort();
    }

}
