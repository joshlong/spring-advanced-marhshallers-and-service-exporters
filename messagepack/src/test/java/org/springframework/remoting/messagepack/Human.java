package org.springframework.remoting.messagepack;

/**
 *
 */
public class Human {
	private String name ;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public Human() {
	}

	public Human(String name) {
		this.name = name;
	}
}
