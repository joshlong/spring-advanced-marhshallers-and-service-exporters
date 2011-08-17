package org.springframework.remoting.messagepack;

import org.springframework.core.style.ToStringCreator;

import java.util.HashSet;
import java.util.Set;

/**
 * Dummy entity with state to test RPC withc complex objects
 */
public class Cat {

	private Set<Human> humans = new HashSet<Human>();
	public Set<Cat> friends = new HashSet<Cat>();
	public String name;
	public int age;

	public void addHuman(Human human) {
		this.humans.add(human);
	}

	public Set<Human> getHumans() {
		return humans;
	}

	public void setHumans(Set<Human> humans) {
		this.humans = humans;
	}

	public Set<Cat> getFriends() {
		return friends;
	}

	public void setFriends(Set<Cat> friends) {
		this.friends = friends;
	}

	public void addFriend(Cat c) {
		this.friends.add(c);
	}

	public Cat(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public Cat() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String toString() {
		return new ToStringCreator(this)
				       .append("friends", this.getFriends())
				       .append("name", this.getName())
				       .append("age", this.getAge())
				       .append("humans", this.getHumans())
				       .toString();
	}
}
