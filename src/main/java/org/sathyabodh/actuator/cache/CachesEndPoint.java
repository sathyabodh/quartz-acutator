package org.sathyabodh.actuator.cache;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.sathyabodh.actuator.cache.exception.NonUniqueCacheException;
import org.sathyabodh.actuator.cache.model.CacheDetailModel;
import org.sathyabodh.actuator.cache.model.CacheEntryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;

@Endpoint(id="caches")
public class CachesEndPoint {

	private static Logger log = LoggerFactory.getLogger(CachesEndPoint.class);
	private Map<String, CacheManager> cacheManagers ;

	public CachesEndPoint(Map<String, CacheManager> cacheManagers) {
		this.cacheManagers = cacheManagers;
	}

	@ReadOperation
	public CacheDetailModel caches(){
		final CacheDetailModel detailModel = new CacheDetailModel();
		cacheManagers.entrySet().forEach(entry->detailModel.get(entry.getKey()).add(entry.getValue()));
		return detailModel;
	}

	@ReadOperation
	public CacheEntryModel cache(@Selector String name, @Nullable String cacheManager){
		return findUniqueCache(name, cacheManager);
	}

	@DeleteOperation
	public boolean evictCaches(@Selector String name, @Nullable String cacheManager){
		CacheEntryModel cache = findUniqueCache(name, cacheManager);
		return cache == null ?  false:clearCache(cache);
	}

	private boolean clearCache(CacheEntryModel cacheEntryModel){
		CacheManager cacheManager = cacheManagers.get(cacheEntryModel.getCacheManager());
		if(cacheManager == null){
			return false;
		}
		Cache cache = cacheManager.getCache(cacheEntryModel.getName());
		if(cache == null){
			return false;
		}

		log.info("Clearning cache:[{}]", cache.getName());
		cache.clear();
		log.info("Cleared cache:[{}]", cache.getName());
		return true;
	}

	private CacheEntryModel findUniqueCache(String name, String cacheManager){
		List<CacheEntryModel> caches = cacheManagers.entrySet().stream().filter(it-> cacheManager == null || cacheManager.equals(it.getKey()))
		.map(entry->entry.getValue().getCache(name) == null ? null : new CacheEntryModel(entry.getKey(), entry.getValue().getCache(name)))
		.filter(entry->entry != null).collect(Collectors.toList());
		if(caches.size() > 1){
			throw new NonUniqueCacheException(
					String.format("Non unique cache name:%s. "
							+ "Please use cacheManager name to uniquely identify", name));
		}
		return caches.isEmpty() ? null : caches.get(0);
	}


}
