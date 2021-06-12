<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ru.javawebinar.topjava.util.MyDateTimeFormatter" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html lang="ru">
<head>
    <title>Meal list</title>
    <style>
        .red {
            color: red;
        }
        .green {
            color: green;
        }
        td {
            width: 150px;
        }
    </style>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<hr>
<h3><a href="meals?action=add">Add meal</a></h3>

<table border="1">
    <thead>
        <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Calories</th>
            <th></th>
            <th></th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="mealTo" items="${mealsTo}">
            <c:set var="excess" value="${mealTo.excess}" scope="request"/>
            <c:set var="dateTime" value="${mealTo.dateTime}" scope="request"/>
            <%
                String color;
                if ((Boolean) request.getAttribute("excess")) color = "red";
                else color = "green";
                MyDateTimeFormatter formatter = MyDateTimeFormatter.getFormatter();
                String dateTime = formatter.format((LocalDateTime) request.getAttribute("dateTime"));
            %>
            <tr class=<%= color%>>
                <td><%= dateTime%></td>
                <td>${mealTo.description}</td>
                <td>${mealTo.calories}</td>
                <td><a href="meals?action=edit&id=${mealTo.id}">Update</a></td>
                <td><a href="meals?action=delete&id=${mealTo.id}">Delete</a></td>
            </tr>
        </c:forEach>
    </tbody>
</table>

</body>
</html>