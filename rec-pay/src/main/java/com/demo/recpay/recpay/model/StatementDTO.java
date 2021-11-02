package com.demo.recpay.recpay.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
public class StatementDTO {
    private int transactionId;
    private User user;
    private String type;
    private Date transactionDate;
    private String description;
    private int amount;
    private int openingBal;
    private int closingBal;
}
