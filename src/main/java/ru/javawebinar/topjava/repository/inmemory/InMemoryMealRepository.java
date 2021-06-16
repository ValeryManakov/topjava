package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(1, meal));
    }

    @Override
    public boolean check(int userId, int id) {
        return userId == repository.get(id).getUserId();
    }

    @Override
    public Meal save(int userId, Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(SecurityUtil.authUserId());
            repository.put(meal.getId(), meal);
            return meal;
        }

        if (!check(userId, meal.getId())) return null;
        // handle case: update, but not present in storage
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int userId, int id) {
        if (!check(userId, id)) return false;
        return repository.remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        if (!check(userId, id)) return null;
        return repository.get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return repository.values().stream().filter(meal -> check(userId, meal.getId())).sorted(Comparator.comparing(Meal::getDate).reversed()).collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAll() {
        return repository.values().stream().sorted(Comparator.comparing(Meal::getDate).reversed()).collect(Collectors.toList());
    }
}

