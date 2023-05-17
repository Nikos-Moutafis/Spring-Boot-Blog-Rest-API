package gr.example.blog.service;

import gr.example.blog.dto.LoginDto;
import gr.example.blog.dto.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);
}
