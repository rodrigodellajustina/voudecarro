package br.edu.unisep.voudecarro;


import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.EditText;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import java.util.Locale;
import android.content.ContentValues;

public class MainActivity extends AppCompatActivity {

    private EditText editValorPagoViagem;
    private EditText editValorMediaVeiculo;
    private EditText editKmViagem;
    private EditText editQtdPassageiros;
    private EditText editValorLitroGasolina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mapeando elementos do meu front end através da classe R
        //com elementos do meu back end as variáveis da classe.
        editValorPagoViagem    = (EditText)findViewById(R.id.edtValorViagem);
        editValorMediaVeiculo  = (EditText)findViewById(R.id.edtMediaVeiculo);
        editKmViagem           = (EditText)findViewById(R.id.edtKmViagem);
        editQtdPassageiros     = (EditText)findViewById(R.id.edtQuantidadePassageiros);
        editValorLitroGasolina = (EditText)findViewById(R.id.edtValorLitroGasolina);

        //Comunicação com SQLite
        SQLiteDatabase meubd;
        // Criamos o arquivo de armazenamento e abrimos uma conexão.
        meubd = openOrCreateDatabase("voudecarro.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

        //setando versão do banco de dados...
        meubd.setVersion(1);
        //setand a locaização do banco de dados...
        meubd.setLocale(Locale.getDefault());
        // criação da tabela tb_info
        final String CREATE_TABLES = "create table if not exists tb_info " +
                                     "(valorpago numeric, mediaveiculo numeric, " +
                                     "kmviagem numeric, qtdpassageiros int , " +
                                     "valorlitrogasolina numeric);";

        //criando a tabela tb_info no banco de dados
        meubd.execSQL(CREATE_TABLES);
    }


    public void Calcular(View view) {
        // declaração do tipo do float
        Float fValorPagoViagem;
        Float fValorMediaVeiculo;
        Float fValorKmViagem;
        Float fQuantidadePassageiros;
        Float fValorLitroGasolina;

        // Valores informados  na tela
        fValorPagoViagem       = Float.parseFloat(editValorPagoViagem.getText().toString());
        fValorMediaVeiculo     = Float.parseFloat(editValorMediaVeiculo.getText().toString());
        fValorKmViagem         = Float.parseFloat(editKmViagem.getText().toString());
        fQuantidadePassageiros = Float.parseFloat(editQtdPassageiros.getText().toString());
        fValorLitroGasolina    = Float.parseFloat(editValorLitroGasolina.getText().toString());

        // declaramos e inicializarmos variáveis de calculo
        Float fQuantidadeCombustivelViagem;
        fQuantidadeCombustivelViagem = Float.parseFloat("0.00");
        Float fValorGasolinaGastoViagem;
        fValorGasolinaGastoViagem = Float.parseFloat("0.00");
        Float fValorRateioPassageiros;
        fValorRateioPassageiros = Float.parseFloat("0.00");

        //calcular rateio
        fQuantidadeCombustivelViagem = (fValorKmViagem/fValorMediaVeiculo);
        fValorGasolinaGastoViagem    = (fQuantidadeCombustivelViagem * fValorLitroGasolina);
        fValorRateioPassageiros      = (fValorGasolinaGastoViagem / fQuantidadePassageiros);

        AlertDialog.Builder msg_compensa = new AlertDialog.Builder(this);
        msg_compensa.setTitle("Informação");
        msg_compensa.setNeutralButton("Ok", null);

        if(fValorRateioPassageiros > fValorPagoViagem){
            msg_compensa.setMessage("Compensa ir Coletivo");
        }else{
            msg_compensa.setMessage("Compensa ir de Carro");
        }

        msg_compensa.show();

        //Alimentar o banco de dados..
        SQLiteDatabase meubd;
        meubd = openOrCreateDatabase("voudecarro.db", SQLiteDatabase.CREATE_IF_NECESSARY,null);

        meubd.delete("tb_info", "1=1", null);

        //Atribuindo valores as colunas do banco de dados
        ContentValues novosvalores  = new ContentValues();
        novosvalores.put("valorpago", fValorPagoViagem);
        novosvalores.put("mediaveiculo", fValorMediaVeiculo);
        novosvalores.put("kmviagem", fValorKmViagem);
        novosvalores.put("qtdpassageiros", fQuantidadePassageiros);
        novosvalores.put("valorlitrogasolina", fValorLitroGasolina);
        //inserir os valores no banco de dados
        meubd.insert("tb_info", null, novosvalores);

    }


    public void Limpa(View view){
        //método que irá limpar os valores na tela
        editValorPagoViagem.setText("");
        editValorMediaVeiculo.setText("");
        editKmViagem.setText("");
        editQtdPassageiros.setText("");
        editValorLitroGasolina.setText("");
        editValorPagoViagem.requestFocus();
    }

    public void Carrega(View view){
        //método que irá carregar os últimos valores inseridos na tela.

        // abrir conexão com banco de dados
        SQLiteDatabase meubd;
        meubd = openOrCreateDatabase("voudecarro.db", SQLiteDatabase.CREATE_IF_NECESSARY,null);

        //realizar a seleção dos dados na tabela tb_info e armazenar em um cursor
        Cursor meucursor = meubd.rawQuery("select * from tb_info", null);
        meucursor.moveToNext();

        //setar os valores no cursor diretamente para tela.
        editValorPagoViagem.setText(meucursor.getString(meucursor.getColumnIndex("valorpago")));
        editValorMediaVeiculo.setText(meucursor.getString(meucursor.getColumnIndex("mediaveiculo")));
        editKmViagem.setText(meucursor.getString(meucursor.getColumnIndex("kmviagem")));
        editQtdPassageiros.setText(meucursor.getString(meucursor.getColumnIndex("qtdpassageiros")));
        editValorLitroGasolina.setText(meucursor.getString(meucursor.getColumnIndex("valorlitrogasolina")));
    }
}