package com.contact.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.contact.dao.ContactRepo;
import com.contact.dao.MyOrderRepo;
import com.contact.dao.UserRepo;
import com.contact.entites.Contact;
import com.contact.entites.MyOrder;
import com.contact.entites.User;
import com.contact.helper.Message;
import com.razorpay.*;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ContactRepo contactRepo;
	
	@Autowired
	private MyOrderRepo myorderRepo;

	// method for adding commen data
	@ModelAttribute
	public void addCommenData(Model model, Principal principle) {

		String userName = principle.getName();
		System.out.println(userName);

		User user = userRepo.getUserByUserName(userName);
		System.out.println(user);

		model.addAttribute("user", user);
	}

	// dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principle) {
		model.addAttribute("title", "User Dashboard");
		return "public/userdash";
	}

	// add form handler
	@GetMapping("/add-contact")
	public String AddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "public/add-contactform";
	}

	// processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {

			String name = principal.getName();
			User user = this.userRepo.getUserByUserName(name);

			// processing uploading file

			if (file.isEmpty()) {
				// if file is empty then try our message
				System.out.println("File is empty");
				contact.setImage("image/contact1.png");
			} else {
				// file the file to folder and update name to contact
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/image").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image file uploaded");
			}

			user.getContact().add(contact);

			contact.setUser(user);

			this.userRepo.save(user);

			System.out.println("Added to database");

			System.out.println(contact);

			// message success
			session.setAttribute("message", new Message("Your Contact is added", "success"));
		} catch (Exception e) {
			System.out.println("ERROR" + e.getMessage());
			e.printStackTrace();
			// mesage unsuccess
			session.setAttribute("message", new Message("Some went wrong !!!!", "danger"));
		}
		return "public/add-contactform";
	}

	// Show contact handler

	// par page[5]
	// current page = 0[page]

	@GetMapping("/all-contacts/{page}")
	public String showContact(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Show Contact");

		String userName = principal.getName();
		User user = this.userRepo.getUserByUserName(userName);

		// current page
		// contact per page -5
		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = this.contactRepo.findContactsByUser(user.getId(), pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		// 1 way to connect to contact in database
		/*
		 * String userName = principal.getName(); User user =
		 * this.userRepo.getUserByUserName(userName); List<Contact> contacts =
		 * user.getContact();
		 */

		return "public/show-contact";
	}

	// showing particular contact details;
	@RequestMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		System.out.println(cId);

		Optional<Contact> contactOptional = this.contactRepo.findById(cId);
		Contact contact = contactOptional.get();

		String userName = principal.getName();
		User user = this.userRepo.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {

			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}

		return "public/contact-details";
	}

	// delete Contact
	@GetMapping("/delete/{cid}")

	public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session,
			Principal principal) {

		Optional<Contact> contactopOptional = this.contactRepo.findById(cId);
		Contact contact = contactopOptional.get();

		String userName = principal.getName();
		User user = this.userRepo.getUserByUserName(userName);

		// check...
		if (user.getId() == contact.getUser().getId()) {

			System.out.println(contact.getcId());
			// contact.setUser(null);
			user.getContact().remove(contact);

			this.userRepo.save(user);

			System.out.println("DELETED");
			session.setAttribute("message", new Message("Contact delete succesfully", "success"));
		}

		return "redirect:/user/all-contacts/0";
	}

	// open update contact
	@PostMapping("/update-contact/{cid}")
	public String updateContact(@PathVariable("cid") Integer cId, Model model) {
		model.addAttribute("title", "Update form");

		Contact contact = this.contactRepo.findById(cId).get();

		model.addAttribute("contact", contact);

		return "public/update-form";
	}

	// update contact
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, HttpSession session, Principal principal) {

		try {

			// old contact details
			Contact oldContact = this.contactRepo.findById(contact.getcId()).get();

			// checking image
			if (!file.isEmpty()) {
// file work
// file rewrite

				// delete old photo
				File deleteFile = new ClassPathResource("static/image").getFile();
				File file1 = new File(deleteFile, oldContact.getImage());
				file1.delete();
				// update file

				File saveFile = new ClassPathResource("static/image").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImage(file.getOriginalFilename());

			} else {
				contact.setImage(oldContact.getImage());
			}

			User user = this.userRepo.getUserByUserName(principal.getName());
			contact.setUser(user);

			this.contactRepo.save(contact);

			session.setAttribute("message", new Message("Your contact is updated...", "success"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(contact.getName());
		System.out.println(contact.getcId());
		return "redirect:/user/" + contact.getcId() + "/contact";
	}

	// my profile
	@GetMapping("/myprofile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile");
		return "public/profile";
	}

	// setting password
	@GetMapping("/settings")
	public String openSetting() {
		return "public/user-settings";
	}

	// change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("Oldpassword") String Oldpassword,
			@RequestParam("Newpassword") String Newpassword, Principal principal, HttpSession session) {

		System.out.println(Oldpassword);
		System.out.println(Newpassword);

		String userName = principal.getName();
		User Currentuser = this.userRepo.getUserByUserName(userName);
		System.out.println(Currentuser.getPassword());

		if (this.passwordEncoder.matches(Oldpassword, Currentuser.getPassword())) {
			// change password
			Currentuser.setPassword(this.passwordEncoder.encode(Newpassword));
			this.userRepo.save(Currentuser);
			session.setAttribute("message", new Message("Your password is updated...", "success"));
		} else {
			// error

			session.setAttribute("message", new Message("Your password is wrong...", "danger"));
			return "redirect:/user/settings";
		}

		return "redirect:/user/index";
	}

	// creating order from payment

	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data,Principal principal) throws RazorpayException {

		System.out.println("Order function succesfully run... ");
		System.out.println(data);

		 int amt = Integer.parseInt(data.get("amount").toString());
		
		  var client = new RazorpayClient("rzp_test_z73Yi5BEZxQOrd", "BT3aKY4fNZL5gdHgnfzUHlTF");
		 
		  JSONObject ob = new JSONObject();
		  ob.put("amount", amt*100);
		  ob.put("currency", "INR");
		  ob.put("receipt", "txn_235425");
		  
		  //creating new order
		  Order order = client.Orders.create(ob);
		  System.out.println(order);
		  
		  //save order in database
		 MyOrder myOrder = new MyOrder();
		  
		  myOrder.setAmount(order.get("amount")+"");
		  myOrder.setOrderId(order.get("id"));
		  myOrder.setPaymentId(null);
		  myOrder.setStatus("created");
		  myOrder.setUser(this.userRepo.getUserByUserName(principal.getName()));
		  myOrder.setReceipt(order.get("receipt"));
		  
		  this.myorderRepo.save(myOrder);
		  
		return order.toString();
	}
	
	//update order
	@PostMapping("/update_order")	
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data){
		
		MyOrder myorder = this.myorderRepo.findByOrderId(data.get("order_id").toString());
		
		myorder.setPaymentId(data.get("payment_id").toString());
		myorder.setStatus(data.get("status").toString());
		
		this.myorderRepo.save(myorder);
		
		System.out.println(data);
		return ResponseEntity.ok(Map.of("msg","updated"));
	}
	
}
