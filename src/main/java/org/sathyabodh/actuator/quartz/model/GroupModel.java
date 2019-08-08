package org.sathyabodh.actuator.quartz.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupModel<T> {

	private Map<String, List<T>> groups = new HashMap<>();

	public void add(String group, T model){
		List<T> models = getGroups().get(group);
		if(models == null){
			models = new ArrayList<>();
			getGroups().put(group, models);
		}
		models.add(model);
	}

	public Map<String, List<T>> getGroups() {
		return groups;
	}

}
