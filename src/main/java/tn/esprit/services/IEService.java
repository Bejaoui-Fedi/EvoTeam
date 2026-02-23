package tn.esprit.services;

import java.sql.SQLException;
import java.util.List;

public interface IEService<T> {
    public void insert(T t) throws SQLException;

    public void update(T t) throws SQLException;

    public void delete(int id) throws SQLException;

    List<T> show() throws SQLException;

    T getById(int id) throws SQLException;
}
