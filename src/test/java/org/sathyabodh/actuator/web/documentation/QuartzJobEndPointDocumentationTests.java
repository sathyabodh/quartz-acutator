package org.sathyabodh.actuator.web.documentation;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.quartz.CronScheduleBuilder;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.sathyabodh.actuator.quartz.QuartzJobEndPoint;
import org.sathyabodh.actuator.quartz.QuartzJobEndPointWebExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties={
		"management.endpoints.web.exposure.include=*",
		"management.endpoints.web.basePath=/actuator"
})
public class QuartzJobEndPointDocumentationTests extends AbstractQuartzEndPointDocumentationTests {

	@Test
	public void testQuartzJobPause() throws Exception {
		JobKey jobKey = new JobKey("myjob", "myjobgroup");
		JobDetail jobDetail = JobBuilder.newJob(Job.class).withIdentity(jobKey).build();
		Mockito.when(scheduler.getJobDetail(jobKey)).thenReturn(jobDetail);
		Mockito.doNothing().when(scheduler).pauseJob(jobKey);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/actuator/quartz-jobs/myjobgroup/myjob/pause"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcRestDocumentation.document("quartz-jobs/named/job/pause"));
	}

	@Test
	public void testQuartzJobResume() throws Exception {
		JobKey jobKey = new JobKey("myjob", "myjobgroup");
		JobDetail jobDetail = JobBuilder.newJob(Job.class).withIdentity(jobKey).build();
		Mockito.when(scheduler.getJobDetail(jobKey)).thenReturn(jobDetail);
		Mockito.doNothing().when(scheduler).pauseJob(jobKey);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/actuator/quartz-jobs/myjobgroup/myjob/resume"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcRestDocumentation.document("quartz-jobs/named/job/resume"));
	}

	@Test
	public void testQuartzJobGroupPause() throws Exception {
		JobKey jobKey = new JobKey("myjob", "myjobgroup");
		Set<JobKey> jobKeys = new HashSet<>();
		jobKeys.add(jobKey);
		GroupMatcher<JobKey> jobGroupMatcher = GroupMatcher.jobGroupEquals("myjobgroup");
		Mockito.when(scheduler.getJobKeys(jobGroupMatcher)).thenReturn(jobKeys);
		Mockito.doNothing().when(scheduler).pauseJobs(jobGroupMatcher);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/actuator/quartz-jobs/myjobgroup/pause"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcRestDocumentation.document("quartz-jobs/named/jobgroup/pause"));
	}

	@Test
	public void testQuartzJobGroupResume() throws Exception {
		JobKey jobKey = new JobKey("myjob", "myjobgroup");
		Set<JobKey> jobKeys = new HashSet<>();
		jobKeys.add(jobKey);
		GroupMatcher<JobKey> jobGroupMatcher = GroupMatcher.jobGroupEquals("myjobgroup");
		Mockito.when(scheduler.getJobKeys(jobGroupMatcher)).thenReturn(jobKeys);
		Mockito.doNothing().when(scheduler).pauseJobs(jobGroupMatcher);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/actuator/quartz-jobs/myjobgroup/resume"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcRestDocumentation.document("quartz-jobs/named/jobgroup/resume"));
	}

