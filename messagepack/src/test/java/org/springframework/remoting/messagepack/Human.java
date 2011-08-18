package org.springframework.remoting.messagepack;

/**
 *
 */
public class Human {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Human() {
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Human human = (Human) o;

		if (name != null ? !name.equals(human.name) : human.name != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

	public Human(String name) {
		this.name = name;
	}
}
