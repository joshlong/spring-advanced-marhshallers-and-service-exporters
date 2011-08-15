package org.springframework.samples.travel.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * A user who can book hotels.
 */
@XmlRootElement
@Entity
@Table(name = "customers")
public class User implements Serializable {

	private String username;

	// important for consideration in Spring Security
	private boolean enabled;
	private String email ;
	private String password;
	private String name;

	@XmlAttribute
	@Column
	public boolean isEnabled() {
		return enabled;
	}

	@XmlAttribute
	@Column
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@XmlAttribute
	@Column
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User() {
	}

	public User(String username, String password, String name) {
		this.username = username;
		this.password = password;
		this.name = name;
	}

	@Id
	@XmlAttribute
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	@Override
	public String toString() {
		return "User(" + username + ")";
	}
}
