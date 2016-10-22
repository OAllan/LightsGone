package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by allanruiz on 21/10/16.
 */
public class GameInfo {
    private int vida;
    private boolean pogo, capita, lanzapapas;
    private int mapa;
    private float x, y, camaraX, camaraY;
    private String nombre;
    private Calendar fecha;
    private Abner abner;

    public GameInfo(){
        vida = 99;
        pogo = capita = lanzapapas = false;
        mapa = 0;
        x = 530;
        y=135;
        nombre = nombre();
        camaraX = 640;
        camaraY = 400;
    }

    public GameInfo(String juego) {
        cargarJuego(juego);
        nombre = juego;
    }

    public void setAbner(Abner abner){
        this.abner = abner;
    }

    private void cargarJuego(String juego) {

        try{
            FileHandle fileHandle;
            fileHandle = Gdx.files.local(juego);
            String archivo = fileHandle.readString();
            String[] lineas = archivo.split("\n");
            mapa = Integer.parseInt(lineas[1].trim());
            x = Float.parseFloat(lineas[2].trim());
            y = Float.parseFloat(lineas[3].trim());
            vida = Integer.parseInt(lineas[4].trim());
            pogo = Boolean.parseBoolean(lineas[5].trim());
            lanzapapas = Boolean.parseBoolean(lineas[6].trim());
            capita = Boolean.parseBoolean(lineas[7].trim());
            camaraX = Float.parseFloat(lineas[8].trim());
            camaraY = Float.parseFloat(lineas[9].trim());

        }
        catch (Exception e){
            Gdx.app.log("Exception ", e.getMessage());
        }

    }

    public void guardarJuego(){
        actualizarDatos();
        fecha = new GregorianCalendar();
        try{
            FileHandle save = Gdx.files.local(nombre);
            save.writeString(fecha.get(Calendar.DAY_OF_MONTH) + "/" + fecha.get(Calendar.MONTH) + "/" + fecha.get(Calendar.YEAR) + "\n", false);
            save.writeString(mapa+"\n",true);
            save.writeString(x + "\n", true);
            save.writeString(y + "\n", true);
            save.writeString(vida + "\n", true);
            save.writeString(pogo + "\n", true);
            save.writeString(lanzapapas + "\n", true);
            save.writeString(capita + "\n", true);
            save.writeString(camaraX + "\n", true);
            save.writeString(camaraY + "\n", true);
        }
        catch (Exception e){
            Gdx.app.log("Exception ", e.getMessage());
        }

    }

    private void actualizarDatos() {
        mapa = Nivel0.mapaActual;
        pogo = abner.getPogo();
        capita = abner.getCapita();
        lanzapapas = abner.getLanzapapas();
        x = abner.getX();
        y = abner.getY();
        camaraX = abner.getCamaraX();
        camaraY = abner.getCamaraY();
    }

    private String nombre(){
        String nombre = "Juego";
        Character numero = null;
        try {
            String path = Gdx.files.getLocalStoragePath();
            FileHandle directorio = new FileHandle(path);
            ArrayList<FileHandle> files = new ArrayList<FileHandle>(directorio.list().length);
            for(FileHandle file: directorio.list())
                files.add(file);

            Iterator<FileHandle> iterator = files.iterator();
            while(iterator.hasNext()){
                FileHandle file = iterator.next();
                String extension = file.extension();
                if(!"txt".equalsIgnoreCase(extension))
                    iterator.remove();
            }


            if(files.size()==1){
                String name = files.get(0).name();
                numero = name.charAt(5);
            }
            else if(files.size()==0)
                numero = '2';

        }catch (Exception e){

        }

        if(numero!=null){
            if(numero.equals(new Character('1')))return nombre + "2.txt";
            else return nombre + "1.txt";
        }


        return null;
    }

    public static void borrarJuego(String file) {
        Gdx.files.local(file).delete();
    }


    public int getVida() {
        return vida;
    }

    public boolean isPogo() {
        return pogo;
    }

    public boolean isCapita() {
        return capita;
    }

    public boolean isLanzapapas() {
        return lanzapapas;
    }

    public int getMapa() {
        return mapa;
    }

    public float getY() {
        return y;
    }

    public float getX() {

        return x;
    }

    public float getCamaraX(){
        return camaraX;
    }

    public float getCamaraY(){
        return camaraY;
    }
}
