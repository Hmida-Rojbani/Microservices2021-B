package de.tekup.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.tekup.user.data.models.UserEntity;
import de.tekup.user.data.repos.AlbumsServiceClient;
import de.tekup.user.data.repos.UserRepository;
import de.tekup.user.ui.models.AlbumResponseModel;
import de.tekup.user.ui.models.UserDTO;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService{
	
	private ModelMapper mapper;
	private UserRepository repository;
	private AlbumsServiceClient  albumsServiceClient;
	
	public UserDTO saveUserToDB(UserDTO userDTO) {
		UserEntity entity = mapper.map(userDTO, UserEntity.class);
		
		entity.setUserId(UUID.randomUUID().toString());
		UserEntity entitySaved= repository.save(entity);
		
		return mapper.map(entitySaved, UserDTO.class);
	}
	
	public UserDTO findUserByUsername(String email) {
		UserEntity user = repository.findByEmail(email)
								.orElseThrow(()->new UsernameNotFoundException("Username not found"));
		return mapper.map(user, UserDTO.class);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDTO dto = findUserByUsername(username);
		return new User(dto.getEmail(), dto.getPassword(), true, true, true, true, new ArrayList<>());
	}
	
	public UserDTO findUserByUserId(String userId) {
		UserEntity user = repository.findByUserId(userId)
								.orElseThrow(()->new UsernameNotFoundException("Username not found"));
		UserDTO userDto =  mapper.map(user, UserDTO.class);
		
		// call the Album service
		List<AlbumResponseModel> albumList = albumsServiceClient.getAlbums(userId);
		userDto.setAlbums(albumList);
		return userDto;
	}

}
