package edu.ccrm.service;
import java.util.List;
import java.util.function.Predicate;

public interface Searchable<T> {
    List<T> search(Predicate<T> condition);
    T findById(String id);
    List<T> findAll();
}