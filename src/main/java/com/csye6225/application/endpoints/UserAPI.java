package com.csye6225.application.endpoints;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.csye6225.application.MetricRegistry;
import com.csye6225.application.objects.*;
import com.csye6225.application.repository.ImageRepository;
import com.csye6225.application.repository.UserRepository;
import com.csye6225.application.security.BucketCreated;
import com.csye6225.application.security.BucketName;
import com.csye6225.application.security.TokenUtils;
import com.csye6225.application.services.ImageServiceImpl;
import com.csye6225.application.services.MessagePublisherImpl;
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
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.SnsClientBuilder;
import software.amazon.awssdk.services.sns.model.SnsResponse;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    @Autowired
    MessagePublisherImpl messagePublisher;

    private DynamoDB dynamoDB;

    private static String tableName = "emailTokenTbl";
    private AmazonDynamoDB client;
//    private SnsClient snsClient;

    @PostConstruct
    void init(){
        client =  AmazonDynamoDBClientBuilder.standard().withCredentials(new InstanceProfileCredentialsProvider(false))
                .withRegion("us-east-1").build();


//         snsClient = SnsClient.builder().region(Region.US_EAST_1).credentialsProvider(software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider.builder().build()).build();
        dynamoDB = new DynamoDB(client);
    }


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
                user.setVerified(false);
                userRepository.save(user);
                User userLatest = userRepository.findByUsername(user.getUsername());
                Table table = dynamoDB.getTable(tableName);
                String tkn = generateUniqueId();
                    Item item = new Item().withString("emailid",user.getUsername())
                            .withString("email", user.getUsername())
                            .withLong("ttl",(System.currentTimeMillis() / 1000L)+ 60)
                            .withString("token",tkn);
                    table.putItem(item);
                Message message = new Message(user.getUsername(),tkn,"publish message");
                LOGGER.info(message.toString());

                messagePublisher.publish(message);
                LOGGER.info("Message published");

                return ResponseEntity.status(HttpStatus.CREATED).body(userLatest);
            } else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("username already exists, try a different one"));
        }catch (Exception e){
            LOGGER.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Unable to create user, check if input data is correct"));
        }
    }

    private static String generateUniqueId() {
        UUID idOne = UUID.randomUUID();
        String str=""+idOne;
        long uid=str.hashCode();
        String filterStr=""+uid;
        str=filterStr.replaceAll("-", "");
        return str;
    }


//    http://prod.domain.tld/v1/verifyUserEmail?email=ebenezerwilliams@northeastern.edu&token=1564076567
//    arn:aws:sns:us-east-1:556795868226:UserVerificationTopic
//    ebenezerwilliams@northeastern.edu

    @GetMapping(value = "/verifyUserEmail",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> verifyUser(@RequestParam("email") String email, @RequestParam("token") String token ){

        metricRegistry.getInstance().counter("verifyUser get","csye6225","verifyUser endpoint").increment();
        LOGGER.info("verifyUser requested"+email + " " +token);

        Table table = dynamoDB.getTable(tableName);
        Item item = table.getItem("emailid", email, "emailid, email, token",null);
        LOGGER.info(item.toString());
        //        if(itemmap.get("emailid").equals(email) && itemmap.get("token").equals(token)){
//            User presentUser = userRepository.findByUsername(email);
//            presentUser.setVerified(true);
//            userRepository.save(presentUser);
//        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body( null);
    }

//    private static void retrieveItem() {
//        Table table = dynamoDB.getTable(tableName);
//
//        try {
//
//            Item item = table.getItem("Id", 120, "Id, ISBN, Title, Authors", null);
//
//            System.out.println("Printing item after retrieving it....");
//            System.out.println(item.toJSONPretty());
//
//        }
//        catch (Exception e) {
//            System.err.println("GetItem failed.");
//            System.err.println(e.getMessage());
//        }
//
//    }

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
        if(!user.getVerified())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body( new ErrorResponse("User not verified"));
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
