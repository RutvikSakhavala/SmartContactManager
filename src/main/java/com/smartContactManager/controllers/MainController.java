package com.smartContactManager.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartContactManager.entities.User;
import com.smartContactManager.helper.Message;
import com.smartContactManager.helper.SessionRemover;
import com.smartContactManager.repositories.UserRepository;
import com.smartContactManager.services.MailService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class MainController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MailService mailService;

	@Autowired
	private SessionRemover sessionRemover;

	@GetMapping("/home")
	public String home(Model model) {
		model.addAttribute("title","Home");
		return "index";
	}

	@GetMapping(value="/about")
	public String about(Model model)
	{
		model.addAttribute("title","About");
		return "about";
	}


	@GetMapping("/signup")
	public String signup(Model model)
	{
		model.addAttribute("title","Sign Up");
		model.addAttribute("user", new User());
		return "signup";
	}

	@PostMapping("/signupData")
	public String signupData(@Valid @ModelAttribute("user") User user, BindingResult result,
		@RequestParam("image") MultipartFile file,@RequestParam(value="agreement",required = false)String agreement,Model model, HttpSession session)
	{
		model.addAttribute("title","Sign Up");
		
		try {
			
			if(agreement==null || result.hasErrors())
			{				
				//System.out.println("Term And Conditions must be checked");
				throw new Exception();
			}
			else {
				if(userRepository.getUserByUserName(user.getEmail())!=null)
				{
					session.setAttribute("message", new Message("Email already exists","danger"));
					throw new Exception();
				}
				if(file.isEmpty())
				{
					user.setImageUrl("userid.png");
				}
				else {
					
					File saveFile = new ClassPathResource("static/images").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					user.setImageUrl(file.getOriginalFilename());
				}
				user.setAgreement(true);
				user.setRole("ROLE_USER");
				user.setEnabled(true);
				user.setPassword(passwordEncoder.encode(user.getPassword()));
				userRepository.save(user);
				session.setAttribute("message", new Message("Signup Successfull","success"));
				return "redirect:signup";
			}
		} catch (Exception e) {

			return "signup";
		}

	}
//
	@GetMapping("/login")
	public String login()
	{
		return "login";
	}

	@GetMapping("/forgetPasswd")
	public String forgetPasswd()
	{
		return "forgetPasswd";
	}
	@PostMapping("/sendOtp")
	public String sendOtp(@RequestParam("email")String email,HttpSession session)
	{
		User user = userRepository.getUserByUserName(email);

		if(user!=null)
		{
			Random random = new Random(1000);
			int otp = random.nextInt();
			mailService.sendMail(session,email,"SCM OTP",String.valueOf(otp));
			session.setAttribute("otp", otp);
			session.setAttribute("user", email);
			session.setAttribute("message", new Message("We sent otp to your email", "success"));
			return "varifyOtp";
		}
		session.setAttribute("message", new Message("Please enter Regestered email", "danger"));
		return "forgetPasswd";
	}
//
	@PostMapping("/varifyOtp")
	public String varifyOtp(@RequestParam("otp")int otp,HttpSession session,Model model)
	{
		if(otp == (int) session.getAttribute("otp")) {
			System.out.println("okk");
			sessionRemover.removeAttribute("OTP");

			return "changePasswd";
		}
		System.out.println("not matched");
		System.out.println(session.getAttribute("OTP"));
		session.setAttribute("message", new Message("Please enter valid otp", "danger"));
			return "varifyOtp";
	}
	@PostMapping("/changePasswd")
	public String changePasswd(@RequestParam("newPasswd")String newPasswd,HttpSession session)
	{
		User user = userRepository.getUserByUserName((String)session.getAttribute("user"));
		user.setPassword(passwordEncoder.encode(newPasswd));
		user.setAgreement(true);
		userRepository.save(user);
		session.setAttribute("message", new Message("Password changed successfully", "success"));
		sessionRemover.removeAttribute("user");

		return "login";
	}

}
