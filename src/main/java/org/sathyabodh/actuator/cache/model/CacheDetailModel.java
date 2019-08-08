package org.sathyabodh.actuator.cache.model;

import java.util.HashMap;
import java.util.Map;

public class CacheDetailModel {

	private Map<String, CacheManagerModel> cacheManagers = new HashMap<>();

	public CacheManagerModel get(String cacheManager){
		CacheManagerModel model = getCacheManagers().get(cacheManager);
		if(model == null){
			model = new CacheManagerModel();
			getCacheManagers().put(cacheManager, model);
		}
		return model;
	}

	public Map<String, CacheManagerModel> getCacheManagers() {
		return cacheManagers;
	}
}
