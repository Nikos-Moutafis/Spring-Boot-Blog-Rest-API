package gr.example.blog.service.impl;

import gr.example.blog.dto.LoginDto;
import gr.example.blog.dto.RegisterDto;
import gr.example.blog.model.Role;
import gr.example.blog.model.User;
import gr.example.blog.repository.RoleRepository;
import gr.example.blog.repository.UserRepository;
import gr.example.blog.security.JwtTokenProvider;
import gr.example.blog.service.AuthService;
import gr.example.blog.service.exception.BlogAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
                           RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String login(LoginDto loginDto) {
        String userNameOrEmail = loginDto.getUsernameOrEmail();
        String password = loginDto.getPassword();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userNameOrEmail, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        return token;
    }

    @Override
    public String register(RegisterDto registerDto) {
        String username = registerDto.getUsername();
        String email = registerDto.getEmail();
        //Check i username already exists in database
        if (userRepository.existsByUsername(username)){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        //check if email exists already in database
        if (userRepository.existsByEmail(email)){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(registerDto.getPassword());

        User user = new User();
        user.setName(registerDto.getName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(hashedPassword);

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
        return "User registered successfully";
    }
}
