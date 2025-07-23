package core.basesyntax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.db.Storage;
import core.basesyntax.exception.RegistrationException;
import core.basesyntax.model.User;
import core.basesyntax.service.RegistrationService;
import core.basesyntax.service.RegistrationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegistrationServiceImplTest {
    private static final int MIN_LOGIN_LENGTH = 6;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MIN_AGE = 18;

    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        registrationService = new RegistrationServiceImpl();
        Storage.people.clear();
    }

    @Test
    void register_nullUser_notOk() {
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> registrationService.register(null));
        assertEquals("User cannot be null", exception.getMessage());
    }

    @Test
    void register_nullLogin_notOk() {
        User user = createUser(null, "validPass", 20);
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals("Login cannot be null", exception.getMessage());
    }

    @Test
    void register_shortLogin_notOk() {
        User user = createUser("abcde", "validPass", 20);
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals(
                "Login must be at least " + MIN_LOGIN_LENGTH + " characters",
                exception.getMessage());
    }

    @Test
    void register_nullPassword_notOk() {
        User user = createUser("validLogin", null, 20);
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals("Password cannot be null", exception.getMessage());
    }

    @Test
    void register_shortPassword_notOk() {
        User user = createUser("validLogin", "12345", 20);
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals(
                "Password must be at least " + MIN_PASSWORD_LENGTH + " characters",
                exception.getMessage());
    }

    @Test
    void register_nullAge_notOk() {
        User user = createUser("validLogin", "validPass", null);
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals("Age cannot be null", exception.getMessage());
    }

    @Test
    void register_ageLessThan18_notOk() {
        User user = createUser("validLogin", "validPass", 17);
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals(
                "User must be at least " + MIN_AGE + " years old, but was 17",
                exception.getMessage());
    }

    @Test
    void register_validUser_ok() {
        User user = createUser("validLogin", "validPass1", 18);
        User registeredUser = registrationService.register(user);
        assertEquals(user, registeredUser);
        assertEquals(1, Storage.people.size());
    }

    @Test
    void register_duplicateLogin_notOk() {
        User existingUser = createUser("duplicateLogin", "pass123", 30);
        Storage.people.add(existingUser);

        User newUserWithSameLogin = createUser("duplicateLogin", "newPass123", 22);
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> registrationService.register(newUserWithSameLogin));
        assertEquals(
                "User with login 'duplicateLogin' already exists",
                exception.getMessage());
    }

    private User createUser(String login, String password, Integer age) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setAge(age);
        return user;
    }
}
