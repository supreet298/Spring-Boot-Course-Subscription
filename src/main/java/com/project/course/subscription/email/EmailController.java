package com.project.course.subscription.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.course.subscription.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/email")
public class EmailController {

	@Autowired
	private EmailService emailService;

	@PostMapping("/send")
	public ResponseEntity<String> sendEmail(@RequestParam String to, @RequestParam String subject,
			@RequestParam String body) {
		emailService.sendEmail(to, subject, body);
		return ResponseEntity.ok("Email sent successfully");
	}

	@GetMapping("/getEmailProperties")
	public ResponseEntity<EmailSettingResponseDTO> getEmailPropertiess() {
		try {
			EmailSettingResponseDTO properties = emailService.getEmailProperties();
			return ResponseEntity.ok(properties);
		} catch (ResourceNotFoundException ex) {
			return ResponseEntity.notFound().build(); // 404 Not Found if properties not found
		} catch (Exception ex) {
			return ResponseEntity.status(500).body(null);
		}
	}
	
	@PutMapping("/updateEmail")
    public ResponseEntity<EmailSetting>updateEmailProprties(@RequestBody EmailSetting settings)
    {
    	try {
    	EmailSetting result=emailService.updateEmailProperties(settings);
    	return ResponseEntity.ok(result);    	}
    	catch(ResourceNotFoundException  e)
    	{
            return ResponseEntity.notFound().build(); // Returns a 404 Not Found response
    	}
    	catch(Exception e)
    	{
    	    return ResponseEntity.status(500).body(null);
    	}
    }
    }
