package com.example.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("sample")
public class SampleResource {

	@GET
	@Path("hoge")
	public String hoge() {
		return "Hoge Resource";
	}
}
