package com.triptravel.backend.users.mappers;

import org.mapstruct.Mapper;

import com.triptravel.backend.users.dtos.UserDTO;
import com.triptravel.backend.users.models.User;

@Mapper
public interface UserMapper {

	UserDTO toDto(User user);
	User toEntity(UserDTO userDto);
}
