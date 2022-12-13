package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.Constants.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO extends DatabaseHelper implements TransactionDAO {
    private List<Transaction> transactions;

    public PersistentTransactionDAO(Context context) {
        super(context);
        this.transactions = new ArrayList<>();
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("dd-MM-yyyy");

        cv.put(COLUMN_DATE,simpleDateFormat.format(date));
        cv.put(COLUMN_ACCOUNT_NUMBER,accountNo);
        cv.put(COLUMN_EXPENSE_TYPE,expenseType.toString());
        cv.put(COLUMN_AMOUNT,amount);

        database.insert(TRANSACTIONS_TABLE,null,cv);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        this.transactions = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();
        String querySQL = "SELECT * FROM " + TRANSACTIONS_TABLE + ";";
        Cursor cursor = database.rawQuery(querySQL,null);

        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("dd-MM-yyyy");
        if(cursor.moveToFirst()){
            do{
                Date date = null;
                try {
                    date = simpleDateFormat.parse(cursor.getString(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String accountNo = cursor.getString(2);
                ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(3).toUpperCase(Locale.ROOT));
                double amount = cursor.getDouble(4);

                Transaction newTransaction = new Transaction(date,accountNo,expenseType,amount);
                transactions.add(newTransaction);

            }
            while(cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        transactions = getAllTransactionLogs();

        int size = transactions.size();
        if( size > limit){
            return transactions.subList(size - limit, size);
        }else{
            return transactions;
        }
    }
}