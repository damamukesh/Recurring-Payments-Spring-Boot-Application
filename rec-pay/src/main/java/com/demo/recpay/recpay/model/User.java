package com.demo.recpay.recpay.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @Column(unique = true)
    private String username;
    private String password;
    private int balance = 0;
    private boolean isActive = false;
    private String roles = "ROLE_USER";
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Transactions> transactionsList = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<RecurringPayments> recurringPayments = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }


    public List<Transactions> getTransactionsList() {
        return transactionsList;
    }

    public void addTransaction(Transactions transaction) {
        this.transactionsList.add(transaction);
    }

    public List<RecurringPayments> getRecurringPayments() {
        return recurringPayments;
    }

    public void addRecurring(RecurringPayments recurringPayment) {
        this.recurringPayments.add(recurringPayment);
    }

    public User(int id, String name, String username, String password, int balance, boolean isActive, String roles) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.isActive = isActive;
        this.roles = roles;
    }

    public User(){

    }

    //Jpa Transactions
    //Application Constants
}
