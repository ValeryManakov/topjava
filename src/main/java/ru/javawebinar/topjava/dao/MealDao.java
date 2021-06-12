package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.List;
import java.util.stream.Collectors;

public class MealDao {
    private List<Meal> meals;

    public MealDao() {
        meals = MealsUtil.getAll();
    }

    public void addMeal(Meal meal) {
        meal.setId(++MealsUtil.ID);
        meals.add(meal);
    }

    public void deleteMeal(int id) {
        meals.remove(getMealById(id));
    }

    public void updateMeal(Meal meal) {
        Meal mealToUpdate = getMealById(meal.getId());
        mealToUpdate.setDateTime(meal.getDateTime());
        mealToUpdate.setDescription(meal.getDescription());
        mealToUpdate.setCalories(meal.getCalories());
    }

    public List<Meal> getAllMeals() {
        return meals;
    }

    public Meal getMealById(int id) {
        return meals.stream().filter(meal -> meal.getId() == id).collect(Collectors.toList()).get(0);
    }

    public List<MealTo> getAllMealsTo() {
        return MealsUtil.filteredByStreams(meals, null, null, MealsUtil.CALORIES_PER_DAY);
    }
}
