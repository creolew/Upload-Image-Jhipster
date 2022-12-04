package com.demo.upload.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.demo.upload.domain.User;
import com.demo.upload.repository.UserExtraRepository;
import com.demo.upload.repository.UserRepository;
import com.demo.upload.security.SecurityUtils;

@Service
@Transactional
public class UserExtraService {
	
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserExtraRepository userExtraRepository;
    
    private final UserRepository userRepository;


    private final Path rootFrontImage = Paths.get("uploadsFrontImage");
    private final Path rootBackImage = Paths.get("uploadsBackImage");


	
	 public UserExtraService(UserExtraRepository userExtraRepository, UserRepository userRepository) {
		this.userExtraRepository = userExtraRepository;
		this.userRepository = userRepository;

	}

	 
	 public void updateUserExtraWithImage(String frontImageUr, String backImageUr) {
		 String userLogin = SecurityUtils
		            .getCurrentUserLogin()
		            .orElseThrow(null);
	     User user = userRepository.findOneByLogin(userLogin).orElse(null);	     
	     com.demo.upload.domain.UserExtra userExtra = (com.demo.upload.domain.UserExtra)userExtraRepository.findByUserId( user.getId() ).orElse(null);
	 
	     userExtra.setFrontImage(frontImageUr);
	     userExtra.setBackImage(backImageUr);

	     userExtraRepository.save(userExtra);
	 
	 }
	 
	 
	 
	 
	 
	 
	 
	 
	 
	
	public void initFrontImageDirectory() {
        try {
          Files.createDirectory(rootFrontImage);
        } catch (IOException e) {
          throw new RuntimeException("Could not initialize folder for upload!");
        }
      }
	
	public void initBackImageDirectory() {
        try {
          Files.createDirectory(rootBackImage);

        } catch (IOException e) {
          throw new RuntimeException("Could not initialize folder for upload!");
        }
      }


    
    public void saveFrontImage(MultipartFile frontImage) {
      try {
        Files.copy(frontImage.getInputStream(), this.rootFrontImage.resolve(frontImage.getOriginalFilename()));

      } catch (Exception e) {
        throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
      }
    }
    
    public void saveBackImage(MultipartFile backImage) {
        try {
          Files.copy(backImage.getInputStream(), this.rootFrontImage.resolve(backImage.getOriginalFilename()));
        } catch (Exception e) {
          throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
      }

    //------------------------------
    public Resource load(String filename) {
      try {
        Path file = rootFrontImage.resolve(filename);
        Resource resource = new UrlResource(file.toUri());

        if (resource.exists() || resource.isReadable()) {
          return resource;
        } else {
          throw new RuntimeException("Could not read the file!");
        }
      } catch (MalformedURLException e) {
        throw new RuntimeException("Error: " + e.getMessage());
      }
    }

    
    public void deleteAll() {
      FileSystemUtils.deleteRecursively(rootFrontImage.toFile());
      FileSystemUtils.deleteRecursively(rootBackImage.toFile());

    }

    public Stream<Path> loadAll() {
      try {
        return Files.walk(this.rootFrontImage, 1).filter(path -> !path.equals(this.rootFrontImage)).map(this.rootFrontImage::relativize);
      } catch (IOException e) {
        throw new RuntimeException("Could not load the files!");
      }
    }
}