	@Test
	public void testQuartzJobList() throws Exception {
		int i = 1;
		Set<JobKey> jobKeys = new HashSet<>();
		List<JobDetail> jobDetails = new ArrayList<>();
		while (i <= 3) {
			JobKey jobKey = new JobKey("myjob_" + i, "myjobgroup");
			JobDetail jobDetail = JobBuilder.newJob(MockNonConcurrentJob.class).withIdentity(jobKey).storeDurably()
					.build();
			++i;
			jobKeys.add(jobKey);
			jobDetails.add(jobDetail);
		}

		GroupMatcher<JobKey> jobGroupMatcher = GroupMatcher.jobGroupEquals("myjobgroup");
		Mockito.when(scheduler.getJobKeys(jobGroupMatcher)).thenReturn(jobKeys);

		Mockito.when(scheduler.getJobDetail(Mockito.any(JobKey.class))).thenReturn(jobDetails.get(0));
		this.mockMvc.perform(MockMvcRequestBuilders.get("/actuator/quartz-jobs?group=myjobgroup&name=myjob_1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcRestDocumentation.document("quartz-jobs/list",
						requestParameters(parameterWithName("group").description("Name of the job group").optional(),
								parameterWithName("name").description("Name of the job").optional()),
						responseFields(fieldWithPath("groups").description("Job group keyed by group name"),
								fieldWithPath("groups.*.[].name").description("Name of the job"),
								fieldWithPath("groups.*.[].concurrentDisallowed")
										.description("Indicates concurrent execution of job is allowed"),
								fieldWithPath("groups.*.[].durable").description("Indicates job is durable"),
								fieldWithPath("groups.*.[].jobClass").description("Class name of the job"))));
	}

	@Test
	public void testGetJobDetails() throws Exception {
		JobKey jobKey = new JobKey("myjob", "myjobgroup");
		JobDetail jobDetail = JobBuilder.newJob(MockNonConcurrentJob.class).withIdentity(jobKey).storeDurably().build();

		Trigger trigger1 = TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity("trigger_1", "trigger_group1")
				.startAt(new Date()).build();

		ScheduleBuilder<?> builder = CronScheduleBuilder.cronSchedule("0 0 23 * * ?")
				.withMisfireHandlingInstructionFireAndProceed();
		Trigger trigger2 = TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity("trigger_2", "trigger_group1")
				.withSchedule(builder).build();

		List triggers = new ArrayList();
		triggers.add(trigger1);
		triggers.add(trigger2);

		Mockito.when(scheduler.getJobDetail(Mockito.any(JobKey.class))).thenReturn(jobDetail);
		Mockito.when(scheduler.getTriggersOfJob(Mockito.any(JobKey.class))).thenReturn(triggers);
		Mockito.when(scheduler.getTriggerState(Mockito.any(TriggerKey.class))).thenReturn(TriggerState.NORMAL);

		this.mockMvc.perform(MockMvcRequestBuilders.get("/actuator/quartz-jobs/myjobgroup/myjob"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcRestDocumentation.document("quartz-jobs/named/jobdetails",
						responseFields(fieldWithPath("name").description("Name of the job"),
								fieldWithPath("group").description("Group name of the job"),
								fieldWithPath("concurrentDisallowed")
										.description("Indicates concurrent execution of job is allowed"),
								fieldWithPath("durable").description("Indicates job is durable"),
								fieldWithPath("jobClass").description("Class name of the job"),
								fieldWithPath("triggers").description("Triggers associated with job"),
								fieldWithPath("triggers.[].name").description("Name of the trigger"),
								fieldWithPath("triggers.[].nextFireTime").description("Next fire time of the trigger"),
								fieldWithPath("triggers.[].previousFireTime")
										.description("Previous fire time of the trigger"),
								fieldWithPath("triggers.[].startTime").description("Start time of the trigger"),
								fieldWithPath("triggers.[].endTime").description("End time of the trigger"),
								fieldWithPath("triggers.[].group").description("Trigger's group"),
								fieldWithPath("triggers.[].state").description("State of trigger ex:PAUSED,NORMAL"),
								fieldWithPath("triggers.[].jobKey").description("Associated Job key of the trigger"))));
	}

	@DisallowConcurrentExecution
	class MockNonConcurrentJob implements Job {

		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
		}
	}

	@Configuration
	@Import(BaseDocumentationConfiguration.class)
	static class QuartzJobEndPointConfiguration {
		@Bean
		public QuartzJobEndPoint quartzJobEndPoint(Scheduler scheduler) {
			return new QuartzJobEndPoint(scheduler);
		}

		@Bean
		public QuartzJobEndPointWebExtension quartzJobEndPointWebExtension(QuartzJobEndPoint quartzJobEndPoint) {
			return new QuartzJobEndPointWebExtension(quartzJobEndPoint);
		}

	}
}
