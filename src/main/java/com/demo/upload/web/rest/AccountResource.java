package com.demo.upload.web.rest;

import com.demo.upload.config.Constants;
import com.demo.upload.domain.User;
import com.demo.upload.repository.UserRepository;
import com.demo.upload.security.SecurityUtils;
import com.demo.upload.service.MailService;
import com.demo.upload.service.UserService;
import com.demo.upload.service.dto.AdminUserDTO;
import com.demo.upload.service.dto.PasswordChangeDTO;
import com.demo.upload.service.upload.ResponseMessage;
import com.demo.upload.web.rest.errors.*;
import com.demo.upload.web.rest.vm.KeyAndPasswordVM;
import com.demo.upload.web.rest.vm.ManagedUserVM;


import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    public AccountResource(UserRepository userRepository, UserService userService, MailService mailService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (isPasswordLengthInvalid(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        User user = userService.registerUser(managedUserVM, 
        									managedUserVM.getPassword(),
        									managedUserVM.getFrontImage(),
        									managedUserVM.getBackImage());
        mailService.sendActivationEmail(user);
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this activation key");
        }
    }

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public AdminUserDTO getAccount() {
        return userService
            .getUserWithAuthorities()
            .map(AdminUserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody AdminUserDTO userDTO) {
        String userLogin = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new AccountResourceException("Current user login not found"));
        
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        
        }
        
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        
        if (!user.isPresent()) {
            throw new AccountResourceException("User could not be found");
        }
        
        userService.updateUser(
            userDTO.getFirstName(),
            userDTO.getLastName(),
            userDTO.getEmail(),
            userDTO.getLangKey(),
            userDTO.getImageUrl()
        );
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     */
    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
        Optional<User> user = userService.requestPasswordReset(mail);
        if (user.isPresent()) {
            mailService.sendPasswordResetMail(user.get());
        } else {
            // Pretend the request has been successful to prevent checking which emails really exist
            // but log that an invalid attempt has been made
            log.warn("Password reset requested for non existing mail");
        }
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this reset key");
        }
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }




//---------------------------------------------------------------

//    private static final Path CURRENT_FOLDER = Paths.get(System.getProperty("user.dir"));
//
//    @PostMapping("/upload")
//    @ResponseStatus(HttpStatus.CREATED)
//    public void uploadImage(@Valid 
//    						@RequestParam MultipartFile image) {
//	      
//    	try {
//    		  Path staticPath = Paths.get("static");	       
//	  	      Path imagePath = Paths.get("images");	      
//	  	      if (!Files.exists(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath))) {
//	  	            Files.createDirectories(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath));
//	  	        }        
//	  	        Path file = CURRENT_FOLDER.resolve(staticPath)
//	  	                				  .resolve(imagePath)
//	  	                				  .resolve(image.getOriginalFilename());        
//	  	        try (OutputStream os = Files.newOutputStream(file)) {
//	  	            os.write(image.getBytes());
//	  	        }
//	  	       
//	  	        
//	  	        String userLogin = SecurityUtils
//	  								.getCurrentUserLogin()
//	  								.orElseThrow(() -> new AccountResourceException("Current user login not found"));
//	  	        
//	  	        System.out.println(userRepository.findOneByLogin(userLogin));
//	  	        
//	  	        
//	  	        userService.updateUserWithImage(
//	  	        		imagePath.resolve(image.getOriginalFilename()).toString()
//	  	        );
//		} catch (Exception e) {
//			
//			System.out.println(e);
//			
//		}
//    	
//    	
//    	
//	    }
	        
    
    
  //---------------------------------------------------------------


    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
    	System.out.println("HUY");

    	String message = "";
     
      try {
    	  userService.save(file);

        message = "Uploaded the file successfully: " + file.getOriginalFilename();
        

		userService.updateUserWithImage(
				Paths.get("uploads").resolve(file.getOriginalFilename()).toString());

     
        
        return ResponseEntity.status(HttpStatus.OK)
      		  .body(new ResponseMessage(message));
      } 
      catch (Exception e) {
        message = "Could not upload the file: " + file.getOriginalFilename() + "!";
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
      		  				.body(new ResponseMessage(message));
      }
    }
    
 
    





    




}
