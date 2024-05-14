package com.smartContactManager.controllers;



import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartContactManager.entities.Contact;
import com.smartContactManager.entities.User;
import com.smartContactManager.repositories.ContactRepository;
import com.smartContactManager.repositories.UserRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/contact")
public class ContactController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	ContactRepository contactRepository;

	@RequestMapping("/deleteContact/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId,Principal principal) {

			String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);
			Optional<Contact> byIdContact = contactRepository.findById(cId);
				Contact contact = byIdContact.get();
			boolean remove = user.getContacts().remove(contact);


			userRepository.save(user);

			System.out.println(remove);
		return "redirect:/user/viewContacts/0";
	}

	@PostMapping("/updateContact/{cId}")
	public String updateContact(@PathVariable("cId") Integer cId,Model model)
	{
		model.addAttribute("title","Update Contact");
		model.addAttribute("cId", cId);
		Contact contact = contactRepository.findById(cId).get();

		model.addAttribute("contact", contact);
		return "user/updateContact";
	}

	@PostMapping("/updateContactData")
	public String updateContactData(@Valid @ModelAttribute("contact")Contact con,BindingResult result,@RequestParam("img")MultipartFile file,Model model,Principal principal)
	{

		try {
			Contact contact = contactRepository.findById(con.getcId()).get();

			if(result.hasErrors())
			{
				throw new Exception();
			}
			else {
				if(file.isEmpty())
				{
					con.setImage(contact.getImage());
				}
				else {
					File deleteFile = new ClassPathResource("static/images").getFile();
					File files = new File(deleteFile, contact.getImage());
					files.delete();



					File saveFile = new ClassPathResource("static/images").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath()+java.io.File.separator+file.getOriginalFilename());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					con.setImage(file.getOriginalFilename());
				}



				User user = contact.getUser();
				con.setUser(user);
				contactRepository.save(con);
			}

		}
		catch(Exception e) {
			e.printStackTrace();
			return "user/updateContact";
		}

		//model.addAttribute("contact", contact);
		return "redirect:/user/viewContacts/0";
	}
}
