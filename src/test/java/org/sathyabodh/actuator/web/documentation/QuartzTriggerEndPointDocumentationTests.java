package org.sathyabodh.actuator.web.documentation;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.sathyabodh.actuator.quartz.QuartzTriggerEndPoint;
import org.sathyabodh.actuator.quartz.QuartzTriggerEndPointWebExtension;
import org.sathyabodh.actuator.web.documentation.QuartzJobEndPointDocumentationTests.MockNonConcurrentJob;
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
public class QuartzTriggerEndPointDocumentationTests extends AbstractQuartzEndPointDocumentationTests {

	@Test
	public void testTriggerListing() throws Exception {

		JobDetail jobDetail = JobBuilder.newJob(MockNonConcurrentJob.class)
				.withIdentity(new JobKey("myjob", "myjobGroup")).storeDurably().build();
		Trigger trigger1 = TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity("trigger_1", "trigger_group1")
				.startAt(new Date()).build();
		Mockito.when(scheduler.getTrigger(Mockito.any(TriggerKey.class))).thenReturn(trigger1);
		Mockito.when(scheduler.getTriggerState(Mockito.any(TriggerKey.class))).thenReturn(TriggerState.NORMAL);

		this.mockMvc.perform(MockMvcRequestBuilders.get("/actuator/quartz-triggers?group=myjobgroup&name=myjob_"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcRestDocumentation.document("quartz-triggers/list",
						requestParameters(parameterWithName("group").description("Name of the trigger").optional(),
								parameterWithName("name").description("Name of the trigger").optional()),
						responseFields(fieldWithPath("groups").description("Trigger group keyed by group name"),
								fieldWithPath("groups.*.[].name").description("Name of the trigger"),
								fieldWithPath("groups.*.[].nextFireTime").description("Next fire time of the trigger"),
								fieldWithPath("groups.*.[].previousFireTime")
										.description("Previous fire time of the trigger"),
								fieldWithPath("groups.*.[].startTime").description("Start time of the trigger"),
								fieldWithPath("groups.*.[].endTime").description("End time of the trigger"),
								fieldWithPath("groups.*.[].group").description("Trigger's group"),
								fieldWithPath("groups.*.[].state").description("State of trigger ex:PAUSED,NORMAL"),
								fieldWithPath("groups.*.[].jobKey").description("Associated Job key of the trigger"))));
	}

	@Test
	public void testTriggerPause() throws Exception {
		JobDetail jobDetail = JobBuilder.newJob(MockNonConcurrentJob.class)
				.withIdentity(new JobKey("myjob", "myjobGroup")).storeDurably().build();
		Trigger trigger1 = TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity("trigger_1", "trigger_group1")
				.startAt(new Date()).build();
		Mockito.when(scheduler.getTrigger(Mockito.any(TriggerKey.class))).thenReturn(trigger1);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/actuator/quartz-triggers/mytriggergroup/mytrigger/pause"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcRestDocumentation.document("quartz-triggers/named/trigger/pause"));

	}

	@Test
	public void testTriggerResume() throws Exception {
		JobDetail jobDetail = JobBuilder.newJob(MockNonConcurrentJob.class)
				.withIdentity(new JobKey("myjob", "myjobGroup")).storeDurably().build();
		Trigger trigger1 = TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity("trigger_1", "trigger_group1")
				.startAt(new Date()).build();
		Mockito.when(scheduler.getTrigger(Mockito.any(TriggerKey.class))).thenReturn(trigger1);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/actuator/quartz-triggers/mytriggergroup/mytrigger/resume"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcRestDocumentation.document("quartz-triggers/named/trigger/resume"));
	}

	@Configuration
	@Import(BaseDocumentationConfiguration.class)
	static class QuartzTriggerEndPointConfiguration {
		@Bean
		public QuartzTriggerEndPoint quartzTriggerEndPoint(Scheduler scheduler) {
			return new QuartzTriggerEndPoint(scheduler);
		}

		@Bean
		QuartzTriggerEndPointWebExtension quartzTriggerEndPointWebExtension(
				QuartzTriggerEndPoint quartzTriggerEndPoint) {
			return new QuartzTriggerEndPointWebExtension(quartzTriggerEndPoint);
		}
	}
}
