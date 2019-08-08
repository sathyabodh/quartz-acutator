package org.sathyabodh.actuator.quartz.model;

import java.util.List;

public class JobDetailModel extends JobModel{
	private String group;
	private List<TriggerDetailModel> triggers;

	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public List<TriggerDetailModel> getTriggers() {
		return triggers;
	}
	public void setTriggers(List<TriggerDetailModel> triggers) {
		this.triggers = triggers;
	}
}
