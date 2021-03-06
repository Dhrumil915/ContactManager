package com.contact.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contact.dao.UserRepo;
import com.contact.entites.User;
import com.contact.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepo userRepo;

	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home");
		return "home";
	}

	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Signup");
		model.addAttribute("user", new User());
		return "signup";
	}

	// handler for register user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1,
			@RequestParam(value = "agrreement", defaultValue = "false") boolean agreement, Model model,
			 HttpSession session
			) {

		try {
			

			if (!agreement) {
				System.out.println("You have not agree terms and condition");
				throw new Exception("You have not agree terms and condition");
			}
			
			if(result1.hasErrors()) {
				
				System.out.println("ERROR"+result1.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("Agreement" + agreement);
			System.out.println("User" + user);

			User result = this.userRepo.save(user);

			model.addAttribute("user", new User());
			
			session.setAttribute("message", new Message("Sucessfully Register", "alert-success"));
		}
		
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something wrong !!!!!"+e.getMessage(), "alert-danger"));
		}

		return "signup";
	}

	//handler for login form
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "Login");
		return "login";
	}
	
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About");
		return "about";
	}
}
