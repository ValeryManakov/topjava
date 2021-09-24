package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static final BeanPropertyRowMapper<User> USER_MAPPER = BeanPropertyRowMapper.newInstance(User.class);
    private static final RowMapper<Map.Entry<Integer, Role>> ROLE_MAPPER = new RowMapper<Map.Entry<Integer, Role>>() {
        @Override
        public Map.Entry<Integer, Role> mapRow(ResultSet rs, int rowNum) throws SQLException {
            int userId = rs.getInt("user_id");
            Role role = Role.valueOf(rs.getString("role"));
            return new AbstractMap.SimpleEntry(userId, role);
        }
    };

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        if (!constraintViolations.isEmpty()) throw new ConstraintViolationException(constraintViolations);

        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update(
                "UPDATE users SET name=:name, email=:email, password=:password, " +
                        "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id", parameterSource) == 0) {
            return null;
        }

        saveRoles(user.getId(), user.getRoles());
        return user;
    }

    private void saveRoles(int userId, Set<Role> roles) {
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", userId);

        List<Role> rolesList = new ArrayList<>();
        roles.stream().forEach(role -> rolesList.add(role));
        jdbcTemplate.batchUpdate("INSERT INTO user_roles (user_id, role) values(?,?)",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, userId);
                    ps.setString(2, rolesList.get(i).toString());
                }

                @Override
                public int getBatchSize() {
                    return rolesList.size();
                }
            });
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", USER_MAPPER, id);
        setRolesToUsers(getRoles(), users);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", USER_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", USER_MAPPER, email);
        setRolesToUsers(getRoles(), users);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users ORDER BY name, email", USER_MAPPER);
        setRolesToUsers(getRoles(), users);
        return users;
    }

    private void setRolesToUsers(Map<Integer, Set<Role>> roles, List<User> users) {
        users.stream().forEach(user -> user.setRoles(roles.get(user.getId())));
    }

    private Map<Integer, Set<Role>> getRoles() {
        List<Map.Entry<Integer, Role>> roleList = jdbcTemplate.query("SELECT * FROM user_roles ORDER BY user_id", ROLE_MAPPER);
        Map<Integer, Set<Role>> roleMap = new HashMap<>();
        roleList.stream().forEach(entry -> {
            int userId = entry.getKey();
            Role role = entry.getValue();
            roleMap.computeIfAbsent(userId, HashSet::new);
            roleMap.get(userId).add(role);
        });
        return roleMap;
    }
}
