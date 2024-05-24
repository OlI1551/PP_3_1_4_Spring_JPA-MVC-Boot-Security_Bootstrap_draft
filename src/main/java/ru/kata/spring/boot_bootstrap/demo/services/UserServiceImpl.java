package ru.kata.spring.boot_bootstrap.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_bootstrap.demo.dao.UserDao;
import ru.kata.spring.boot_bootstrap.demo.models.Role;
import ru.kata.spring.boot_bootstrap.demo.models.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<User> listUsers() {
        return userDao.getUsersList();
    }

    public User getUserById(Long userId) {
        return userDao.getUserById(userId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void create(User user, String role) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        if (role.equals("ROLE_ADMIN")) {
            roles.add(new Role(2L, "ROLE_ADMIN"));
        } else {
            roles.add(new Role(1L, "ROLE_USER"));
        }

        user.setRoles(roles);
        userDao.addUser(user);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void update(User user, String role) {
        // Find the user to update
        User existingUser = userDao.getUserById(user.getId());

        // Update user details
        existingUser.setId(user.getId());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setAge(user.getAge());
        existingUser.setEmail(user.getEmail());

        Set<Role> roles = new HashSet<>();
        if (role.equals("ROLE_ADMIN")) {
            roles.add(new Role(2L, "ROLE_ADMIN"));
        } else {
            roles.add(new Role(1L, "ROLE_USER"));
        }

        existingUser.setRoles(roles);
        userDao.updateUser(existingUser);
//        userDao.updateUser(userDao.getUserById(user.getId()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void delete(User user) {
        userDao.deleteUser(userDao.getUserById(user.getId()));
//        userDao.deleteUser(user);
    }
}
