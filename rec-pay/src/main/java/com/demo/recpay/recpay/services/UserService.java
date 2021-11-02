package com.demo.recpay.recpay.services;

import com.demo.recpay.recpay.model.*;
import com.demo.recpay.recpay.repository.RecurringPaymentsRepository;
import com.demo.recpay.recpay.repository.TransactionsRepository;
import com.demo.recpay.recpay.repository.UserRepository;
import com.demo.recpay.recpay.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private RecurringPaymentsRepository recurringPaymentsRepository;

    @Autowired
    private TransactionsRepository transactionsRepository;

    @Autowired
    EntityManager em;

    @Autowired
    ModelMapper modelMapper;

    public List<User> getAllUsers(){
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public User getUser(int id){
        return userRepository.findById(id).get();
    }

    public void addUser(UserDTO userdTo){
        User user = modelMapper.map(userdTo, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void addUsers(List<User> al){
        al.stream()
                .forEach((user)-> {
                            user.setPassword(passwordEncoder.encode(user.getPassword()));
                            userRepository.save(user);
                        });
    }

    public String addMoney(int id, int amount){
        User user = userRepository.findById(id).get();
        int openingBal = user.getBalance();
        user.setBalance(user.getBalance() + amount);
        int closingBal = user.getBalance();
        Transactions transaction = new Transactions(user, "CR", "Money Added", amount, openingBal, closingBal, null);
        transactionsRepository.save(transaction);
        userRepository.save(user);
        return "Money Added";
    }

    public List<RecurringPayments> setupNewRecurringPayment(int id, RecurringPaymentsDTO recurringPaymentsDTO){
        RecurringPayments recurringPayment = modelMapper.map(recurringPaymentsDTO, RecurringPayments.class);
        User user = userRepository.findById(id).get();
        recurringPayment.setUser(user);
        recurringPaymentsRepository.save(recurringPayment);
        return recurringPaymentsRepository.findAll().stream().collect(Collectors.toList());
    }


    @Transactional
    public String transferMonthlyPayments(int id){
        User user = userRepository.findById(id).get();
        Query query = em.createNativeQuery("select * from recurring_payments where (user_id=? AND start_date<=?) AND (active=1 AND no_of_times>0)", RecurringPayments.class);
        query.setParameter(1, user);
        query.setParameter(2, new Date());
        List<RecurringPayments> recurringPaymentsList= query.getResultList();
        int flag=0;
        if(recurringPaymentsList.size()==0){
            return "No Pending Payments";
        }
        recurringPaymentsList.stream().forEach(
                (recurringPayment) -> {
                    int openingBal = user.getBalance();
                    if (openingBal >= recurringPayment.getAmount()) {
                    recurringPayment.setNoOfTimes(recurringPayment.getNoOfTimes() - 1);
                    if (recurringPayment.getNoOfTimes() == 0) {
                        recurringPayment.setActive(false);
                    }
                        user.setBalance(user.getBalance() - recurringPayment.getAmount());
                        int closingBal = user.getBalance();
                        Transactions transaction = new Transactions(user, "DB", recurringPayment.getDescription(), recurringPayment.getAmount(), openingBal, closingBal, recurringPayment);
//                        user.addRecurring(recurringPayment);
                        userRepository.save(user);
                        recurringPaymentsRepository.save(recurringPayment);
                        transactionsRepository.save(transaction);
                    }
                }
        );
        return "Paid";
    }

    public String removeRecurringPayment(int id, int recc){
        User user = userRepository.findById(id).get();
        Query query = em.createNativeQuery("select * from recurring_payments where user_id=? AND recc_id=?", RecurringPayments.class);
        query.setParameter(1, user);
        query.setParameter(2, recc);
        List<RecurringPayments> recurringPayments = (List<RecurringPayments>) query.getResultList();
        if(recurringPayments.size()==0){
            return "No Recurring Payment Found";
        }
        else{
            RecurringPayments recurringPayment = recurringPayments.get(0);
            recurringPayment.setActive(false);
            recurringPaymentsRepository.save(recurringPayment);
            return "Removed";
        }
    }

    public List<Transactions> getStatement(int id, String from, String to){
        User user = userRepository.findById(id).get();
        Query query = em.createNativeQuery("select * from transactions where transaction_date BETWEEN ? AND ? AND user_id=?", Transactions.class);
        query.setParameter(1, from);
        query.setParameter(2, to);
        query.setParameter(3, user);
        List<Transactions> transactionsList = query.getResultList();
        return transactionsList;
    }


//    public String isRecurring(int id, String description){
//        int electricityBill = 500;
//        int waterBill = 300;
//        int gasBill = 1000;
//        int amount=0;
//        switch(description){
//            case "electricity" : amount = electricityBill; break;
//            case "water" : amount = waterBill; break;
//            case "gas" : amount = gasBill;
//        }
//        User user = userRepository.getById(id);
//        if(user.getBalance()<amount){
//            return "Balance is insufficient";
//        }
//        else{
//            Query query = em.createNativeQuery("select * from recurring_payments where user_id=? AND description=? order by recc_id DESC LIMIT 1",RecurringPayments.class);
//            query.setParameter(1, user);
//            query.setParameter(2, description);
//            List<RecurringPayments> list = query.getResultList();
//            if(list.isEmpty()){
//                return setupNewRecurringPayment(user, description, amount);
//            }
//            RecurringPayments recurringPayments = list.get(0);
//            if(recurringPayments.isActive()){
//                int noOfTimes = recurringPayments.getNoOfTimes();
//                recurringPayments.setNoOfTimes(++noOfTimes);
//                recurringPaymentsRepository.save(recurringPayments);
//                return transferMonthlyPayments(user, description, amount, recurringPayments);
//            }
//            else{
//                return setupNewRecurringPayment(user, description, amount);
//            }
//        }
//    }


//    public String setupNewRecurringPayment(User user, String description, int amount){
//        RecurringPayments recurringPayments = new RecurringPayments(user, description, amount, 1);
//        recurringPaymentsRepository.save(recurringPayments);
//        return transferMonthlyPayments(user, description, amount, recurringPayments);
//    }

//    @Transactional
//    public String transferMonthlyPayments(User user, String description, int amount, RecurringPayments recc){
//        int openingBal = user.getBalance();
//        user.setBalance(user.getBalance()-amount);
//        int closingBal = user.getBalance();
//        Transactions transaction = new Transactions(user, "DB", description, amount, openingBal, closingBal, recc);
//        transactionsRepository.save(transaction);
//        userRepository.save(user);
//        return "Paid";
//    }
//public String removeRecurringPayment(int id, String description) {
//        User user = userRepository.findById(id).get();
//        Query query = em.createNativeQuery("select * from recurring_payments where user_id=? AND description=? ORDER BY recc_id LIMIT 1", RecurringPayments.class);
//        query.setParameter(1, user);
//        query.setParameter(2, description);
//        List<RecurringPayments> list = query.getResultList();
//        if (list.isEmpty()) {
//            return "No Recurring Payment Found";
//        }
//        RecurringPayments recurringPayments = list.get(0);
//        if (recurringPayments.isActive()) {
//            recurringPayments.setActive(false);
//            recurringPaymentsRepository.save(recurringPayments);
//            return "Recurring Payment Removed";
//        } else {
//            return "No Active Recurring Payment Found";
//        }
//    }

}
