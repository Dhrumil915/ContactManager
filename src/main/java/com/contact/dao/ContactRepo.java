package com.contact.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.entites.Contact;
import com.contact.entites.User;


public interface ContactRepo extends JpaRepository<Contact, Integer> {

	//pagination
	//current page
	//contact per page -5
	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactsByUser(@Param("userId")int userId, Pageable pageable);
	
	//search
	public List<Contact> findByNameContainingAndUser(String name,User user);
}
