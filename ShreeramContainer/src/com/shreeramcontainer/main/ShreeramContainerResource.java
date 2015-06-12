package com.shreeramcontainer.main;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path("/")
@Component
public class ShreeramContainerResource {

	@Autowired
	EmailService service;
	
	private static final Logger LOGGER = Logger.getLogger(ShreeramContainerResource.class.getName());

	@POST
	@Path("/contactus")
	public Response getMsg(@FormParam("cnumber")String contactNumber,
			@FormParam("cmessage")String contactMessage) {

		if(contactNumber==null || contactNumber.isEmpty()){
			return Response.status(404).build();
		}
		//TODO: Validate Inputs

		try{
			service.sendContactEmail(contactNumber, contactMessage);
		}catch(Exception e){
			LOGGER.log(Level.SEVERE,"Error while sending emails",e);
		}
		URI uri=null;
		try{
			uri = new URI("../");
		}catch(Exception e){
			LOGGER.log(Level.SEVERE,"Error generating home page URI",e);
		}

		return Response.seeOther(uri).build();
	}

}