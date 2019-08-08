package org.sathyabodh.actuator.cache.model;

import org.springframework.cache.Cache;

public class CacheEntryModel extends CacheModel {
	private String name;
	private String cacheManager;

	public CacheEntryModel(String cacheManager, Cache cache){
		super(cache.getNativeCache().getClass().getName());
		this.setName(cache.getName());
		this.setCacheManager(cacheManager);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCacheManager() {
		return cacheManager;
	}
	public void setCacheManager(String cacheManager) {
		this.cacheManager = cacheManager;
	}
}
