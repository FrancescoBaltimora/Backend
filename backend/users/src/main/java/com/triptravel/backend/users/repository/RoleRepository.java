package com.triptravel.backend.users.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.triptravel.backend.users.models.Role;

@Repository
public interface RoleRepository extends MongoRepository<Role, Integer>{
	Role findByName(String name);
}
