package org.sathyabodh.actuator.quartz;

import java.util.Set;
import java.util.stream.Collectors;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.sathyabodh.actuator.quartz.exception.UnsupportStateChangeException;
import org.sathyabodh.actuator.quartz.model.GroupModel;
import org.sathyabodh.actuator.quartz.model.TriggerDetailModel;
import org.sathyabodh.actuator.quartz.service.TriggerModelBuilder;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.lang.Nullable;

@Endpoint(id = "quartz-triggers")
public class QuartzTriggerEndPoint {
	private Scheduler scheduler;
	private TriggerModelBuilder triggerModelBuilder = new TriggerModelBuilder();

	public QuartzTriggerEndPoint(Scheduler scheduler){
		this.scheduler = scheduler;
	}

	@ReadOperation
	public GroupModel<TriggerDetailModel> listTriggers(@Nullable String group, @Nullable String name) throws SchedulerException {
		try {
			if (name != null && group != null) {
				TriggerDetailModel model = triggerModelBuilder.buildTriggerDetailModel(scheduler, new TriggerKey(name, group));
				if (model == null) {
					return null;
				}
				GroupModel<TriggerDetailModel> groupModel = new GroupModel<>();
				groupModel.add(group, model);
				return groupModel;
			}
			GroupMatcher<TriggerKey> triggerGroupMatcher = group == null ?
					GroupMatcher.anyTriggerGroup():GroupMatcher.triggerGroupEquals(group);
			Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(triggerGroupMatcher);
			if(name != null){
				triggerKeys = triggerKeys.stream().filter(key->name.equals(key.getName())).collect(Collectors.toSet());
			}
			if (triggerKeys == null || triggerKeys.isEmpty()) {
				return null;
			}
			GroupModel<TriggerDetailModel> groupModel = new GroupModel<>();
			triggerKeys.forEach(key->addTriggerDetailModel(groupModel, key));
			return groupModel;
		} catch (SchedulerException e) {
			throw e;
		}
	}

	private void addTriggerDetailModel(GroupModel<TriggerDetailModel> groupModel, TriggerKey key){
		TriggerDetailModel model;
		try {
			model = triggerModelBuilder.buildTriggerDetailModel(scheduler, key);
			groupModel.add(key.getGroup(), model);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	@WriteOperation
	public boolean modifyTriggerStatus(@Selector String group, @Selector String name, @Selector String state)
			throws SchedulerException {
		TriggerKey triggerKey = new TriggerKey(name, group);
		Trigger trigger = scheduler.getTrigger(triggerKey);
		if (trigger == null)
			return false;
		else if (QuartzState.PAUSE.equals(state)) {
			scheduler.pauseTrigger(triggerKey);
		} else if (QuartzState.RESUME.equals(state)) {
			scheduler.resumeTrigger(triggerKey);
		} else {
			throw new UnsupportStateChangeException(String.format("unsupported state change. state:[%s]", state));
		}
		return true;
	}

	@WriteOperation
	public boolean modifyTriggersStatus(@Selector String group, @Selector String state) throws SchedulerException {
		GroupMatcher<TriggerKey> triggerGroupMatcher = GroupMatcher.triggerGroupEquals(group);
		Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(triggerGroupMatcher);
		if (triggerKeys == null || triggerKeys.isEmpty()) {
			return false;
		} else if (QuartzState.PAUSE.equals(state)) {
			scheduler.pauseTriggers(triggerGroupMatcher);
		} else if (QuartzState.RESUME.equals(state)) {
			scheduler.resumeTriggers((triggerGroupMatcher));
		} else {
			throw new UnsupportStateChangeException(String.format("unsupported state change. state:[%s]", state));
		}
		return true;
	}
}
