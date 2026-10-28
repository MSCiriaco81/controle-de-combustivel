package com.example.controlecombustivel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ControleCombustivel.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABELA_ABASTECIMENTO = "abastecimentos";
    public static final String COL_ID = "id";
    public static final String COL_DATA = "data"; // formato YYYY-MM-DD
    public static final String COL_HODOMETRO = "hodometro";
    public static final String COL_LITROS = "litros";
    public static final String COL_TIPO = "tipo";
    public static final String COL_VALOR_LITRO = "valor_litro";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA_ABASTECIMENTO + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATA + " TEXT NOT NULL, " +
                COL_HODOMETRO + " REAL NOT NULL, " +
                COL_LITROS + " REAL NOT NULL, " +
                COL_TIPO + " TEXT NOT NULL, " +
                COL_VALOR_LITRO + " REAL NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_ABASTECIMENTO);
        onCreate(db);
    }

    public long salvarAbastecimento(String data, double hodometro, double litros, String tipo, double valorLitro) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DATA, data);
        cv.put(COL_HODOMETRO, hodometro);
        cv.put(COL_LITROS, litros);
        cv.put(COL_TIPO, tipo);
        cv.put(COL_VALOR_LITRO, valorLitro);
        return db.insert(TABELA_ABASTECIMENTO, null, cv);
    }

    // Busca o último abastecimento registrado antes do atual
    public Cursor buscarUltimoAbastecimento() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABELA_ABASTECIMENTO + " ORDER BY " + COL_HODOMETRO + " DESC LIMIT 1", null);
    }
}