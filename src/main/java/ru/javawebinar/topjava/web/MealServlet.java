package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MyDateTimeFormatter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final long serialVersionUID = 1L;
    private static String INSERT_OR_EDIT = "/meal.jsp";
    private static String LIST_MEAL = "/meals.jsp";
    private MealDao dao;

    public MealServlet() {
        super();
        dao = new MealDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("doGet");
        String loggerMessage = "";
        String forward="";
        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            loggerMessage = "forward to meals";
            forward = LIST_MEAL;
            request.setAttribute("mealsTo", dao.getAllMealsTo());
        } else if (action.equalsIgnoreCase("delete")){
            loggerMessage = "delete meal, forward to meals";
            int id = Integer.parseInt(request.getParameter("id"));
            dao.deleteMeal(id);
            forward = LIST_MEAL;
            request.setAttribute("mealsTo", dao.getAllMealsTo());
        } else if (action.equalsIgnoreCase("edit")){
            loggerMessage = "edit meal, forward to meal";
            forward = INSERT_OR_EDIT;
            int id = Integer.parseInt(request.getParameter("id"));
            Meal meal = dao.getMealById(id);
            request.setAttribute("meal", meal);
        } else if (action.equalsIgnoreCase("listMeal")){
            loggerMessage = "listMeal, forward to meals";
            forward = LIST_MEAL;
            request.setAttribute("mealsTo", dao.getAllMealsTo());
        } else {
            loggerMessage = "forward to meal";
            forward = INSERT_OR_EDIT;
            request.setAttribute("meal", new Meal());
        }

        log.debug(loggerMessage);
        RequestDispatcher view = request.getRequestDispatcher(forward);
        view.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("doPost");
        String loggerMessage = "";

        Meal meal = new Meal();

        request.setCharacterEncoding("UTF-8");
        meal.setDescription(request.getParameter("description"));

        meal.setCalories(Integer.parseInt(request.getParameter("calories")));

        LocalDateTime dateTime = MyDateTimeFormatter.getFormatter().parse(request.getParameter("dateTime"));
        meal.setDateTime(dateTime);

        String id = request.getParameter("id");
        if(id == null || id.isEmpty() || Integer.parseInt(id) == 0) {
            loggerMessage = "add meal, forward to meals";
            dao.addMeal(meal);
        } else {
            loggerMessage = "update meal, forward to meals";
            meal.setId(Integer.parseInt(id));
            dao.updateMeal(meal);
        }

        log.debug(loggerMessage);
        RequestDispatcher view = request.getRequestDispatcher(LIST_MEAL);
        request.setAttribute("mealsTo", dao.getAllMealsTo());
        view.forward(request, response);
    }
}