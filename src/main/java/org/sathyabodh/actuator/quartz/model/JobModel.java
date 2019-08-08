package org.sathyabodh.actuator.quartz.model;


public class JobModel {
	private String name;
	private boolean isConcurrentDisallowed;
	private boolean isDurable;
	private String jobClass;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isConcurrentDisallowed() {
		return isConcurrentDisallowed;
	}
	public void setConcurrentDisallowed(boolean isConcurrentDisallowed) {
		this.isConcurrentDisallowed = isConcurrentDisallowed;
	}
	public boolean isDurable() {
		return isDurable;
	}
	public void setDurable(boolean isDurable) {
		this.isDurable = isDurable;
	}
	public String getJobClass() {
		return jobClass;
	}
	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}
}
