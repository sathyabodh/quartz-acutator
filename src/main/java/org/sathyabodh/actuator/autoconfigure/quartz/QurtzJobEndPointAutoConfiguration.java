package org.sathyabodh.actuator.autoconfigure.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.sathyabodh.actuator.quartz.QuartzJobEndPoint;
import org.sathyabodh.actuator.quartz.QuartzJobEndPointWebExtension;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ Scheduler.class, SchedulerFactory.class })
@AutoConfigureAfter(QuartzAutoConfiguration.class)
public class QurtzJobEndPointAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnEnabledEndpoint
	public QuartzJobEndPoint quartzJobEndPoint(Scheduler scheduler) {
		return new QuartzJobEndPoint(scheduler);
	}

	@Bean
	@ConditionalOnBean(QuartzJobEndPoint.class)
	public QuartzJobEndPointWebExtension quartzJobEndPointWebExtension(QuartzJobEndPoint quartzJobEndPoint) {
		return new QuartzJobEndPointWebExtension(quartzJobEndPoint);
	}

}
