package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;

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
        File txt;
        Scanner scn=null;
        try{
            txt = new File(juego);
            scn= new Scanner(txt);
            scn.nextLine();
            mapa = Integer.parseInt(scn.nextLine().trim());
            x = Float.parseFloat(scn.nextLine().trim());
            y = Float.parseFloat(scn.nextLine().trim());
            vida = Integer.parseInt(scn.nextLine().trim());
            pogo = Boolean.parseBoolean(scn.nextLine().trim());
            lanzapapas = Boolean.parseBoolean(scn.nextLine().trim());
            capita = Boolean.parseBoolean(scn.nextLine().trim());
            camaraX = Float.parseFloat(scn.nextLine().trim());
            camaraY = Float.parseFloat(scn.nextLine().trim());
        }
        catch (Exception e){
            Gdx.app.log("Exception ", e.getMessage());
        }
        finally {
            if(scn!= null)
                scn.close();
        }
    }

    public void guardarJuego(){
        actualizarDatos();
        File txt = null;
        FileWriter save = null;
        fecha = new GregorianCalendar();
        try{
            txt = new File(nombre);
            save = new FileWriter(txt);
            save.write(fecha.get(Calendar.DAY_OF_MONTH) + "/"+fecha.get(Calendar.MONTH)+"/"+fecha.get(Calendar.YEAR)+"\n");
            save.write(mapa+"\n");
            save.write(x+"\n");
            save.write(y+"\n");
            save.write(vida+"\n");
            save.write(pogo+"\n");
            save.write(lanzapapas+"\n");
            save.write(capita+"\n");
            save.write(camaraX+"\n");
            save.write(camaraY +"\n");
            save.close();
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
            File archivo = new File(".");
            File directorio = new File(archivo.getCanonicalPath());
            ArrayList<File> files = new ArrayList<File>(directorio.listFiles().length);
            for(File file: directorio.listFiles())
                files.add(file);

            Iterator<File> iterator = files.iterator();
            while(iterator.hasNext()){
                File file = iterator.next();
                String name = file.getName();
                if(name.length()>3)
                    if(!name.substring(name.length()-3, name.length()).equalsIgnoreCase("txt")){
                        iterator.remove();
                    }
            }

            if(files.size()==2)
                borrarJuego(files);
            else if(files.size()==1){
                String name = files.get(0).getName();
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

    private String borrarJuego(ArrayList<File> files) {
        return null;
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
