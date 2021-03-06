package com.contact.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.contact.dao.UserRepo;
import com.contact.entites.User;

public class UserDetailService implements UserDetailsService{

	//feching userfrom database 
	
	@Autowired
	private UserRepo userRespo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRespo.getUserByUserName(username);
		if(user==null) {
			throw new UsernameNotFoundException("Could not found user !!!!");
		}
		
		CustomerDetails customerDetail = new CustomerDetails(user);
		return customerDetail;
	}

}
