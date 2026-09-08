package com.fitness.userservice.service;

import com.fitness.userservice.DTO.AuthResponse;
import com.fitness.userservice.DTO.LoginRequest;
import com.fitness.userservice.DTO.UserDTO;
import com.fitness.userservice.DTO.UserRequest;
import com.fitness.userservice.exceptipns.BadCredentialsException;
import com.fitness.userservice.exceptipns.EmailAlreadyExistsException;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.InvalidCredentialsException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static io.lettuce.core.KillArgs.Builder.user;
import static org.antlr.v4.runtime.tree.xpath.XPath.findAll;

@Slf4j
@Service

public class UserService {
    
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserCacheService userCacheService;


    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, JwtService jwtService, UserCacheService userCacheService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userCacheService = userCacheService;
    }


    public UserDTO register(UserRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){

            throw new EmailAlreadyExistsException("Email Already Exist: "+request.getEmail());
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        return modelMapper.map(userRepository.save(modelMapper
                .map(user, User.class)), UserDTO.class);


    }


   public UserDTO fetchUserById(Integer id){

        return userCacheService.fetchByUserId(id);
   }


   public Boolean ExistsBYId(Integer id){

        return userRepository.existsById(id);
   }


    public AuthResponse login(LoginRequest request) {


        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(request.getEmail()));


        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(token);
    }


    public UserDTO updateUser(UserDTO userDto, Integer id){

         User user= userRepository.findById(id).orElseThrow(
                 ()-> new UsernameNotFoundException("User Not Found: "+ id));

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());

       User Saved= userRepository.save(user);

        userCacheService.evict(id);

        UserDTO savedDto=modelMapper.map(Saved, UserDTO.class );
        userCacheService.storeInCache(id, savedDto);

        return savedDto;

    }

    public Page<UserDTO> fetchAll(int page, int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);


        return userRepository.findAll(pageable)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

}
