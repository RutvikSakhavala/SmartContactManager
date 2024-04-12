package com.smartContactManager.controllers;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartContactManager.entities.Contact;
import com.smartContactManager.entities.User;
import com.smartContactManager.helper.Message;
import com.smartContactManager.repositories.ContactRepository;
import com.smartContactManager.repositories.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserRepository userRepository;
	@Autowired
	ContactRepository contactRepository;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@ModelAttribute
	public void commonData(Model model, Principal principal) {
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
	}

	@GetMapping("/index")
	public String home() {
		return "/user/index";
	}

	@GetMapping("/addContact")
	public String addContact(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "/user/addContact";
	}

	@PostMapping("/addContactData")
	public String addContactData(@Valid @ModelAttribute("contact") Contact contact, BindingResult result, Model model,
			Principal principal, @RequestParam("img") MultipartFile file, HttpSession session) {
		try {

			if (result.hasErrors())
			{
				throw new Exception();
			}
			else
			{
				if(file.isEmpty())
				{
					contact.setImage("userid.png");
				}
				else {
					System.out.println(file.getOriginalFilename());
					File saveFile = new ClassPathResource("static/images").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					contact.setImage(file.getOriginalFilename());
				}

				System.out.println(contact);
				String userName = principal.getName();
				User user = userRepository.getUserByUserName(userName);

				contact.setUser(user);
				user.getContacts().add(contact);
				userRepository.save(user);

			    session.setAttribute("message", "Contact added successfully");

				model.addAttribute("contact", new Contact());

				return "/user/addContact";
			}
		}
		catch (Exception e)
		{
			List<ObjectError> allErrors = result.getAllErrors();
			for (ObjectError objectError : allErrors) {
				System.out.println(objectError);
			}
			System.out.println("error");
			return "/user/addContact";
		}
	}

	@GetMapping("/viewContacts/{page}")
	public String viewContacts(@PathVariable("page") int page, Model model,Principal principal)
	{

		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		Pageable pageable = PageRequest.of(page, 10);
	    Page<Contact> contacts = contactRepository.findContactsByUser(user.getId(), pageable);
	    if(contacts.getTotalPages()==0)
	    {
	    	model.addAttribute("msg", "No Contacts!!");
	    }
	    	    
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
	    

		return "/user/viewContacts";
	}

	@RequestMapping("/contact/{cId}")
	public String contactDetail(@PathVariable("cId") Integer cId,Model model)
	{
		Optional<Contact> optionalContact = contactRepository.findById(cId);
		Contact contact = optionalContact.get();
		model.addAttribute("contact", contact);
		return "/user/contact";
	}

	@GetMapping("/profile")
	public String profile()
	{
		return "/user/profile";
	}

	@GetMapping("/setting")
	public String setting()
	{
		return "/user/setting";
	}

	@PostMapping("/changePasswd")
	public String changePasswd(@RequestParam("oldPasswd")String oldPasswd,@RequestParam("newPasswd")String newPasswd,Principal principal,HttpSession session) {

		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);

		if(bCryptPasswordEncoder.matches(oldPasswd, user.getPassword()))
		{
			user.setPassword(bCryptPasswordEncoder.encode(newPasswd));
			user.setAgreement(true);
		    userRepository.save(user);
		    session.setAttribute("message", new Message("Password Change successfully","success"));
//
//
		}else {
			session.setAttribute("message", new Message("Invalid Credentials","danger"));
			return "redirect:/user/setting";
		}


		return "redirect:/user/setting";
	}
}
