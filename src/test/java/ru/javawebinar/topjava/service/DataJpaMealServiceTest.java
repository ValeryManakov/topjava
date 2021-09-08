package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.DataJpaActiveDbProfileResolver;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.MealTestData.MATCHER;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles(resolver = DataJpaActiveDbProfileResolver.class)
public class DataJpaMealServiceTest extends MealServiceTest {

    @Test
    public void getWithUser() {
        Meal meal = service.get(ADMIN_MEAL_ID, ADMIN_ID);
        User user = meal.getUser();
        MealTestData.MATCHER.assertMatch(meal, adminMeal1);
        UserTestData.MATCHER.assertMatch(user, admin);
    }
}
