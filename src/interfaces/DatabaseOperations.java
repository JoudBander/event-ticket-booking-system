package interfaces;

import java.sql.SQLException;
import java.util.ArrayList;


public interface DatabaseOperations<T> {

    
    void insert(T object) throws SQLException;

    void update(T object) throws SQLException;

    void delete(String id) throws SQLException;

    T findById(String id) throws SQLException;

    
    ArrayList<T> findAll() throws SQLException;
}
