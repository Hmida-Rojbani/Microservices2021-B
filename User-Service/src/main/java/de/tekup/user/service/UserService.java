package de.tekup.user.service;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import de.tekup.user.data.models.UserEntity;
import de.tekup.user.data.repos.UserRepository;
import de.tekup.user.ui.models.UserDTO;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
	
	private ModelMapper mapper;
	private UserRepository repository;
	
	public UserDTO saveUserToDB(UserDTO userDTO) {
		UserEntity entity = mapper.map(userDTO, UserEntity.class);
		
		entity.setUserId(UUID.randomUUID().toString());
		UserEntity entitySaved= repository.save(entity);
		
		return mapper.map(entitySaved, UserDTO.class);
	}

}
