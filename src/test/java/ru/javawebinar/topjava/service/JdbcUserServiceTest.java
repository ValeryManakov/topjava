package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.JdbcActiveDbProfileResolver;

@ActiveProfiles(resolver = JdbcActiveDbProfileResolver.class)
public class JdbcUserServiceTest extends UserServiceTest {
}
