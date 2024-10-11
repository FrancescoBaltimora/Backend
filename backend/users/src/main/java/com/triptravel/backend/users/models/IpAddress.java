package com.triptravel.backend.users.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ipAddresses")
public class IpAddress {

	@Id
	String ipAddress;
	boolean blocked;
	Long lastRequest;
}
