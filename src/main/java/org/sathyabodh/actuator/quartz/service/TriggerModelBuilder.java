package org.sathyabodh.actuator.quartz.service;

import java.util.List;
import java.util.stream.Collectors;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.sathyabodh.actuator.quartz.model.TriggerDetailModel;

public class TriggerModelBuilder {

	public List<TriggerDetailModel> buildTriggerDetailModel(Scheduler scheduler, JobKey jobKey) throws SchedulerException{
		List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
		return triggers.stream().map(t->buildTriggerDetailModel(scheduler, t)).collect(Collectors.toList());
	}

	public TriggerDetailModel buildTriggerDetailModel(Scheduler scheduler, TriggerKey triggerKey) throws SchedulerException{
		Trigger trigger = scheduler.getTrigger(triggerKey);
		return buildTriggerDetailModel(scheduler, trigger);
	}

	private TriggerDetailModel buildTriggerDetailModel(Scheduler scheduler, Trigger trigger){
		TriggerDetailModel model = new TriggerDetailModel();
		model.setName(trigger.getKey().getName());
		model.setNextFireTime(trigger.getNextFireTime());
		model.setPreviousFireTime(trigger.getPreviousFireTime());
		model.setStartTime(trigger.getStartTime());
		model.setEndTime(trigger.getEndTime());
		model.setGroup(trigger.getKey().getGroup());
		model.setJobKey(trigger.getJobKey().toString());
		TriggerState triggerState;
		try {
			triggerState = scheduler.getTriggerState(trigger.getKey());
			model.setState(triggerState.toString());
		} catch (SchedulerException e) {
			model.setState("Could not set due to error");
		}
		return model;
	}

}
