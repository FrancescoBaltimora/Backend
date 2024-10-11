package com.triptravel.backend.users.mappers;

import org.mapstruct.Mapper;

import com.triptravel.backend.users.dtos.UserSearchDTO;
import com.triptravel.backend.users.models.User;

@Mapper
public interface UserSearchMapper {

	UserSearchDTO toDto(User user);
	User toEntity(UserSearchDTO user);
}
