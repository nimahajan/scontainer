package com.shreeramcontainer.main;

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

		try{
			service.sendContactEmail(contactNumber, contactMessage);
		}catch(Exception e){
			LOGGER.log(Level.SEVERE,"Error while submitting details",e);
			return Response.status(500).entity("Submitting details failed").build();
		}

		//return Response.seeOther(uri).build();
		return Response.status(201).entity("Contact details submitted successfully.").build();
	}

}