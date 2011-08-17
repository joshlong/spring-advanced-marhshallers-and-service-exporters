package org.springframework.remoting.messagepack;

import org.springframework.core.style.ToStringCreator;

import java.util.HashSet;
import java.util.Set;

/**
 * Dummy entity with state to test RPC withc complex objects
 */
public class Cat {
	private String name ;
	private int age ;
	private Set <Cat> friends = new HashSet <Cat> ();

	public Cat getBrother() {
		return brother;
	}

	public void setBrother(Cat brother) {
		this.brother = brother;
	}

	private Cat brother ;

	public Set<Cat> getFriends() {
		return friends;
	}

	public void setFriends(Set<Cat> friends) {
		this.friends = friends;
	}

	public void addFriend(Cat c){
		this.friends .add(c) ;
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

	public String toString(){
		return new ToStringCreator(this)
				       .append("friends", this.getFriends())
				       .append("name", this.getName())
				       .append("age", this.getAge())
				       .toString();
	}
}
