<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ru.javawebinar.topjava.util.MyDateTimeFormatter" %>
<%@ page import="ru.javawebinar.topjava.model.Meal" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html lang="ru">
<head>
    <link type="text/css"
          href="css/ui-lightness/jquery-ui-1.8.18.custom.css" rel="stylesheet" />
    <script type="text/javascript" src="js/jquery-1.7.1.min.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.8.18.custom.min.js"></script>
    <title>Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Edit meal</h2>
<%--<script>--%>
<%--    $(function() {--%>
<%--        $('input[name=dateTime]').datetimepicker();--%>
<%--    });--%>
<%--</script>--%>

<%
    MyDateTimeFormatter formatter = MyDateTimeFormatter.getFormatter();
    String dateTime = formatter.format(((Meal) request.getAttribute("meal")).getDateTime());
%>

<form method="POST" action='meals' name="Edit meal">
    <input type="hidden" name="id" value="<c:out value="${meal.id}" />" />
    DateTime : <input type="text" name="dateTime"
               value=<%= dateTime%> /> <br />
    Description : <input type="text" name="description"
                   value="<c:out value="${meal.description}" />" /> <br />
    Calories : <input type="text" name="calories"
                         value="<c:out value="${meal.calories}" />" /> <br />
        <input type="submit" value="Save" />
</form>
</body>
</html>