/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.DAO;

import javafx.collections.ObservableList;

/**
 *
 * @author Tim Smith
 */
public interface CrudDao<T> {
    public ObservableList<?> getAll() throws DaoException, Exception;
    public T getById(int id) throws DaoException, Exception;
    public void insert(T dataObj) throws DaoException, Exception;
    public void update(T dataObj) throws DaoException, Exception;
    public void delete(T dataObj) throws DaoException, Exception;
}
