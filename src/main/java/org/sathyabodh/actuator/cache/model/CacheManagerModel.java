package org.sathyabodh.actuator.cache.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class CacheManagerModel {
	private Map<String, CacheModel> caches = new HashMap<>();

	public void add(CacheManager cacheManager){
		cacheManager.getCacheNames().forEach(name->addEntry(cacheManager.getCache(name)));
	}

	private void addEntry(Cache cache){
		CacheModel model = new CacheModel(cache.getNativeCache().getClass().getName());
		getCaches().put(cache.getName(), model);
	}

	public Map<String, CacheModel> getCaches() {
		return caches;
	}
}
