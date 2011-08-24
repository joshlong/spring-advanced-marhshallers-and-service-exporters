package org.springframework.samples.crm.rest;

import org.springframework.http.converter.thrift.ThriftHttpMessageConverter;
import org.springframework.samples.crm.services.CrmService;
import org.springframework.stereotype.Controller;
import org.springframework.thrift.crm.Customer;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@Controller
@RequestMapping(value = "/ws/", headers = CrmRestController.acceptHeader)
public class CrmRestController {

	static public final String acceptHeader = "Accept=application/json, application/xml, " + ThriftHttpMessageConverter.MEDIA_TYPE_STRING ;

	@Inject
	private CrmService crmService ;

	@RequestMapping(value = "/customers/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Customer customer( @PathVariable("id") int  id){
		return crmService.getCustomerById( id);
	}

	@RequestMapping(value = "/customers", method = RequestMethod.POST)
	@ResponseBody
	public Customer login(@RequestBody Customer  c) {
		 return crmService.createCustomer(c.getFirstName(),c.getLastName(),c.getEmail(),c.getId());
	}

}
