package com.goals.course.manager.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Configuration related to running service on AWS ECS Fargate.
 * We overwrite IP address which is registered to eureka server.
 * We are using endpoint which is defined in aws:
 * https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task-metadata-endpoint.html
 * Note! Ensure that this is used in app profiles which are executed on AWS ECS Fargate.
 */
@Slf4j
@Component
@Profile("dev")
public class CloudConfiguration implements BeanPostProcessor {

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${server.port}")
    private int port;

    private String fargateIp;

    @PostConstruct
    private void init() {
        try {
            fargateIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("Could not get the Fargate instance ip address.");
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {

        if (bean instanceof EurekaInstanceConfigBean instanceConfigBean) {
            log.info("EurekaInstanceConfigBean detected. Setting IP address to " + fargateIp);
            instanceConfigBean.setInstanceId(fargateIp + ":" + serviceName + ":" + port);
            instanceConfigBean.setIpAddress(fargateIp);
            instanceConfigBean.setHostname(fargateIp);
            instanceConfigBean.setStatusPageUrl("http://" + fargateIp + ":" + port + "/actuator/info");
            instanceConfigBean.setHealthCheckUrl("http://" + fargateIp + ":" + port + "/actuator/health");
        }

        return bean;
    }
}
