package org.sathyabodh.actuator.quartz;

import java.util.Set;
import java.util.stream.Collectors;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.sathyabodh.actuator.quartz.exception.UnsupportStateChangeException;
import org.sathyabodh.actuator.quartz.model.GroupModel;
import org.sathyabodh.actuator.quartz.model.JobDetailModel;
import org.sathyabodh.actuator.quartz.model.JobModel;
import org.sathyabodh.actuator.quartz.service.TriggerModelBuilder;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.lang.Nullable;

@Endpoint(id = "quartz-jobs")
public class QuartzJobEndPoint {

	private Scheduler scheduler;

	private TriggerModelBuilder triggerModelBuilder = new TriggerModelBuilder();

	public QuartzJobEndPoint(Scheduler scheduler){
		this.scheduler = scheduler;
	}

	@ReadOperation
	public GroupModel<JobModel> listJobs(@Nullable String group, @Nullable String name) throws SchedulerException {
		try {
			if (name != null && group != null) {
				JobModel model = createJobModel(new JobKey(name, group));
				if (model == null) {
					return null;
				}
				GroupModel<JobModel> jobGroupModel = new GroupModel<>();
				jobGroupModel.add(group, model);
				return jobGroupModel;
			}
			GroupMatcher<JobKey> jobGroupMatcher = group == null ?
					GroupMatcher.anyJobGroup():GroupMatcher.jobGroupEquals(group);

			Set<JobKey> jobKeys = scheduler.getJobKeys(jobGroupMatcher);
			if(name != null){
				jobKeys = jobKeys.stream().filter(key->name.equals(key.getName())).collect(Collectors.toSet());
			}
			if (jobKeys == null || jobKeys.isEmpty()) {
				return null;
			}
			GroupModel<JobModel> jobGroupModel = new GroupModel<>();
			jobKeys.forEach(key->{JobModel model = createJobModel(key);
					jobGroupModel.add(key.getGroup(), model);
					});
			return jobGroupModel;
		} catch (SchedulerException e) {
			throw e;
		}
	}

	@ReadOperation
	public JobDetailModel getJobDetail(@Selector String group, @Selector String name) throws SchedulerException{
		JobDetail jobDetail = scheduler.getJobDetail(new JobKey(name, group));
		JobDetailModel model = new JobDetailModel();
		copyJobDetailModel(jobDetail, model);
		model.setGroup(jobDetail.getKey().getGroup());
		model.setTriggers(triggerModelBuilder.buildTriggerDetailModel(scheduler, jobDetail.getKey()));
		return model;
	}

	private JobModel createJobModel(JobKey key){
		try {
			JobDetail jobDetail = scheduler.getJobDetail(key);
			if (jobDetail == null) {
				return null;
			}
			JobModel model = new JobModel();
			copyJobDetailModel(jobDetail, model);
			return model;

		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}

	}

	private void copyJobDetailModel(JobDetail jobDetail,JobModel model) {
			model.setName(jobDetail.getKey().getName());
			model.setDurable(jobDetail.isDurable());
			model.setConcurrentDisallowed(jobDetail.isConcurrentExectionDisallowed());
			model.setJobClass(jobDetail.getJobClass().getName());
	}

	@WriteOperation
	public boolean modifyJobStatus(@Selector String group, @Selector String name, @Selector String state)
			throws SchedulerException {
		JobKey jobKey = new JobKey(name, group);
		JobDetail detail = scheduler.getJobDetail(jobKey);
		if (detail == null)
			return false;
		else if (QuartzState.PAUSE.equals(state)) {
			scheduler.pauseJob(jobKey);
		} else if (QuartzState.RESUME.equals(state)) {
			scheduler.resumeJob(jobKey);
		} else {
			throw new UnsupportStateChangeException(String.format("unsupported state change. state:[%s]", state));
		}
		return true;
	}

	@WriteOperation
	public boolean modifyJobsStatus(@Selector String group, @Selector String state) throws SchedulerException{
		GroupMatcher<JobKey> jobGroupMatcher = GroupMatcher.jobGroupEquals(group);
		Set<JobKey> jobKeys = scheduler.getJobKeys(jobGroupMatcher);
		if (jobKeys == null || jobKeys.isEmpty()) {
			return false;
		}
		else if (QuartzState.PAUSE.equals(state)) {
			scheduler.pauseJobs(jobGroupMatcher);
		} else if (QuartzState.RESUME.equals(state)) {
			scheduler.resumeJobs(jobGroupMatcher);
		} else {
			throw new UnsupportStateChangeException(String.format("unsupported state change. state:[%s]", state));
		}
		return true;
	}

}
