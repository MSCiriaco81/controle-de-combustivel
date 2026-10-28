package com.example.controlecombustivel;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText editData, editHodometro, editLitros, editValorLitro;
    private RadioGroup rgCombustivel;
    private Button btnRegistrar, btnAbrirCalculadora;
    private TextView textResultados;
    private DbHelper dbHelper;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DbHelper(this);

        editData = findViewById(R.id.editData);
        editHodometro = findViewById(R.id.editHodometro);
        editLitros = findViewById(R.id.editLitros);
        editValorLitro = findViewById(R.id.editValorLitro);
        rgCombustivel = findViewById(R.id.rgCombustivel);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnAbrirCalculadora = findViewById(R.id.btnAbrirCalculadora);
        textResultados = findViewById(R.id.textResultados);

        // Define data atual como sugestão inicial
        editData.setText(dateFormat.format(new Date()));

        btnRegistrar.setOnClickListener(v -> processarRegistro());

        btnAbrirCalculadora.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalculadoraFlexActivity.class);
            startActivity(intent);
        });
    }

    private void processarRegistro() {
        String dataStr = editData.getText().toString().trim();
        String hodoStr = editHodometro.getText().toString().trim();
        String litrosStr = editLitros.getText().toString().trim();
        String valorLitroStr = editValorLitro.getText().toString().trim();

        if (dataStr.isEmpty() || hodoStr.isEmpty() || litrosStr.isEmpty() || valorLitroStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        double hodometroAtual = Double.parseDouble(hodoStr);
        double litrosAtuais = Double.parseDouble(litrosStr);
        double valorLitro = Double.parseDouble(valorLitroStr);

        int selectedId = rgCombustivel.getCheckedRadioButtonId();
        RadioButton rbSelecionado = findViewById(selectedId);
        String tipoCombustivel = rbSelecionado.getText().toString();

        // 1. Busca o registro imediatamente anterior para realizar o cálculo
        Cursor cursor = dbHelper.buscarUltimoAbastecimento();
        if (cursor != null && cursor.moveToFirst()) {
            double hodometroAnterior = cursor.getDouble(cursor.getColumnIndexOrThrow(DbHelper.COL_HODOMETRO));
            String dataAnteriorStr = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_DATA));
            cursor.close();

            if (hodometroAtual <= hodometroAnterior) {
                Toast.makeText(this, "O hodômetro deve ser maior que o anterior (" + hodometroAnterior + " km)!", Toast.LENGTH_LONG).show();
                return;
            }

            double distanciaPercorrida = hodometroAtual - hodometroAnterior;
            double consumoMedio = distanciaPercorrida / litrosAtuais; // km/l
            double custoTotalAbastecimento = litrosAtuais * valorLitro;
            double custoPorKm = custoTotalAbastecimento / distanciaPercorrida; // R$/km

            // Diferença de dias
            long dias = calcularDiferencaDias(dataAnteriorStr, dataStr);
            dias = Math.max(dias, 1); // evita divisão por zero se for no mesmo dia
            double kmPorDia = distanciaPercorrida / dias;

            String resultado = String.format(Locale.getDefault(),
                    "--- ANÁLISE DE CONSUMO ---\n" +
                            "Distância percorrida: %.1f km\n" +
                            "Consumo Médio: %.2f km/L\n" +
                            "Custo Efetivo: R$ %.2f / km\n" +
                            "Intervalo: %d dia(s)\n" +
                            "Média percorrida: %.1f km/dia",
                    distanciaPercorrida, consumoMedio, custoPorKm, dias, kmPorDia);

            textResultados.setText(resultado);
        } else {
            textResultados.setText("Primeiro abastecimento registrado com sucesso!\nO cálculo de consumo será gerado a partir do próximo abastecimento.");
        }

        // 2. Salva o registro no banco
        dbHelper.salvarAbastecimento(dataStr, hodometroAtual, litrosAtuais, tipoCombustivel, valorLitro);
        Toast.makeText(this, "Abastecimento salvo com sucesso!", Toast.LENGTH_SHORT).show();
    }

    private long calcularDiferencaDias(String dataIniStr, String dataFimStr) {
        try {
            Date dataIni = dateFormat.parse(dataIniStr);
            Date dataFim = dateFormat.parse(dataFimStr);
            long diffEmMillis = Math.abs(dataFim.getTime() - dataIni.getTime());
            return TimeUnit.DAYS.convert(diffEmMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            return 1;
        }
    }
}