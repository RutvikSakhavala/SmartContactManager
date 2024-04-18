package com.smartContactManager.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smartContactManager.entities.Contact;
import com.smartContactManager.entities.User;
import com.smartContactManager.repositories.ContactRepository;
import com.smartContactManager.repositories.UserRepository;

@RestController
public class SearchController {
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	@GetMapping("/user/search/{query}")		
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal){
		User user = userRepository.getUserByUserName(principal.getName());
		
		List<Contact> contacts = contactRepository.findByFirstNameContainingAndUserOrSecondNameContainingAndUser(query,user,query,user);
		
		return ResponseEntity.ok(contacts);
	}
}
