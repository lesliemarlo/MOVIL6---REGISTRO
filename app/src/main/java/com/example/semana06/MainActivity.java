package com.example.semana06;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.semana06.entity.Categoria;
import com.example.semana06.entity.Libro;
import com.example.semana06.entity.Pais;
import com.example.semana06.service.ServiceCategoriaLibro;
import com.example.semana06.service.ServiceLibro;
import com.example.semana06.service.ServicePais;
import com.example.semana06.util.ConnectionRest;
import com.example.semana06.util.FunctionUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //Pais
    //Aquí declaramos un Spinner que nos servira para seleccionar un pais
    Spinner spnPais;
    ArrayAdapter<String> adaptadorPais; //para mostrar la lista de paises

    //Se declara una lista de países que
    // se utilizará para almacenar
    // los datos de los países que se obtienen
    // de la base de datos remota.
    ArrayList<String> paises = new ArrayList<>();

    //Categoria
    Spinner spnCategoria;
    ArrayAdapter<String> adaptadorCategoria;
    ArrayList<String> categorias = new ArrayList<>();

    //Servicio
    ServiceLibro serviceLibro;
    ServicePais servicePais;
    ServiceCategoriaLibro serviceCategoriaLibro;
    //Boton
    Button btnRegistra;

    EditText txtTitulo, txtAnio, txtSerie;
    //-- solo para campos de texto donde se ingresa informacion
    //--


    @Override //Aquí se realiza la configuración
    // inicial de la interfaz de usuario y se inicializan
    // los objetos y variables necesarios
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //ADAPTERS -- aqu se inicializan los adaptadores para el spinner
        adaptadorPais = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, paises);
        spnPais = findViewById(R.id.spnRegLibPais);
        spnPais.setAdapter(adaptadorPais);

        adaptadorCategoria = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categorias);
        spnCategoria = findViewById(R.id.spnRegLibCategoria);
        spnCategoria.setAdapter(adaptadorCategoria);

        // se inicializa el servicio para onbtener los datos del serv rest
        servicePais = ConnectionRest.getConnection().create(ServicePais.class);
        serviceLibro = ConnectionRest.getConnection().create(ServiceLibro.class);
        serviceCategoriaLibro = ConnectionRest.getConnection().create(ServiceCategoriaLibro.class);
//se hace llamado a un metodo psr la carga de lista de los spinners
        cargaPais();
        cargaCategoria();

//--se inicializan lso campos de texto
        txtTitulo = findViewById(R.id.txtRegLibTitulo);
        txtAnio = findViewById(R.id.txtRegLibAnio);
        txtSerie = findViewById(R.id.txtRegLibSerie);

        //-- se inicializa el boton registra
        btnRegistra = findViewById(R.id.btnRegLibEnviar);
//----
        //para cuando le des clicl al boton y se ejecutara el codigo
        btnRegistra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REGISTRO
                //obtiene los datos ingresados/seleccionados y los guarda en las variables locales
                String titulo = txtTitulo.getText().toString();
                String anio = txtAnio.getText().toString();
                String serie = txtSerie.getText().toString();
                String idPais = spnPais.getSelectedItem().toString().split(":")[0];
                String idCategoria = spnCategoria.getSelectedItem().toString().split(":")[0];

                // Se crea una nueva instancia de la clase
                Pais objPais = new Pais();
                //etablece el id en objPais
                //y lo convierte a un entero
                objPais.setIdPais(Integer.parseInt(idPais.trim()));

                Categoria objCategoria = new Categoria();
                objCategoria.setIdCategoria(Integer.parseInt(idCategoria.trim()));

                //--se crea una instancia de la clase libro
                //DATO: al instanciar clases estas creando objetos reales a los que
                // puedes asignar valores a sus atributos y esta instancia te permite trabajar con el objeto/creacion de una copia real
                Libro objLibro = new Libro();
                //se establecen los datos al objeto libro
                //el setter asigna valores a un atributo de un objeto
                objLibro.setTitulo(titulo);
                objLibro.setAnio(Integer.parseInt(anio));
                objLibro.setSerie(serie);
                objLibro.setPais(objPais);
                objLibro.setCategoria(objCategoria);
                objLibro.setFechaRegistro(FunctionUtil.getFechaActualStringDateTime());
                objLibro.setEstado(1);

                registra(objLibro);
//-- los datos se han tomado y estan esperando ser mandados al servidor rest
            }
        });
    }

    void cargaPais() {
        //solicitud para obtener una lista
        Call<List<Pais>> call = servicePais.listaTodos();
        call.enqueue(new Callback<List<Pais>>() {
            @Override
            public void onResponse(Call<List<Pais>> call, Response<List<Pais>> response) {
                if (response.isSuccessful()) {
                    List<Pais> lst = response.body(); //SI ES CORRECTO TRAE LA DATA
                    for (Pais obj : lst) {
                        paises.add(obj.getIdPais() + " : " + obj.getNombre());
                    }
                    adaptadorPais.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Pais>> call, Throwable t) {

            }
        });
    }

    void cargaCategoria() {
        Call<List<Categoria>> call = serviceCategoriaLibro.listaTodos();
        call.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                if (response.isSuccessful()) {
                    List<Categoria> lst = response.body();
                    for (Categoria obj : lst) {
                        categorias.add(obj.getIdCategoria() + " : " + obj.getDescripcion());
                    }
                    adaptadorCategoria.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {

            }
        });
    }

    //BOTON
    void registra(Libro obj) {
        Call<Libro> call = serviceLibro.registra(obj);
        call.enqueue(new Callback<Libro>() {
            @Override
            public void onResponse(Call<Libro> call, Response<Libro> response) {
                if (response.isSuccessful()) {
                    Libro objSalida = response.body();
                    mensajeAlert(" Registro de Libro exitoso:  "
                            + " \n >>>> ID >> " + objSalida.getIdLibro()
                            + " \n >>> Título >>> " + objSalida.getTitulo());
                }
            }


            @Override
            public void onFailure(Call<Libro> call, Throwable t) {

            }
        });

    }

    //mensajes

    void mensajeToast(String mensaje) {
        Toast toast1 = Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG);
        toast1.show();
    }


    //alert
    public void mensajeAlert(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(true);
        alertDialog.show();
    }
}