package com.example.burak.ratecoindeneme.models;

import java.util.List;

public class TransactionModel {
    private int id;
    private int rateID;
    private int tx_index;
    private int nonce;
    private int totalcost;
    private String sender;
    private String receiver;
    private String amount;
    private String hash;
    private String Brand;

    public int getId() {return id;}
    public void setId(int id) {
        this.id = id;
    }

    public int getrateID() {return rateID;}
    public void setrateID(int id) {
        this.rateID = rateID;
    }

    public int getTx_index() {
        return tx_index;
    }
    public void setTx_index(int tx_index) { this.tx_index = tx_index;}

    public int getTotalcost() {return totalcost;}
    public void setTotalcost(int totalcost) {
        this.totalcost = totalcost;
    }

    public int getNonce() {return nonce;}
    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getHash(){return hash;}
    public void setHash(String hash){this.hash=hash;}

    public String getBrand(){return Brand;}
    public void setBrand(String Brand){this.Brand = Brand;}


}

