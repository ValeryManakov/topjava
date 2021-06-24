package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealMapper;
import ru.javawebinar.topjava.util.Util;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class JdbcMealRepository implements MealRepository {

    private static final RowMapper<Meal> ROW_MAPPER = new MealMapper();

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertMeal;

    @Autowired
    public JdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertMeal = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("meals")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("user_id", userId)
                .addValue("dateTime", meal.getDateTime())
                .addValue("description", meal.getDescription())
                .addValue("calories", meal.getCalories());
        if (meal.isNew()) {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update("INSERT INTO meals (user_id, dateTime, description, calories) " +
                    "VALUES(:user_id, :dateTime, :description, :calories)", map, keyHolder);
            meal.setId((Integer) keyHolder.getKeyList().get(0).get("id"));
        } else if (namedParameterJdbcTemplate.update(
                    "UPDATE meals SET dateTime=:dateTime, description=:description" +
                            ", calories=:calories WHERE id=:id and user_id=:user_id", map) == 0) return null;
        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        return jdbcTemplate.update("DELETE FROM meals WHERE id=? and user_id=?", id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        List<Meal> meals = jdbcTemplate.query("SELECT * FROM meals WHERE id=? and user_id=?", ROW_MAPPER, id, userId);
        return DataAccessUtils.singleResult(meals);
    }

    @Override
    public List<Meal> getAll(int userId) {
        List<Meal> meals = jdbcTemplate.query("SELECT * FROM meals WHERE user_id=?", ROW_MAPPER, userId);
        return meals.stream().sorted(Comparator.comparing(Meal::getDateTime).reversed()).collect(Collectors.toList());
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        List<Meal> meals = getAll(userId);
        return meals == null ? Collections.emptyList() :
                meals.stream()
                     .filter(meal -> Util.isBetweenHalfOpen(meal.getDateTime(), startDateTime, endDateTime))
                     .collect(Collectors.toList());
    }
}
