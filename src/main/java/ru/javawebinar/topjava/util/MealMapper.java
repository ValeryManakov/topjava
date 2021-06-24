package ru.javawebinar.topjava.util;

import org.springframework.jdbc.core.RowMapper;
import ru.javawebinar.topjava.model.Meal;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MealMapper implements RowMapper<Meal> {
    @Override
    public Meal mapRow(ResultSet resultSet, int i) throws SQLException {
        Meal meal = new Meal();

        meal.setId(resultSet.getInt("id"));
        meal.setDateTime(resultSet.getTimestamp("dateTime").toLocalDateTime());
        meal.setDescription(resultSet.getString("description"));
        meal.setCalories(resultSet.getInt("calories"));

        return meal;
    }
}
