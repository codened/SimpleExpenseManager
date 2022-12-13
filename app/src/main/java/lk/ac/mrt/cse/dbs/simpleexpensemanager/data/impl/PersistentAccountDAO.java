package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.Constants.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO extends DatabaseHelper implements AccountDAO {
    private List<String> NumOfAccounts;
    private List<Account> accounts;

    public PersistentAccountDAO(@Nullable Context context) {

        super(context);
        this.NumOfAccounts = new ArrayList<>();
        this.accounts = new ArrayList<>();
    }

    @Override
    public List<String> getAccountNumbersList() {
        this.NumOfAccounts = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();
        String querySql = "SELECT " + COLUMN_ACCOUNT_NUMBER + " FROM " + ACCOUNTS_TABLE +";";
        Cursor cursor = database.rawQuery(querySql,null);

        if(cursor.moveToFirst()){
            do{
                NumOfAccounts.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }
        return NumOfAccounts;
    }

    @Override
    public List<Account> getAccountsList() {
        this.accounts = new ArrayList<>();
        String querySql = "SELECT * FROM " + ACCOUNTS_TABLE + ";";
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery(querySql,null);

        if(cursor.moveToFirst()){
            do{
                String accountNo = cursor.getString(1);
                String bankName = cursor.getString(2);
                String accountHolderName = cursor.getString(3);
                double balance = cursor.getDouble(4);

                Account newAccount = new Account(accountNo,bankName,accountHolderName,balance);
                accounts.add(newAccount);

            }while(cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase database = this.getReadableDatabase();
        String querySql = "SELECT * FROM " + ACCOUNTS_TABLE + " WHERE " + COLUMN_ACCOUNT_NUMBER + "= '" + accountNo +"' ;";
        Cursor cursor = database.rawQuery(querySql,null);


        String bankName = cursor.getString(2);
        String accountHolderName = cursor.getString(3);
        double balance = cursor.getDouble(4);

        Account account = new Account(accountNo,bankName,accountHolderName,balance);

        cursor.close();
        database.close();

        return account;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        this.NumOfAccounts = getAccountNumbersList();

        if(!NumOfAccounts.contains(account.getAccountNo())) {

            cv.put(COLUMN_ACCOUNT_NUMBER, account.getAccountNo());
            cv.put(COLUMN_BANK_NAME, account.getBankName());
            cv.put(COLUMN_ACCOUNT_HOLDER, account.getAccountHolderName());
            cv.put(COLUMN_ACCOUNT_BALANCE, account.getBalance());

            database.insert(ACCOUNTS_TABLE, null, cv);
            database.close();
        }

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase database = this.getWritableDatabase();

        database.execSQL("delete from accounts where accountNo = '"+ accountNo +"' ;");

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        SQLiteDatabase database = this.getWritableDatabase();
        String querySql = "SELECT " + COLUMN_ACCOUNT_BALANCE+ " FROM " + ACCOUNTS_TABLE + " WHERE " + COLUMN_ACCOUNT_NUMBER + "= '" + accountNo +"' ;";
        Cursor cursor = database.rawQuery(querySql,null);

        cursor.moveToFirst();
        double balance = cursor.getDouble(0);

        switch (expenseType){
            case INCOME:
                balance += amount;
                break;
            case EXPENSE:
                balance -= amount;
                break;
            default:
                break;
        }

        String updateQuerySql = "UPDATE " + ACCOUNTS_TABLE + " SET " + COLUMN_ACCOUNT_BALANCE + "= " + balance + " WHERE " + COLUMN_ACCOUNT_NUMBER + "= '" + accountNo +"' ;";
        database.execSQL(updateQuerySql);
        cursor.close();
        database.close();

    }
}