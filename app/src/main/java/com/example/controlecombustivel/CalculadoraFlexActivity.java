package com.example.controlecombustivel;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Locale;

public class CalculadoraFlexActivity extends AppCompatActivity {

    private TextInputEditText editPrecoGasolina, editPrecoEtanol;
    private Button btnComparar;
    private TextView textResultadoComparacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculadora_flex);

        editPrecoGasolina = findViewById(R.id.editPrecoGasolina);
        editPrecoEtanol = findViewById(R.id.editPrecoEtanol);
        btnComparar = findViewById(R.id.btnComparar);
        textResultadoComparacao = findViewById(R.id.textResultadoComparacao);

        btnComparar.setOnClickListener(v -> {
            String gasStr = editPrecoGasolina.getText().toString().trim();
            String etaStr = editPrecoEtanol.getText().toString().trim();

            if (gasStr.isEmpty() || etaStr.isEmpty()) {
                Toast.makeText(this, "Informe o valor de ambos os combustíveis!", Toast.LENGTH_SHORT).show();
                return;
            }

            double precoGasolina = Double.parseDouble(gasStr);
            double precoEtanol = Double.parseDouble(etaStr);

            // Regra dos 70%: se Preço Etanol / Preço Gasolina <= 0.70, compensa Etanol
            double razao = precoEtanol / precoGasolina;

            if (razao <= 0.70) {
                textResultadoComparacao.setText(String.format(Locale.getDefault(),
                        "Abasteça com ETANOL!\n(Relação: %.1f%% do preço da gasolina)", razao * 100));
            } else {
                textResultadoComparacao.setText(String.format(Locale.getDefault(),
                        "Abasteça com GASOLINA!\n(Relação: %.1f%% do preço da gasolina)", razao * 100));
            }
        });
    }
}