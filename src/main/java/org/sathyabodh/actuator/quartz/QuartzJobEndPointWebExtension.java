package org.sathyabodh.actuator.quartz;

import org.quartz.SchedulerException;
import org.sathyabodh.actuator.quartz.exception.UnsupportStateChangeException;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.annotation.EndpointWebExtension;

@EndpointWebExtension(endpoint=QuartzJobEndPoint.class)
public class QuartzJobEndPointWebExtension {

	private QuartzJobEndPoint qurtzJobEndPoint ;

	public QuartzJobEndPointWebExtension(QuartzJobEndPoint qurtzJobEndPoint){
		this.qurtzJobEndPoint = qurtzJobEndPoint;
	}

	@WriteOperation
	public WebEndpointResponse<?> modifyJobStatus(@Selector String group, @Selector String name, @Selector String state) throws SchedulerException {
		try{
			boolean isSucess = qurtzJobEndPoint.modifyJobStatus(group, name, state);
			int status = isSucess ? WebEndpointResponse.STATUS_OK : WebEndpointResponse.STATUS_NOT_FOUND;
			return new WebEndpointResponse<>(status);
		}catch(UnsupportStateChangeException e){
			return new WebEndpointResponse<>(WebEndpointResponse.STATUS_BAD_REQUEST);
		}
	}
	@WriteOperation
	public WebEndpointResponse<?> modifyJobsStatus(@Selector String group,@Selector String state) throws SchedulerException {
		try{
			boolean isSucess = qurtzJobEndPoint.modifyJobsStatus(group, state);
			int status = isSucess ? WebEndpointResponse.STATUS_OK : WebEndpointResponse.STATUS_NOT_FOUND;
			return new WebEndpointResponse<>(status);
		}catch(UnsupportStateChangeException e){
			return new WebEndpointResponse<>(WebEndpointResponse.STATUS_BAD_REQUEST);
		}
	}
}
