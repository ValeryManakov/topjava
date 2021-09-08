package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.DataJpaActiveDbProfileResolver;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.UserTestData.MATCHER;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ActiveProfiles(resolver = DataJpaActiveDbProfileResolver.class)
public class DataJpaUserServiceTest extends UserServiceTest {

    @Test
    public void getWithMeal() {
        User user = service.get(USER_ID);
        List<Meal> meals = user.getMeals().stream().
                                sorted(Comparator.comparing(Meal::getId).reversed()).
                                collect(Collectors.toList());
        UserTestData.MATCHER.assertMatch(user, UserTestData.user);
        MealTestData.MATCHER.assertMatch(meals, MealTestData.meals);
    }
}
