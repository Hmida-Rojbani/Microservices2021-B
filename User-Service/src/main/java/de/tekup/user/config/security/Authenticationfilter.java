package de.tekup.user.config.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.tekup.user.service.UserService;
import de.tekup.user.ui.models.LoginRequestModel;
import de.tekup.user.ui.models.UserDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class Authenticationfilter extends UsernamePasswordAuthenticationFilter {

	private Environment env;
	private UserService userService;

	public Authenticationfilter(Environment env, UserService userService, AuthenticationManager authenticationManager) {
		super();
		this.env = env;
		this.userService = userService;
		super.setAuthenticationManager(authenticationManager);
	}  

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		try {
			LoginRequestModel creds = new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);

			return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(creds.getEmail(),
					creds.getPassword(), new ArrayList<>()));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		String email = ((User) auth.getPrincipal()).getUsername();
		UserDTO userDetails = userService.findUserByUsername(email);
		
		String token = Jwts.builder()
						.setSubject(userDetails.getUserID())
						.setExpiration(new Date(System.currentTimeMillis()+ Long.parseLong(env.getProperty("token.expiration_time"))))
						.signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
						.compact();
		response.addHeader("token", token);
		response.addHeader("userId", userDetails.getUserID());
	
	}
	
	
	

}
