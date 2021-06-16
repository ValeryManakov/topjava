package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealRepository {
    //false if meal do not belong to userId
    boolean check(int userId, int id);

    // null if updated meal do not belong to userId
    Meal save(int userId, Meal meal);

    // false if meal do not belong to userId
    boolean delete(int userId, int id);

    // null if meal do not belong to userId
    Meal get(int userId, int id);

    // ORDERED dateTime desc
    List<Meal> getAll(int userId);

    // all meals
    List<Meal> getAll();
}
