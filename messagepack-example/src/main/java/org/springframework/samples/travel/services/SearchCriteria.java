package org.springframework.samples.travel.services;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * A backing bean for the main hotel search form. Encapsulates the criteria
 * needed to perform a hotel search.
 * <p/>
 * This object is annotated with JAXB 2 annotations so that it may be marshalled using the Spring OXM JAXB2 {@link org.springframework.oxm.jaxb.Jaxb2Marshaller}
 * which is used in our RESTful {@link org.springframework.samples.travel.rest.HotelsRestController}.
 */
@XmlRootElement
public class SearchCriteria implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	public double getMaximumPrice() {
		return maximumPrice;
	}

	public void setMaximumPrice(double maximumPrice) {
		this.maximumPrice = maximumPrice;
	}

	private double maximumPrice;

	/**
	 * The user-provided search criteria for finding Hotels.
	 */
	private String searchString;

	/**
	 * The maximum page size of the Hotel result list
	 */
	private int pageSize;

	/**
	 * The current page of the Hotel result list.
	 */
	private int page;

	@XmlAttribute
	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	@XmlAttribute
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@XmlAttribute
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
}
