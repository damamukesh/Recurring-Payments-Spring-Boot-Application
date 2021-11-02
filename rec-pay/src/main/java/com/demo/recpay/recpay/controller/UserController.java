package com.demo.recpay.recpay.controller;

import com.demo.recpay.recpay.auth.MyUserDetails;
import com.demo.recpay.recpay.auth.MyUserDetailsService;
import com.demo.recpay.recpay.model.*;
import com.demo.recpay.recpay.services.UserService;
import com.demo.recpay.recpay.util.JwtUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String getHello(@RequestHeader(name="Authorization") String token){
        return "HEllo World!" + jwtTokenUtil.extractUsername(token.substring(7));
    }

    @GetMapping("/users")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable int id){
        return userService.getUser(id);
    }

    @PostMapping(value = "/signup")
    public void addUser(@RequestBody UserDTO userDto){
        userService.addUser(userDto);
    }

    @PostMapping(value = "/signups")
    public void addUsers(@RequestBody List<User> users){
        userService.addUsers(users);
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws  Exception {
        System.out.println(authenticationRequest.getUsername() + " " + authenticationRequest.getPassword());
        try{authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
        );}
        catch (BadCredentialsException e){
            throw new Exception("Incorrect username or password", e);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        System.out.println(userDetails);
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PutMapping("/add/{amount}")
    public String addMoney(Authentication authentication, @PathVariable int amount){
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        userService.addMoney(myUserDetails.getId(), amount);
        return "Money Added";
    }

    @PostMapping("/recurring_payment")
    public List<RecurringPayments> setupNewRecurringPayment(Authentication authentication, @RequestBody RecurringPaymentsDTO recurringPaymentsDTO){
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        return userService.setupNewRecurringPayment(myUserDetails.getId(), recurringPaymentsDTO);
    }

    @PutMapping("/payments")
    public String transferMonthlyPayments(Authentication authentication){
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        return userService.transferMonthlyPayments(myUserDetails.getId());
    }

    @PutMapping("/cancel/{recc_id}")
    public String removeRecurringPayment(Authentication authentication,@PathVariable int recc_id){
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        return userService.removeRecurringPayment(myUserDetails.getId(), recc_id);
    }

    @GetMapping("/getStatement/{from}/{to}")
    public List<Transactions> getStatement(Authentication authentication, @PathVariable String from, @PathVariable String to){
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        return userService.getStatement(myUserDetails.getId(), from, to);
    }

//    @PutMapping("/pay/{description}")
//    public String setupNewRecurringPayment(Authentication authentication, @PathVariable String description){
//        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
//        return userService.isRecurring(myUserDetails.getId(), description);
//    }

//    @PutMapping("/users/{id}/pay")
//    public String setupNewRecurringPayment(@PathVariable int id){
//        return userService.setupNewRecurringPayment(id);
//    }
//

}
