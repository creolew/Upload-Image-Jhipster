package com.demo.upload.web.rest;

import com.demo.upload.domain.UserExtra;
import com.demo.upload.repository.UserExtraRepository;
import com.demo.upload.security.SecurityUtils;
import com.demo.upload.service.UserExtraService;
//import com.demo.upload.service.UserExtraService;
import com.demo.upload.service.UserService;
import com.demo.upload.service.upload.ResponseMessage;
import com.demo.upload.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.demo.upload.domain.UserExtra}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UserExtraResource {

    private final Logger log = LoggerFactory.getLogger(UserExtraResource.class);

    private static final String ENTITY_NAME = "userExtra";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserExtraRepository userExtraRepository;
    
    private final UserExtraService userExtraService;


    

    public UserExtraResource( UserExtraRepository userExtraRepository, UserExtraService userExtraService) {
		
    	
		this.userExtraRepository = userExtraRepository;
		this.userExtraService = userExtraService;
	}

	/**
     * {@code POST  /user-extras} : Create a new userExtra.
     *
     * @param userExtra the userExtra to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userExtra, or with status {@code 400 (Bad Request)} if the userExtra has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-extras")
    public ResponseEntity<UserExtra> createUserExtra(@RequestBody UserExtra userExtra) throws URISyntaxException {
        log.debug("REST request to save UserExtra : {}", userExtra);
        if (userExtra.getId() != null) {
            throw new BadRequestAlertException("A new userExtra cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserExtra result = userExtraRepository.save(userExtra);
        return ResponseEntity
            .created(new URI("/api/user-extras/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-extras/:id} : Updates an existing userExtra.
     *
     * @param id the id of the userExtra to save.
     * @param userExtra the userExtra to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userExtra,
     * or with status {@code 400 (Bad Request)} if the userExtra is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userExtra couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-extras/{id}")
    public ResponseEntity<UserExtra> updateUserExtra(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserExtra userExtra
    ) throws URISyntaxException {
        log.debug("REST request to update UserExtra : {}, {}", id, userExtra);
      
        if (userExtra.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
       
        if (!Objects.equals(id, userExtra.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userExtraRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        
        
        
        UserExtra result = userExtraRepository.save(userExtra);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userExtra.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-extras/:id} : Partial updates given fields of an existing userExtra, field will ignore if it is null
     *
     * @param id the id of the userExtra to save.
     * @param userExtra the userExtra to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userExtra,
     * or with status {@code 400 (Bad Request)} if the userExtra is not valid,
     * or with status {@code 404 (Not Found)} if the userExtra is not found,
     * or with status {@code 500 (Internal Server Error)} if the userExtra couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-extras/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserExtra> partialUpdateUserExtra(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserExtra userExtra
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserExtra partially : {}, {}", id, userExtra);
        if (userExtra.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userExtra.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userExtraRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserExtra> result = userExtraRepository
            .findById(userExtra.getId())
            .map(existingUserExtra -> {
                if (userExtra.getFrontImage() != null) {
                    existingUserExtra.setFrontImage(userExtra.getFrontImage());
                }
                if (userExtra.getBackImage() != null) {
                    existingUserExtra.setBackImage(userExtra.getBackImage());
                }

                return existingUserExtra;
            })
            .map(userExtraRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userExtra.getId().toString())
        );
    }

    /**
     * {@code GET  /user-extras} : get all the userExtras.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userExtras in body.
     */
    @GetMapping("/user-extras")
    public List<UserExtra> getAllUserExtras() {
        log.debug("REST request to get all UserExtras");
        return userExtraRepository.findAll();
    }

    /**
     * {@code GET  /user-extras/:id} : get the "id" userExtra.
     *
     * @param id the id of the userExtra to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userExtra, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-extras/{id}")
    public ResponseEntity<UserExtra> getUserExtra(@PathVariable Long id) {
        log.debug("REST request to get UserExtra : {}", id);
        Optional<UserExtra> userExtra = userExtraRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(userExtra);
    }

    /**
     * {@code DELETE  /user-extras/:id} : delete the "id" userExtra.
     *
     * @param id the id of the userExtra to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-extras/{id}")
    public ResponseEntity<Void> deleteUserExtra(@PathVariable Long id) {
        log.debug("REST request to delete UserExtra : {}", id);
        userExtraRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }





//-------------------------------------------------------------
    


    @PostMapping("/upload/user-extra/image")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("frontImage") MultipartFile frontImage,
    												  @RequestParam("backImage") MultipartFile backImage) {
    	System.out.println("HUY");

    	String message = "";
     
      try {
    	  userExtraService.saveFrontImage(frontImage);
    	  userExtraService.saveBackImage(backImage);


    	  message = "Uploaded the file successfully: " + frontImage.getOriginalFilename();
        
        userExtraService.updateUserExtraWithImage(
				Paths.get("uploadsFrontImage").resolve(frontImage.getOriginalFilename()).toString(),
				Paths.get("uploadsBackImage").resolve(backImage.getOriginalFilename()).toString() );


     
        
        return ResponseEntity.status(HttpStatus.OK)
      		  .body(new ResponseMessage(message));
      } 
      catch (Exception e) {
        message = "Could not upload the file: " + frontImage.getOriginalFilename() + "!";
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
      		  				.body(new ResponseMessage(message));
      }
    }

    
    
    
    
    


}
