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
    private static final String VALID_LOGIN = "validLogin";
    private static final String VALID_PASSWORD = "validPass";
    private static final int VALID_AGE = 20;
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
        User user = createUser(null, VALID_PASSWORD, VALID_AGE);
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals("Login cannot be null", exception.getMessage());
    }

    @Test
    void register_shortLogin_notOk() {
        User user = createUser("abc", VALID_PASSWORD, VALID_AGE);
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals("Login must be at least "
                        + MIN_LOGIN_LENGTH + " characters long",
                exception.getMessage());
    }

    @Test
    void register_nullPassword_notOk() {
        User user = createUser(VALID_LOGIN, null, VALID_AGE);
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals("Password cannot be null", exception.getMessage());
    }

    @Test
    void register_shortPassword_notOk() {
        String[] shortPasswords = {"", "a", "abc", "abcde"};
        for (String pass : shortPasswords) {
            User user = createUser(VALID_LOGIN, pass, VALID_AGE);
            RegistrationException exception = assertThrows(RegistrationException.class,
                    () -> registrationService.register(user));
            assertEquals("Password must be at least " + MIN_PASSWORD_LENGTH
                    + " characters long", exception.getMessage());
        }
    }

    @Test
    void register_nullAge_notOk() {
        User user = createUser(VALID_LOGIN, VALID_PASSWORD, null);
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals("Age cannot be null", exception.getMessage());
    }

    @Test
    void register_negativeAge_notOk() {
        User user = createUser(VALID_LOGIN, VALID_PASSWORD, -5);
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals("User "
                        + "must be at least " + MIN_AGE + " years old, but was -5",
                exception.getMessage());
    }

    @Test
    void register_ageLessThanMinimum_notOk() {
        User user = createUser(VALID_LOGIN, VALID_PASSWORD, 17);
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
        assertEquals("User "
                        + "must be at least " + MIN_AGE + " years old, but was 17",
                exception.getMessage());
    }

    @Test
    void register_duplicateLogin_notOk() {
        User existingUser = createUser("duplicateLogin", "pass123", 25);
        Storage.people.add(existingUser);

        User newUser = createUser("duplicateLogin", "newPass123", 26);
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> registrationService.register(newUser));
        assertEquals("User with login 'duplicateLogin' already exists", exception.getMessage());
    }

    @Test
    void register_validUser_ok() {
        User user = createUser(VALID_LOGIN, VALID_PASSWORD, VALID_AGE);
        User registeredUser = registrationService.register(user);
        assertEquals(user, registeredUser);
        assertEquals(1, Storage.people.size());
    }

    @Test
    void register_boundaryLoginLength_ok() {
        User user = createUser("login", VALID_PASSWORD, VALID_AGE);
        RegistrationException ex = assertThrows(RegistrationException.class,
                () -> registrationService.register(user));

        User validLogin = createUser("logins", VALID_PASSWORD, VALID_AGE);
        User registered = registrationService.register(validLogin);
        assertEquals(validLogin, registered);
    }

    @Test
    void register_boundaryPasswordLength_ok() {
        User pass5 = createUser(VALID_LOGIN, "abcde", VALID_AGE);
        RegistrationException ex = assertThrows(RegistrationException.class,
                () -> registrationService.register(pass5));

        User pass6 = createUser(VALID_LOGIN, "abcdef", VALID_AGE);
        User registered = registrationService.register(pass6);
        assertEquals(pass6, registered);
    }

    @Test
    void register_boundaryAge_ok() {
        User age17 = createUser(VALID_LOGIN, VALID_PASSWORD, 17);
        RegistrationException ex = assertThrows(RegistrationException.class,
                () -> registrationService.register(age17));

        User age18 = createUser(VALID_LOGIN, VALID_PASSWORD, 18);
        User registered = registrationService.register(age18);
        assertEquals(age18, registered);
    }

    private User createUser(String login, String password, Integer age) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setAge(age);
        return user;
    }
}
