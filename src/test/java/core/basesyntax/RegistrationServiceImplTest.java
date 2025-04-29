package core.basesyntax.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.db.Storage;
import core.basesyntax.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegistrationServiceImplTest {
    private RegistrationService registrationService;
    @BeforeEach
    void setUp() {
        registrationService = new RegistrationServiceImpl();
        Storage.people.clear();
    }

    @Test
    void register_nullUser_notOk() {
        RegistrationException ex = assertThrows(RegistrationException.class,
                () -> registrationService.register(null));
        assertEquals("User can't be null", ex.getMessage());
    }

    @Test
    void register_nullLogin_notOk() {
        User user = new User();
        user.setLogin(null);
        user.setPassword("password");
        user.setAge(20);

        RegistrationException ex = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals("Login can't be null", ex.getMessage());
    }

    @Test
    void register_shortLogin_notOk() {
        User user = new User();
        user.setLogin("abc");
        user.setPassword("password");
        user.setAge(20);

        RegistrationException ex = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals("Login length must be at least 6", ex.getMessage());
    }

    @Test
    void register_nullPassword_notOk() {
        User user = new User();
        user.setLogin("login123");
        user.setPassword(null);
        user.setAge(20);

        RegistrationException ex = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals("Password can't be null", ex.getMessage());
    }

    @Test
    void register_shortPassword_notOk() {
        User user = new User();
        user.setLogin("login123");
        user.setPassword("abc");
        user.setAge(20);

        RegistrationException ex = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals("Password length must be at least 6", ex.getMessage());
    }

    @Test
    void register_nullAge_notOk() {
        User user = new User();
        user.setLogin("login123");
        user.setPassword("password");
        user.setAge(null);

        RegistrationException ex = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals("Age can't be null", ex.getMessage());
    }

    @Test
    void register_underage_notOk() {
        User user = new User();
        user.setLogin("login123");
        user.setPassword("password");
        user.setAge(17);

        RegistrationException ex = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals("Age must be at least 18", ex.getMessage());
    }

    @Test
    void register_loginAlreadyExists_notOk() {
        User user = new User();
        user.setLogin("login123");
        user.setPassword("password");
        user.setAge(20);


        RegistrationException ex = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals("User with this login already exists", ex.getMessage());
    }


    }
}
