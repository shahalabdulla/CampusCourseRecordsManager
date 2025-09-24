package edu.ccrm.service;

public interface Persistable<T> {
    void saveToFile(String filename) throws Exception;
    T loadFromFile(String filename) throws Exception;
    boolean validateData();
}
