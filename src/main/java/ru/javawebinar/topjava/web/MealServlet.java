package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    private ConfigurableApplicationContext appCtx;
    private MealRestController controller;
    private UserService userService;

    @Override
    public void init() {
        log.info("Open application context");
        appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));
        controller = appCtx.getBean(MealRestController.class);
        userService = appCtx.getBean(UserService.class);
    }

    @Override
    public void destroy() {
        log.info("Close application context");
        appCtx.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                    LocalDateTime.parse(request.getParameter("dateTime")),
                    request.getParameter("description"),
                    Integer.parseInt(request.getParameter("calories")));
        meal.setUserId(SecurityUtil.authUserId());

        boolean isNew = meal.isNew();
        log.info(isNew ? "Create {}" : "Update {}", meal);
        if (isNew) controller.create(meal);
        else controller.update(meal, Integer.valueOf(id));
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                controller.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        controller.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "filter":
                log.info("getFiltered");

                String startTime = request.getParameter("startTime");
                String endTime = request.getParameter("endTime");
                String startDate = request.getParameter("startDate");
                String endDate = request.getParameter("endDate");

                LocalTime startLocalTime = startTime.equals("") ? null : LocalTime.parse(startTime);
                LocalTime endLocalTime = endTime.equals("") ? null : LocalTime.parse(endTime);
                LocalDate startLocalDate = startDate.equals("") ? null : LocalDate.parse(startDate);
                LocalDate endLocalDate = endDate.equals("") ? null : LocalDate.parse(endDate);

                request.setAttribute("meals",
                        controller.getFilteredTos(startLocalTime, endLocalTime, startLocalDate, endLocalDate));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                boolean isAdmin = userService.get(SecurityUtil.authUserId()).getRoles().contains(Role.ADMIN);
                request.setAttribute("meals",
                        isAdmin ? controller.getAllTos() : controller.getTos());
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
