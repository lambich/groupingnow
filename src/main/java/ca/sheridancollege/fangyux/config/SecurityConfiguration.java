package ca.sheridancollege.fangyux.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.util.UrlPathHelper;

import ca.sheridancollege.fangyux.config.oauth.*;
import ca.sheridancollege.fangyux.service.StudentUserDetailsService;
import ca.sheridancollege.fangyux.service.UserService;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserService userService;
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new StudentUserDetailsService();
	}
	
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }
	
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers(HttpMethod.GET, "/").permitAll()
		.antMatchers(HttpMethod.GET, "/registration**").permitAll()
		.antMatchers(HttpMethod.GET, "//js/**").permitAll()
		.antMatchers(HttpMethod.GET, "/css/**").permitAll()
		.antMatchers(HttpMethod.GET, "/images/**").permitAll()
		.antMatchers(HttpMethod.GET, "//user/**").hasRole("USER")
		.antMatchers(HttpMethod.GET, "/cart").hasRole("USER")
		.antMatchers(HttpMethod.GET, "/addGroup").hasRole("USER")
		.antMatchers(HttpMethod.GET, "/addEvent").hasRole("USER")
		.antMatchers(HttpMethod.POST, "/addGroup").hasRole("USER")
		.antMatchers(HttpMethod.POST, "/addEvent").hasRole("USER")
		.anyRequest().authenticated() 
		.and()
		.formLogin()
		.loginPage("/login")
		.usernameParameter("email")
		.permitAll()
		.defaultSuccessUrl("/")
		.successHandler(customAuthenticationSuccessHandler)
		.and()
		.logout()
		.invalidateHttpSession(true)
		.clearAuthentication(true)
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//		.logoutSuccessUrl("/login?logout")
		.logoutSuccessUrl("/")
		.permitAll();
		
		http.csrf().disable();
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception{
		web.ignoring().antMatchers("/images/**,/js/**");
	}
	
	@Autowired
	private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

}
