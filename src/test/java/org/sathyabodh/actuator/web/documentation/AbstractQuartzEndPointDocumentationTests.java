package org.sathyabodh.actuator.web.documentation;

import org.junit.Before;
import org.junit.Rule;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.reactive.WebFluxEndpointManagementContextConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.servlet.WebMvcEndpointManagementContextConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public class AbstractQuartzEndPointDocumentationTests {
	@Rule
	public JUnitRestDocumentation documentation = new JUnitRestDocumentation("target/generated-snippets");
	@Autowired
	protected WebApplicationContext context;

	protected MockMvc mockMvc;

	@MockBean
	protected Scheduler scheduler;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(MockMvcRestDocumentation.documentationConfiguration(this.documentation)).build();
	}

	@Configuration
	@ImportAutoConfiguration({ JacksonAutoConfiguration.class,
		HttpMessageConvertersAutoConfiguration.class, WebMvcAutoConfiguration.class,
		DispatcherServletAutoConfiguration.class, EndpointAutoConfiguration.class,
		WebEndpointAutoConfiguration.class,
		WebMvcEndpointManagementContextConfiguration.class,
		WebFluxEndpointManagementContextConfiguration.class,
		PropertyPlaceholderAutoConfiguration.class, WebFluxAutoConfiguration.class,
		HttpHandlerAutoConfiguration.class })
static class BaseDocumentationConfiguration {
}

}
