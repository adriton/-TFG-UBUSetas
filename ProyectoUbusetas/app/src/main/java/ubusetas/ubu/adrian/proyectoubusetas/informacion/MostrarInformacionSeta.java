package ubusetas.ubu.adrian.proyectoubusetas.informacion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ubusetas.ubu.adrian.proyectoubusetas.R;
import ubusetas.ubu.adrian.proyectoubusetas.basedatos.DBsetasManager;
import ubusetas.ubu.adrian.proyectoubusetas.interfaces.MostrarResultados;

/*
* @name: DBsetasManager
* @Author: Adrián Antón García
* @category: class
* @Description: Clase que muestra información relativa a la seta pulsada
* */

public class MostrarInformacionSeta extends AppCompatActivity implements View.OnClickListener{

    private String nombreSeta;
    private Bitmap fotoSeta;
    private Bitmap fotoBitmap;
    private int posImagenSeta;
    private String descripcionEs;
    private String comestibilidadEs;
    private String enlace;
    private String genero;
    private List<String> resultados;

    //elementos
    private ImageView imageViewSetaDescrita;
    private TextView textViewTextoDescripcionSeta;
    private TextView textViewTextoGeneroSeta;
    private TextView textViewTextoComestibilidadSeta;
    private TextView textViewTextoEnlaceSeta;
    private Button botonVolverMostrar;

    //Base de datos

    private DBsetasManager baseDatos;

    /*
    * @name: onCreate
    * @Author: Adrián Antón García
    * @category: procedure
    * @Description: Procedimiento que se ejecuta al iniciarse la actividad mostrarInformacionSeta,
    * inicializa todos las variables necesarias.
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_informacion_seta);

        //inicializo los elements de la interfaz

        botonVolverMostrar = (Button) findViewById(R.id.boton_volverMostrar);
        botonVolverMostrar.setOnClickListener(this);
        imageViewSetaDescrita = (ImageView) findViewById(R.id.imageView_setaDescrita);
        textViewTextoDescripcionSeta = (TextView) findViewById(R.id.textView_textoDescripcionSeta);
        textViewTextoGeneroSeta = (TextView) findViewById(R.id.textView_textoGeneroSeta);
        textViewTextoComestibilidadSeta = (TextView) findViewById(R.id.textView_textoComestibilidadSeta);
        textViewTextoEnlaceSeta = (TextView) findViewById(R.id.textView_textoEnlaceSeta);


        //recojo los datos provenientes de la actividad mostrar resultados

        Intent intentRecibidos = getIntent();
        Bundle datosRecibidos = intentRecibidos.getExtras();
        //recibo la información que llega de mostrar resultados
        nombreSeta= (String) datosRecibidos.get("nombreSeta");
        fotoSeta = (Bitmap) datosRecibidos.get("fotoSeta");
        fotoBitmap = (Bitmap) datosRecibidos.get("fotoBitmap");
        posImagenSeta = (int) datosRecibidos.get("posImagenSeta");
        resultados = (List<String>) datosRecibidos.get("resultados");


        //Coloco la imagen de la seta en su imageview
        imageViewSetaDescrita.setImageBitmap(fotoSeta);

        //Accedo a la base de datos

        baseDatos=new DBsetasManager(this);
        baseDatos.open();
        descripcionEs=baseDatos.getDescripcionEsp(nombreSeta);
        comestibilidadEs=baseDatos.getComestibilidadEs(nombreSeta);
        enlace = baseDatos.getEnlace(nombreSeta);
        genero = baseDatos.getGenero(nombreSeta);
        baseDatos.close();
        textViewTextoDescripcionSeta.setText(descripcionEs);
        textViewTextoGeneroSeta.setText(genero);
        textViewTextoComestibilidadSeta.setText(comestibilidadEs);
        textViewTextoEnlaceSeta.setText(enlace);
    }

    /*
    * @name: onClick
    * @Author: Adrián Antón García
    * @category: procedure
    * @Description: Procedimiento que se ejecuta al pinchar sobre algún botón
    * */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boton_volverMostrar: //Volver a la actividad mostrar resultados
                    Intent cambioActividad = new Intent(MostrarInformacionSeta.this, MostrarResultados.class);
                //devuelvo la info necesaria para cargar la clase mostrar resultados
                cambioActividad.putExtra("fotoBitmap", fotoBitmap);
                cambioActividad.putExtra("posImagenSeta", posImagenSeta);
                cambioActividad.putStringArrayListExtra("resultados", (ArrayList<String>) resultados);
                startActivity(cambioActividad);
            break;
        }
    }
}