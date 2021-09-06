package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.JpaActiveDbProfileResolver;

@ActiveProfiles(resolver = JpaActiveDbProfileResolver.class)
public class JpaUserServiceTest extends UserServiceTest {
}
