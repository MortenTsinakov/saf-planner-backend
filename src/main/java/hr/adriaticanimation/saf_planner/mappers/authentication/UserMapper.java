package hr.adriaticanimation.saf_planner.mappers.authentication;

import hr.adriaticanimation.saf_planner.dtos.authentication.SignUpRequest;
import hr.adriaticanimation.saf_planner.dtos.authentication.UserAuthenticationResponse;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.entities.user.UserRole;
import hr.adriaticanimation.saf_planner.services.authentication.JwtService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.Instant;

@Mapper(componentModel = "spring", imports = {Timestamp.class, Instant.class, UserRole.class})
public abstract class UserMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Autowired
    protected JwtService jwtService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(signUpRequest.password()))")
    @Mapping(target = "role", expression = "java(UserRole.USER)")
    @Mapping(target = "createdAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "updatedAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "lastLogin", expression = "java(Timestamp.from(Instant.now()))")
    public abstract User signUpRequestToUser(SignUpRequest signUpRequest);
    @Mapping(target = "jwt", expression = "java(jwtService.generateToken(user))")
    public abstract UserAuthenticationResponse userToUserAuthenticationResponse(User user);
}
