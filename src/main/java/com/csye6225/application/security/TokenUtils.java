package com.csye6225.application.security;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class TokenUtils {

    public String generrateToken(UserDetails userDetails){
            return createToken("salt", userDetails.getPassword());
    }

    private String createToken(String salt, String passwd) {
        Base64 b = new Base64();
        String encoding = b.encodeAsString(new String("test1:" + passwd).getBytes());
//        String encoding1 = decodeToken(encoding);
        return  encoding;
    }

    public String extractToken(String token){
        String decodedToken = decodeToken(token);
        return decodedToken.split(":")[1];
    }

    public String extractUserName(String token){
        token = getToken(token);
        String decodedToken = decodeToken(token);
        return decodedToken.split(":")[0];
    }

    public String extractPassword(String token){
        System.out.println(token);
        token = getToken(token);
        String decodedToken = decodeToken(token);
        System.out.println(decodedToken);
        return decodedToken.split(":")[1];
    }

    private String getToken( String token){
        return token.split(" ")[1];
    }
    private String decodeToken(String jwt){
        Base64 b = new Base64();
        byte[] decoding = b.decode(jwt);
        return new String(decoding);
    }
}
