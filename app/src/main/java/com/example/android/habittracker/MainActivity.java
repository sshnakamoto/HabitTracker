package com.example.android.habittracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.habittracker.data.HabitContract.HabitEntry;
import com.example.android.habittracker.data.HabitDbHelper;

public class MainActivity extends AppCompatActivity {

    /* Variavel necessária para fazer o loop infito*/
    private int i = 0;
    /* Torna o Helper(Auxiliador Global (Acessivel por todos os métodos) )*/
    private HabitDbHelper helper;
    /* Cria um Array com 5 Strings */
    private String[] activitys = {"programar", "dançar", "cantar", "malhar", "estudar"};
    /* Cria um Array com 5 ints*/
    private int[] time = {60, 60, 30, 60, 60};
    /* Cria um TextView Global para mostrar informações sobre a database */
    private TextView dbInfo;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Encontra o FloatButton no XML e adiciona um Listener */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick( View v ) {
                /* Insere Dados na Database e Mostra no App */
                insertData();
                readData();
            }
        });

        /* Encontra o TextView no XML */
        dbInfo = findViewById(R.id.db_info);

        /* Inicializa um objeto auxiliador da database */
        helper = new HabitDbHelper(this);
    }

    /* Infla o Menu */
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /* Exibe as opções e chama os respectivos métodos */
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch (item.getItemId()){
            case R.id.empty_db:
            emptyDb();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Deleta e recria a database */
    private void emptyDb() {

        SQLiteDatabase db = helper.getReadableDatabase();
        db.execSQL(HabitDbHelper.SQL_DELETE_ENTRIES);
        db.execSQL(HabitDbHelper.SQL_CREATE_ENTRIES);

        dbInfo.setText("Dados da database: \n\n" );
    }

    @Override
    protected void onResume() {
        readData();
        super.onResume( );
    }

    /* Inseri dados na Database infinitamente */
    private void insertData(){

        /* Faz um Loop infinito para sempre ficar adicionando informações na database */
        if (i == 5) i = 0;

        /* Obtém a database */
        SQLiteDatabase db = helper.getReadableDatabase();

        /* Adiciona informações na database cada vez que é chamado */
        ContentValues values = new ContentValues();
        values.put(HabitEntry.COLUMN_HABIT_ACTIVITY, activitys[i]);
        values.put(HabitEntry.COLUMN_HABIT_TIME, time[i]);

        db.insert(HabitEntry.TABLE_NAME, null, values);
        i++;

    }

    /* Mostra Informações da database no app */
    private void readData(){

        Cursor cursor = helper.cursor();

        try {
            /* Encontra uma TextView no Layout para exibir dados da database */
            dbInfo.setText("Dados da database: \n\n" );
            dbInfo.append(
                            HabitEntry._ID + " || " +
                            HabitEntry.COLUMN_HABIT_ACTIVITY  + " || " +
                            HabitEntry.COLUMN_HABIT_TIME + "\n");

            /* Informa as indíces e as colunas em que o cursor deve se mover */
            int idColumnIndex = cursor.getColumnIndex(HabitEntry._ID);
            int activityColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_ACTIVITY);
            int timeColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_TIME);

            /* Executa e move o cursos para recuperar dados entre as linhas (rows) */
            while(cursor.moveToNext()){
                int currentID = cursor.getInt(idColumnIndex);
                String currentActivity = cursor.getString(activityColumnIndex);
                int currentTime = cursor.getInt(timeColumnIndex);

                /* Acrescenta informações da database ao aplicativo */
                dbInfo.append(
                        currentID + " || " +
                        currentActivity + " || " +
                        currentTime + "\n"
                );
            }

        } finally {
            /* Fecha o cursor para evitar desperdicios de memória */
            cursor.close();
        }
    }
}
