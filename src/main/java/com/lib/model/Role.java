package com.lib.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ADMIN, TEACHER, STUDENT, LIBRARIAN;
	@JsonCreator
	public static Role fromValue(String value) {
	    for (Role role : Role.values()) {
	        if (role.name().equalsIgnoreCase(value)) {
	            return role;
	        }
	    }
	    throw new IllegalArgumentException("Invalid role: " + value);
	}

}