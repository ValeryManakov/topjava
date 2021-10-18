package ru.javawebinar.topjava.web.meal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@RestController
@RequestMapping(value = "/profile/meals", produces = MediaType.APPLICATION_JSON_VALUE)
public class MealUIController extends AbstractMealController {

    @Override
    @GetMapping
    public List<MealTo> getAll() {
        return super.getAll();
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void create(@RequestParam String dateTime,
                       @RequestParam String description,
                       @RequestParam String calories) {
        super.create(new Meal(null, LocalDateTime.parse(dateTime), description, Integer.parseInt(calories)));
    }

    @GetMapping("/filter")
    public  List<MealTo> getBetween(@RequestParam String startDate,
                                    @RequestParam String startTime,
                                    @RequestParam String endDate,
                                    @RequestParam String endTime) {

        return super.getBetween(DateTimeUtil.parseLocalDate(startDate),
                                DateTimeUtil.parseLocalTime(startTime),
                                DateTimeUtil.parseLocalDate(endDate),
                                DateTimeUtil.parseLocalTime(endTime));
    }
}
