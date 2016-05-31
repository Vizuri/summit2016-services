package com.redhat.vizuri.insurance;

import java.io.Serializable;

public class Customer implements Serializable {
	private static final long serialVersionUID = 4025370813288444322L;

	private Long id;
	private String name;
	private Integer age;
	private PolicyLevel policyLevel;
	
	public Customer() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public PolicyLevel getPolicyLevel() {
		return policyLevel;
	}

	public void setPolicyLevel(PolicyLevel policyLevel) {
		this.policyLevel = policyLevel;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", name=" + name + ", age=" + age + ", policyLevel=" + policyLevel + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
