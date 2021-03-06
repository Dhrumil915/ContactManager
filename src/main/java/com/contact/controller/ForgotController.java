package com.contact.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contact.dao.UserRepo;
import com.contact.entites.User;
import com.contact.service.Emailservice;

@Controller
public class ForgotController {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	Random random = new Random(1000);
	
	@Autowired
	private Emailservice emailService; 

	// Email id form
	@RequestMapping("/forgot")
	public String openEmailForm() {
		return "forgot-email";
	}

	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email,HttpSession session) {
		System.out.println(email);

		// generating otp of 5 digit

		int otp = random.nextInt(99999);

		System.out.println(otp);

		//send otp to email
		String subject = "OTP FROM DH";
		String message = ""
				+ "<div style='border:1px solid #e2e2e2; paddding: 20px;'>"
				+ "<h1>"
				+ "OTP is"
				+"<b>"+otp
				+"</b>"
				+"</h1>"
				+"</div>";
		String to = email;
		
		boolean flag = this.emailService.sendEmail(subject, message, to);
		
		if(flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "varify_otp";
		}
		else {
			session.setAttribute("message", "Check your email id...");
			return "forgot-email";	
		}
		
	}
	
	//verify otp
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp,HttpSession session) {
		
		int myOtp = (int) session.getAttribute("myotp");
		String email = (String) session.getAttribute("email");
		
		if(myOtp==otp) {
			//password change form
			User user = this.userRepo.getUserByUserName(email);
			
			if(user==null) {
				//send error message
				session.setAttribute("message", "User Does not exist !!!!");
				return "forgot-email";	
				
			}
			else {
				//send change password
			}
			
			return "password_change";
		}else {
			session.setAttribute("message", "You entered wrong OTP !!!!");
		return "varify_otp";
		}
		
	}
	
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword,HttpSession session) {
		
		String email = (String) session.getAttribute("email");
	
		User user = this.userRepo.getUserByUserName(email);
		
		user.setPassword(this.passwordEncoder.encode(newpassword));
		
		this.userRepo.save(user);
		
		return "redirect:/signin?change=password changed successfully";
	}
	
}
