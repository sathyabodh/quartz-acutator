package org.sathyabodh.actuator.quartz;

import org.quartz.SchedulerException;
import org.sathyabodh.actuator.quartz.exception.UnsupportStateChangeException;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.annotation.EndpointWebExtension;

@EndpointWebExtension(endpoint=QuartzTriggerEndPoint.class)
public class QuartzTriggerEndPointWebExtension {

	private QuartzTriggerEndPoint qurtzTriggerEndPoint ;

	public QuartzTriggerEndPointWebExtension(QuartzTriggerEndPoint qurtzTriggerEndPoint){
		this.qurtzTriggerEndPoint = qurtzTriggerEndPoint;
	}

	@WriteOperation
	public WebEndpointResponse<?> modifyTriggerStatus(@Selector String group, @Selector String name, @Selector String state) throws SchedulerException {
		try{
			boolean isSucess = qurtzTriggerEndPoint.modifyTriggerStatus(group, name, state);
			int status = isSucess ? WebEndpointResponse.STATUS_OK : WebEndpointResponse.STATUS_NOT_FOUND;
			return new WebEndpointResponse<>(status);
		}catch(UnsupportStateChangeException e){
			return new WebEndpointResponse<>(WebEndpointResponse.STATUS_BAD_REQUEST);
		}
	}
	@WriteOperation
	public WebEndpointResponse<?> modifyTriggersStatus(@Selector String group,@Selector String state) throws SchedulerException {
		try{
			boolean isSucess = qurtzTriggerEndPoint.modifyTriggersStatus(group, state);
			int status = isSucess ? WebEndpointResponse.STATUS_OK : WebEndpointResponse.STATUS_NOT_FOUND;
			return new WebEndpointResponse<>(status);
		}catch(UnsupportStateChangeException e){
			return new WebEndpointResponse<>(WebEndpointResponse.STATUS_BAD_REQUEST);
		}
	}

}
