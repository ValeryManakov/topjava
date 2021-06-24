package ru.javawebinar.topjava.service;

import org.assertj.core.condition.Not;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThrows;

@ContextConfiguration("classpath:spring/spring-db.xml")
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void create() {
        Meal created = service.create(MealTestData.getNew(), UserTestData.USER_ID);
        Integer newId = created.getId();
        Meal newMeal = MealTestData.getNew();
        newMeal.setId(newId);
        MealTestData.assertMatch(created, newMeal);
        MealTestData.assertMatch(service.get(newId, UserTestData.USER_ID), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() {
        assertThrows(DataAccessException.class, () ->
                service.create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0)
                        , "Duplicate", 500), UserTestData.USER_ID));
    }

    @Test
    public void delete() {
        service.delete(MealTestData.USER_MEAL_ID, UserTestData.USER_ID);
        assertThrows(NotFoundException.class, () ->
                service.get(MealTestData.USER_MEAL_ID, UserTestData.USER_ID));
    }

    @Test
    public void deletedNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(MealTestData.NOT_FOUND, UserTestData.USER_ID));
    }

    @Test
    public void deletedAnotherUserMeal() {
        assertThrows(NotFoundException.class, () -> service.delete(MealTestData.ADMIN_MEAL_ID, UserTestData.USER_ID));
    }

    @Test
    public void get() {
        Meal meal = service.get(MealTestData.USER_MEAL_ID, UserTestData.USER_ID);
        MealTestData.assertMatch(meal, MealTestData.userMeal);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(MealTestData.NOT_FOUND, UserTestData.USER_ID));
    }

    @Test
    public void getAnotherUserMeal() {
        assertThrows(NotFoundException.class, () -> service.get(MealTestData.ADMIN_MEAL_ID, UserTestData.USER_ID));
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate startDate = LocalDate.of(2020, Month.JANUARY, 30);
        LocalDate endDate = LocalDate.of(2020, Month.JANUARY, 30);
        MealTestData.assertMatch(DataAccessUtils.singleResult(service.getBetweenInclusive(startDate, endDate, UserTestData.USER_ID)), MealTestData.userMeal);

        LocalDate newStartDate = LocalDate.of(2020, Month.JANUARY, 31);
        LocalDate newEndDate = LocalDate.of(2020, Month.JANUARY, 31);
        MealTestData.assertMatch(DataAccessUtils.singleResult(service.getBetweenInclusive(newStartDate, newEndDate, UserTestData.USER_ID)), null);
    }

    @Test
    public void update() {
        Meal updated = MealTestData.getUpdated();
        service.update(updated, UserTestData.USER_ID);
        MealTestData.assertMatch(service.get(MealTestData.USER_MEAL_ID, UserTestData.USER_ID), updated);
    }

    @Test
    public void updateNotFound() {
        Meal updated = MealTestData.getUpdated();
        updated.setId(MealTestData.NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updated, UserTestData.USER_ID));
    }

    @Test
    public void updateAnotherUserMeal() {
        Meal updated = MealTestData.getUpdated();
        assertThrows(NotFoundException.class, () -> service.update(updated, UserTestData.ADMIN_ID));
    }

    @Test
    public void getAll() {
        List<Meal> meals = service.getAll(UserTestData.USER_ID);
        MealTestData.assertMatch(meals, MealTestData.userMeal);
    }
}