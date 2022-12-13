package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.Constants.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context) {
        super(context, "200322R.db", null,  1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createAccountsTableStatement = "CREATE TABLE " + ACCOUNTS_TABLE + "(" + COLUMN_ACCOUNT_NUMBER + " text PRIMARY KEY, " + COLUMN_BANK_NAME + " text, " + COLUMN_ACCOUNT_HOLDER + " text, " + COLUMN_ACCOUNT_BALANCE + " real);";
        sqLiteDatabase.execSQL(createAccountsTableStatement);

        String createTransactionsTableStatement = "CREATE TABLE " + TRANSACTIONS_TABLE + "(" + COLUMN_TRANSACTION_NUMBER + " integer PRIMARY KEY AUTOINCREMENT, " + COLUMN_DATE + " text, " + COLUMN_ACCOUNT_NUMBER + " text, " + COLUMN_EXPENSE_TYPE + " text, " + COLUMN_AMOUNT + " real);";
        sqLiteDatabase.execSQL(createTransactionsTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}