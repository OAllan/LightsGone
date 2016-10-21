package mx.itesm.lightsgone;

import java.util.Calendar;

/**
 * Created by allanruiz on 21/10/16.
 */
public class GameInfo {
    private int vida;
    private boolean pogo, capita, lanzapapas;
    private int mapa;
    private float x, y;
    private String nombre;
    private Calendar fecha;

    public GameInfo(){
        vida = 99;
        pogo = capita = lanzapapas = false;
        mapa = 0;
        x = 530;
        y=135;
    }

    public GameInfo(String juego){
        cargarJuego(juego);
    }

    private void cargarJuego(String juego) {

    }


}
