package ru.javawebinar.topjava.repository.jdbc;

import javafx.util.converter.LocalDateStringConverter;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.Util;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Profile("hsqldb")
public class HsqldbJdbcMealRepository extends JdbcMealRepository {

    public HsqldbJdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        List<Meal> meals = getAll(userId);
        return meals.stream().filter(meal -> Util.isBetweenHalfOpen(meal.getDateTime(), startDateTime, endDateTime)).collect(Collectors.toList());
    }

    @Override
    protected MapSqlParameterSource getMapSqlParameterSource(Meal meal, int userId) {
        LocalDateTime localDateTime = meal.getDateTime();
        Date date = DateTimeUtil.getDate(localDateTime);

        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("description", meal.getDescription())
                .addValue("calories", meal.getCalories())
                .addValue("date_time", date)
                .addValue("user_id", userId);

        return map;
    }
}
