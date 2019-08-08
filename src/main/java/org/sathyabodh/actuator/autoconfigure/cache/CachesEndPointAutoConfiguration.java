package org.sathyabodh.actuator.autoconfigure.cache;

import java.util.Map;

import org.sathyabodh.actuator.cache.CachesEndPoint;
import org.sathyabodh.actuator.cache.CachesEndPointWebExtension;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(CacheManager.class)
@AutoConfigureAfter(CacheAutoConfiguration.class)
public class CachesEndPointAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnEnabledEndpoint
	public CachesEndPoint cachesEndPoint(Map<String, CacheManager> cacheManagers){
		return new CachesEndPoint(cacheManagers);
	}

	@Bean
	@ConditionalOnBean(CachesEndPoint.class)
	public CachesEndPointWebExtension cachesEndPointWebExtension(CachesEndPoint cachesEndPoint){
		return new CachesEndPointWebExtension(cachesEndPoint);
	}
}
