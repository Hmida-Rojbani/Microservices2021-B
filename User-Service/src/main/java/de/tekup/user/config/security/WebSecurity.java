package de.tekup.user.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import de.tekup.user.service.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurity extends WebSecurityConfigurerAdapter{
	
	private Environment env;
	private UserService userService;
	private BCryptPasswordEncoder bcrypt;
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		// accept all request
		http.authorizeRequests().antMatchers("/api/users/register").permitAll();
		// accept only the gateway requests 
		http.authorizeRequests().antMatchers("/**")
								.hasIpAddress(env.getProperty("gateway.ip"))
								.and()
								.addFilter(getAuthenticationfilter());
		http.headers().frameOptions().disable();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService)
			.passwordEncoder(bcrypt);
	}
	
	private Authenticationfilter getAuthenticationfilter() throws Exception{
		Authenticationfilter authenticationfilter = new Authenticationfilter(env, userService, authenticationManager());
		authenticationfilter.setFilterProcessesUrl(env.getProperty("login.url.path"));
		return authenticationfilter;
	}
	
	

}
