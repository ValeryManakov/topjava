package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;

import java.util.Arrays;
import java.util.List;

public class UsersUtil {
    public static final List<User> users = Arrays.asList(
            new User(null, "Valery", "valera@mail.ru", "val", Role.USER),
            new User(null, "Ivan", "ivan@mail.ru", "iv", Role.USER),
            new User(null, "Vasya", "vasya@mail.ru", "vas", Role.USER),
            new User(null, "Alex", "alex@mail.ru", "al", Role.USER),
            new User(null, "John", "john@mail.ru", "j", Role.USER),
            new User(null, "Smith", "smith@mail.ru", "sm", Role.USER),
            new User(null, "Dima", "dima@mail.ru", "dim", Role.USER)
    );
}
