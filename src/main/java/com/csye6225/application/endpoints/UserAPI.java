package com.csye6225.application.endpoints;

import com.csye6225.application.MetricRegistry;
import com.csye6225.application.objects.ErrorResponse;
import com.csye6225.application.objects.Image;
import com.csye6225.application.objects.User;
import com.csye6225.application.objects.UserDTO;
import com.csye6225.application.repository.ImageRepository;
import com.csye6225.application.repository.UserRepository;
import com.csye6225.application.security.BucketCreated;
import com.csye6225.application.security.BucketName;
import com.csye6225.application.security.TokenUtils;
import com.csye6225.application.services.ImageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Service
@RestController
@RequestMapping("/v2")
public class UserAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAPI.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    ImageServiceImpl service;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenUtils tokenUtils;

    @Value("${cloud.aws.bucketname}")
    private String bucketName;

    @Autowired
    MetricRegistry metricRegistry;


    @PostMapping(value = "/user",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity createUser(@RequestBody User user){
        try {
            metricRegistry.getInstance().counter("Create User","csye6225","createuser endpoint").increment();
            LOGGER.info("Creating a user endpoint called");
            user.setPassword(passwordEncoder.encode( user.getPassword()));
            User temp = userRepository.findByUsername(user.getUsername());
            if (userRepository.findByUsername(user.getUsername()) == null) {
                userRepository.save(user);
                User userLatest = userRepository.findByUsername(user.getUsername());
                return ResponseEntity.status(HttpStatus.CREATED).body(userLatest);
            } else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("username already exists, try a different one"));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Unable to create user, check if input data is correct"));
        }
    }

    @PutMapping(value = "/user/self",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity updateUser(@RequestBody User user , @RequestHeader("Authorization") String basicToken){
        try {
            metricRegistry.getInstance().counter("Update User","csye6225","update user endpoint").increment();
            LOGGER.info("Update user details  endpoint called");
            if(!tokenUtils.extractUserName(basicToken).equals(user.getUsername())){
                return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Credentials do not match the user being updated");
            }
            User presentUser = userRepository.findByUsername(user.getUsername());
            if (presentUser != null) {
                user.setId(presentUser.getId());
                user.setPassword(passwordEncoder.encode( user.getPassword()));
                userRepository.save(user);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Unable to update data, check if data entered is of the correct "));
        }
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping(value = "/user/self",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String basicToken){
        metricRegistry.getInstance().counter("User get","csye6225","Getuser endpoint").increment();
        LOGGER.info("User details requested");
        System.out.println(basicToken);
        String username = tokenUtils.extractUserName(basicToken);
        User user = userRepository.findByUsername(username);
        if(user ==null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body( new ErrorResponse("User not found"));
        UserDTO userDTO = UserDTO.builder().firstName(user.getFirstName()).lastName(user.getLastName()).
                account_created(user.getAccount_created()).account_updated(user.getAccount_updated()).id(user.getId())
                .username(user.getUsername()).build();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body( userDTO);
    }

    @PostMapping(value="/user/self/pic",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> postPic(@RequestHeader("Authorization") String basicToken, @RequestPart("file") MultipartFile file ) {
        try{
            metricRegistry.getInstance().counter("Post Picture","csye6225","post picture endpoint").increment();
            LOGGER.info("Insert picture into S3 bucket endpoint called");
        System.out.println(basicToken);
        String tempfilename = file.getOriginalFilename();
            if(!(tempfilename.endsWith("png")||tempfilename.endsWith("jpg")||tempfilename.endsWith("jpeg"))){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Invalid file type"));
                }

        String username = tokenUtils.extractUserName(basicToken);
        User user = userRepository.findByUsername(username);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Invalid user"));
        Image checkImage = imageRepository.findByuserId(user.getId());
        if(checkImage!=null ){
            if( checkImage.getFileName().equals(file.getOriginalFilename()))
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(checkImage);
            Image tempimg = updateImage(checkImage,user,file);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(tempimg);
        }

        Image img = Image.builder().userId(user.getId()).fileName(file.getOriginalFilename()).build();
//        Image tempimg = imageRepository.save(img);
        // String fileName = String.format("%s", file.getOriginalFilename());
            BucketCreated bc = new BucketCreated();
        String path = String.format("%s/%s", bucketName, user.getId());
        img.setUrl(path+"/"+file.getOriginalFilename());
        // String path = BucketName.USER_IMAGE.getBucketName() +"/" + tempimg.getId() ;
        service.saveImage(path, file.getOriginalFilename(), file);
        imageRepository.save(img);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(img);

        }
        catch (Exception e){
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Error in inserting profile picture"));
        }
    }

    private Image updateImage(Image checkImage, User user, MultipartFile file) {
        LOGGER.info("Method to update image is called");
        performDelete(checkImage, user);
        Image img = Image.builder().userId(user.getId()).fileName(file.getOriginalFilename()).build();
//        Image tempimg = imageRepository.save(img);
        BucketCreated bc = new BucketCreated();
        String path = String.format("%s/%s", bucketName, user.getId());
        img.setUrl(path+"/"+file.getOriginalFilename());
        service.saveImage(path, file.getOriginalFilename(), file);
        imageRepository.save(img);

        return img;
    }

    @GetMapping(value="/user/self/pic",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getPic(@RequestHeader("Authorization") String basicToken){
        try {
            metricRegistry.getInstance().counter("Get profile pic","csye6225","getProfilePic endpoint").increment();
            LOGGER.info("Endpoint to retrieve picture is called");
            System.out.println(basicToken);
            String username = tokenUtils.extractUserName(basicToken);
            User user = userRepository.findByUsername(username);
            if (user == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User not found"));
            String userID = user.getId();
            Image image = imageRepository.findByuserId(userID);
            if (image == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("No profile pic to show"));
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(image);
        }catch (Exception e){
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Error in getting profile picture"));
        }

    }

    @DeleteMapping(value="/user/self/pic",
            produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?>deletePic(@RequestHeader("Authorization")String basicToken){
    try{
        metricRegistry.getInstance().counter("Delete Picture","csye6225","deletepicture endpoint").increment();
        LOGGER.info("Endpoint to delete picture is called");
        System.out.println(basicToken);
        String username=tokenUtils.extractUserName(basicToken);
        User user=userRepository.findByUsername(username);
        if(user==null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User not found"));
        String userID=user.getId();
        Image image=imageRepository.findByuserId(userID);
        if (image == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("No profile pic to delete"));
        performDelete(image,user);
//        imageRepository.deleteById(image.getId());
//        String path = String.format("%s/%s", BucketName.USER_IMAGE.getBucketName(), image.getId());
//        service.deleteImage(path,image.getFileName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }catch (Exception e){
        System.out.println(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Error in getting profile picture"));
    }
    }
    private void performDelete(Image image, User user) {
        BucketCreated bc = new BucketCreated();
        imageRepository.deleteById(image.getId());
        String path = String.format("%s/%s", bucketName, user.getId());
        service.deleteImage(path,image.getFileName());
    }



}
