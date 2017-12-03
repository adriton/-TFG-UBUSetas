package ubusetas.ubu.adrian.proyectoubusetas.clavedicotomica;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import ubusetas.ubu.adrian.proyectoubusetas.R;
import ubusetas.ubu.adrian.proyectoubusetas.basedatos.AccesoDatosExternos;
import ubusetas.ubu.adrian.proyectoubusetas.lanzador.Lanzadora;
import ubusetas.ubu.adrian.proyectoubusetas.tarjetasClaves.AdaptadorTarjetasClaves;
import ubusetas.ubu.adrian.proyectoubusetas.tarjetasClaves.TarjetaClave;
import ubusetas.ubu.adrian.proyectoubusetas.clasificador.RecogerFoto;
import ubusetas.ubu.adrian.proyectoubusetas.informacion.MostrarSetas;

/*
* @name: MostrarClaves
* @Author: Adrián Antón García
* @category: clase
* @Description: Clase que muestra un listado de las claves dicotómicas de la aplicación.
* */

public class MostrarClaves extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AccesoDatosExternos acceso;

    //RecyclerView que va a contener las tarjetas de las claves
    private RecyclerView recyclerView;
    //Adaptador que va a enlazar las tarjetas con sus layout
    private AdaptadorTarjetasClaves adapter;
    //Lista con las tarjetas
    private ArrayList<TarjetaClave> listaTarjetasClaves = new ArrayList<>();
    //Array con los colores de las tarjetas
    private int[] colors;
    //Array con los nombres de las tarjetas
    private String[] names;

    /*
    * @name: onCreate
    * @Author: Adrián Antón García
    * @category: procedimiento
    * @Description: Procedimiento que se ejecuta cuando se carga la clase, inicializa los elementos
    * y los relaciona con el contexto.
    * @param: Bundle, Bundle donde se guardan los datos cuando se cierra la actividad.
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_claves);
        acceso = new AccesoDatosExternos(this);

        //cargamos la lista de setas y los colores
        names = getResources().getStringArray(R.array.nombres_claves);
        colors = getResources().getIntArray(R.array.initial_colors_mostrar_setas);

        //inicializamos las tarjetas
        inicializarTarjetas();

        //Creamos el adaptador entre las tarjetas y el layout de las tarjetas
        if (adapter == null) {
            adapter = new AdaptadorTarjetasClaves(this, listaTarjetasClaves);
        }

        //a que recycler esta adapatado esta actividad
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_lista_claves);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //parte del menu lateral
        Toolbar toolbar = (Toolbar) findViewById(R.id.barra_mostrar_claves);
        //cargamos la nueva barra
        setSupportActionBar(toolbar);

        //cargamos el layout del menu y lo inicializamos
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_mostrar_claves);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    /*
     * @name: inicializarTarjetas
     * @Author: Adrián Antón García
     * @category: Procedimiento
     * @Description: Procedimiento que inicializa las tarjetas de las claves
     * */

    private void inicializarTarjetas() {
        //40 tarjetas
        for (int i = 0; i < 41; i++) {
            //Inicializamos la tarjeta
            String nombreSeta = names[i];
            TarjetaClave card = new TarjetaClave();
            card.setId((long) i);
            card.setName(nombreSeta);
            card.setColorResource(colors[i]);
            listaTarjetasClaves.add(card);
        }
    }
    /*
    * @name: onCreate
    * @Author: Adrián Antón García
    * @category: procedimiento
    * @Description: Procedimiento que se ejectua cuando se pulsa el boton volver del movil.
    * */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_mostrar_claves);
        //si el menu esta abierto
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            //lo cerramos
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //si el menu esta cerrado llamamos al constructor padre
            super.onBackPressed();
        }
    }

    /*
    * @name: onNavigationItemSelected
    * @Author: Adrián Antón García
    * @category: Metodo
    * @Description: Metodo que se activa cuando pulsamos un botón del menú.
    * @Param: MenuItem, Item pulsado del menú.
    * */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_clasificar) {
            Intent cambioActividad = new Intent(MostrarClaves.this, RecogerFoto.class);
            startActivity(cambioActividad);
        } else if (id == R.id.menu_informacion) {
            Intent cambioActividad = new Intent(MostrarClaves.this, MostrarSetas.class);
            startActivity(cambioActividad);
        } else if (id == R.id.menu_home) {
            Intent cambioActividad = new Intent(MostrarClaves.this, Lanzadora.class);
            startActivity(cambioActividad);
        } else if (id == R.id.menu_idioma) {
            if (Locale.getDefault().getLanguage().equals("es")) {
                acceso.actualizarIdioma("en");
                Toast.makeText(this, "Language changed", Toast.LENGTH_LONG).show();
            } else {
                acceso.actualizarIdioma("es");
                Toast.makeText(this, "Idioma cambiado", Toast.LENGTH_LONG).show();
            }
            Intent intent = new Intent();
            intent.setClass(this, this.getClass());
            //llamamos a la actividad
            this.startActivity(intent);
            //finalizamos la actividad actual
            this.finish();
        }
        //Cerramos el menu lateral
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_mostrar_claves);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
