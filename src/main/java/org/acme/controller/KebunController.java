package org.acme.controller;

import io.vertx.core.json.JsonObject;
import org.acme.service.GenerateDocument;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api")
public class KebunController {

    @Inject
    GenerateDocument generateDocument;

//    @POST
//    @Consumes(value = MediaType.APPLICATION_JSON)
//    @Produces(value = MediaType.APPLICATION_JSON)
//    public Response post(JsonObject payload){
//        JsonObject result = generateDocument.generateDocument(payload);
//        return Response.ok().entity(payload).build();
//    }
}
