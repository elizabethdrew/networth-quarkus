package com.networth.userservice.util;

import com.networth.userservice.exception.InvalidInputException;
import com.networth.userservice.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.Rule;
import org.passay.RuleResult;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class HelperUtils {

    private static final Logger LOG = Logger.getLogger(HelperUtils.class);
    private final UserRepository userRepository;

    public HelperUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validatePassword(String password) {
        List<Rule> rules = new ArrayList<>();
        rules.add(new LengthRule(8, 100));
        rules.add(new CharacterRule(EnglishCharacterData.UpperCase, 1));
        rules.add(new CharacterRule(EnglishCharacterData.LowerCase, 1));
        rules.add(new CharacterRule(EnglishCharacterData.Digit, 1));
        rules.add(new CharacterRule(EnglishCharacterData.Special, 1));

        PasswordValidator validator = new PasswordValidator(rules);
        RuleResult result = validator.validate(new PasswordData(password));

        if (!result.isValid()) {
            throw new InvalidInputException("Invalid password: " + String.join(", ", validator.getMessages(result)));
        }

        LOG.debug("Password validation passed");
    }

    public void validateUsernameUnique(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new InvalidInputException("Username already in use: " + username);
        }
    }

    public void validateEmailUnique(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new InvalidInputException("Email already in use: " + email);
        }
    }
}
