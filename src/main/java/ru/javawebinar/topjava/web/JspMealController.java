package ru.javawebinar.topjava.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class JspMealController {

    @Autowired
    private MealService service;

    @GetMapping("/meals/delete")
    public String deleteMeal(HttpServletRequest request) {
        int userId = SecurityUtil.authUserId();
        service.delete(getId(request), userId);
        return "redirect:/meals";
    }

    @GetMapping("/meals/create")
    public String createMeal(HttpServletRequest request) {
        final Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
        request.setAttribute("meal", meal);
        return "mealForm";
    }

    @GetMapping("/meals/update")
    public String updateMeal(HttpServletRequest request) {
        int userId = SecurityUtil.authUserId();
        final Meal meal = service.get(getId(request), userId);
        request.setAttribute("meal", meal);
        return "mealForm";
    }

    @GetMapping("/meals")
    public String getMeals(HttpServletRequest request) {
        LocalDate startDate = request.getParameter("startDate") != null ? parseLocalDate(request.getParameter("startDate")) : null;
        LocalDate endDate = request.getParameter("endDate") != null ? parseLocalDate(request.getParameter("endDate")) : null;
        LocalTime startTime = request.getParameter("startTime") != null ? parseLocalTime(request.getParameter("startTime")) : null;
        LocalTime endTime = request.getParameter("endTime") != null ? parseLocalTime(request.getParameter("endTime")) : null;

        int userId = SecurityUtil.authUserId();
        List<Meal> mealsDateFiltered = service.getBetweenInclusive(startDate, endDate, userId);
        request.setAttribute("meals", MealsUtil.getFilteredTos(mealsDateFiltered, SecurityUtil.authUserCaloriesPerDay(), startTime, endTime));

        return "meals";
    }

    @PostMapping("/meals/save")
    public String saveMeal(HttpServletRequest request) throws IOException {
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                convertToUTF_8(request.getParameter("description")),
                Integer.parseInt(request.getParameter("calories")));

        int userId = SecurityUtil.authUserId();
        if (StringUtils.hasLength(request.getParameter("id"))) {
            assureIdConsistent(meal, getId(request));
            service.update(meal, userId);
        } else {
            checkNew(meal);
            service.create(meal, userId);
        }

        return "redirect:/meals";
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

    private String convertToUTF_8(String str) throws UnsupportedEncodingException {
        return new String(str.getBytes("ISO-8859-1"), "UTF-8");
    }
}
