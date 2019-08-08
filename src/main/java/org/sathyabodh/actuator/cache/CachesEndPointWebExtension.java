package org.sathyabodh.actuator.cache;

import org.sathyabodh.actuator.cache.exception.NonUniqueCacheException;
import org.sathyabodh.actuator.cache.model.CacheEntryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.annotation.EndpointWebExtension;
import org.springframework.lang.Nullable;


@EndpointWebExtension(endpoint = CachesEndPoint.class)
public class CachesEndPointWebExtension {
	private static Logger log = LoggerFactory.getLogger(CachesEndPoint.class);

	private CachesEndPoint cachesEndPoint;

	public CachesEndPointWebExtension(CachesEndPoint cachesEndPoint) {
		this.cachesEndPoint = cachesEndPoint;
	}

	@ReadOperation
	public WebEndpointResponse<CacheEntryModel> cache(@Selector String name, @Nullable String cacheManager) {
		CacheEntryModel model = null;
		int status;
		try {
			model = cachesEndPoint.cache(name, cacheManager);
			status = null == model ? WebEndpointResponse.STATUS_NOT_FOUND : WebEndpointResponse.STATUS_OK;
		} catch (NonUniqueCacheException e) {
			log.error("Error in getting cache detials", e);
			status = WebEndpointResponse.STATUS_BAD_REQUEST;
		}
		return new WebEndpointResponse<>(model, status);
	}

	@DeleteOperation
	public WebEndpointResponse<Void> delete(@Selector String name, @Nullable String cacheManager) {
		int status;
		try {
			boolean isSuccess = cachesEndPoint.evictCaches(name, cacheManager);
			status = isSuccess ? WebEndpointResponse.STATUS_OK : WebEndpointResponse.STATUS_NOT_FOUND;
		} catch (NonUniqueCacheException e) {
			log.error("Error in clearing cache detials", e);
			status = WebEndpointResponse.STATUS_BAD_REQUEST;
		}
		return new WebEndpointResponse<>(status);
	}
}
