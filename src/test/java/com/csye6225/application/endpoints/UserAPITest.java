package com.csye6225.application.endpoints;

import com.csye6225.application.objects.ErrorResponse;
import com.csye6225.application.objects.User;
import com.csye6225.application.repository.UserRepository;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class UserAPITest {

//    @ClassRule
//    public final EnvironmentVariables environmentVariables = new EnvironmentVariables().set("name", "value");

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserAPI userAPI;

    @Test
    public void testInitioalization(){
        assertNotEquals(userAPI,null);
    }

    @Before
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateUser() {
        User usr = User.builder().username("username@gmail.com")
                .password("password").firstName("First").lastName("Last").build();
        ResponseEntity re = userAPI.createUser(usr);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("mockedpassword");
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(usr);
        assertEquals(HttpStatus.CREATED.value(),re.getStatusCode().value());
    }

    @Test
    public void testCreateExistingUser() {
        User usr = User.builder().username("username@gmail.com")
                .password("password").firstName("First").lastName("Last").build();

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("mockedpassword");
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(usr);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(usr);
        ResponseEntity re = userAPI.createUser(usr);
        assertEquals(HttpStatus.BAD_REQUEST.value(),re.getStatusCode().value());
        assertEquals(ErrorResponse.class,re.getBody().getClass());
    }
}