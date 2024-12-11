package hr.adriaticanimation.saf_planner.mappers.authentication;

import hr.adriaticanimation.saf_planner.dtos.authentication.SignUpRequest;
import hr.adriaticanimation.saf_planner.dtos.authentication.UserAuthenticationResponse;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.entities.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userMapper.setPasswordEncoder(passwordEncoder);
    }

    @Test
    void signUpRequestToUser() {
        String email = "test@test.com";
        String firstName = "John";
        String lastName = "Smith";
        String password = "password";
        String expectedPassword = "hashed_password";
        UserRole expectedRole = UserRole.USER;

        SignUpRequest signUpRequest = new SignUpRequest(
                email,
                firstName,
                lastName,
                password
        );

        when(passwordEncoder.encode(password)).thenReturn(expectedPassword);

        User user = userMapper.signUpRequestToUser(signUpRequest);

        assertNotNull(user);

        assertEquals(email, user.getEmail());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(expectedPassword, user.getPassword());
        assertEquals(expectedRole, user.getRole());

        assertInstanceOf(Timestamp.class, user.getCreatedAt());
        assertInstanceOf(Timestamp.class, user.getUpdatedAt());
        assertInstanceOf(Timestamp.class, user.getLastLogin());
    }

    @Test
    void userToUserAuthenticationResponse() {
        Long id = 1L;
        String email = "test@test.com";
        String firstName = "John";
        String lastName = "Smith";

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        UserAuthenticationResponse response = userMapper.userToUserAuthenticationResponse(user);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals(email, response.email());
        assertEquals(firstName, response.firstName());
        assertEquals(lastName, response.lastName());
    }
}