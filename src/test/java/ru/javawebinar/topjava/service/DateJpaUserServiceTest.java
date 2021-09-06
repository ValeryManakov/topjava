package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.DataJpaActiveDbProfileResolver;

@ActiveProfiles(resolver = DataJpaActiveDbProfileResolver.class)
public class DateJpaUserServiceTest extends UserServiceTest {
}
