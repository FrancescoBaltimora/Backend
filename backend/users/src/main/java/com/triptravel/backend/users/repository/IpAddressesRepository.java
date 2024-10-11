package com.triptravel.backend.users.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.triptravel.backend.users.models.IpAddress;

@Repository
public interface IpAddressesRepository extends MongoRepository<IpAddress, String>{
	IpAddress findByIpAddress(String ip);
}
