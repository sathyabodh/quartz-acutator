package org.sathyabodh.actuator.autoconfigure.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.sathyabodh.actuator.quartz.QuartzTriggerEndPoint;
import org.sathyabodh.actuator.quartz.QuartzTriggerEndPointWebExtension;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({Scheduler.class, SchedulerFactory.class})
@AutoConfigureAfter(QuartzAutoConfiguration.class)
public class QuartzTriggerEndPointAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnEnabledEndpoint
	public QuartzTriggerEndPoint quartzTriggerEndPoint(Scheduler scheduler){
		return new QuartzTriggerEndPoint(scheduler);
	}

	@Bean
	@ConditionalOnBean(QuartzTriggerEndPoint.class)
	public QuartzTriggerEndPointWebExtension quartzTriggerEndPointWebExtension(QuartzTriggerEndPoint quartzTriggerEndPoint){
		return new QuartzTriggerEndPointWebExtension(quartzTriggerEndPoint);
	}
}
