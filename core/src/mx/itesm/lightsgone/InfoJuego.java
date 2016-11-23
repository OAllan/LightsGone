package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

/**
 * Created by allanruiz on 21/10/16.
 */
public class InfoJuego {
    private int vida, vidas;
    private boolean pogo, capita, lanzapapas, lampara, armario, cinematicaInicio;
    private boolean pogoTemp, capitaTemp, lanzapapasTemp, lamparaTemp, armarioTemp;
    private int mapa;
    private boolean vida1, vida2, vida3, vida4;
    private float x, y, camaraX, camaraY, timer, alpha;
    private String nombre;
    private Calendar fecha;
    private Abner abner;
    private String fechaS;
    private boolean mensaje;
    private Sprite guardado;
    private AssetManager manager;
    private LightsGone.Transicion transicion;

    public InfoJuego(){
        vidas = 0;
        vida = 99;
        pogo = capita = lanzapapas = lampara = false;
        pogoTemp = capitaTemp = lanzapapasTemp = lamparaTemp=false;
        mapa = 0;
        x = 530;
        y=135;
        nombre = nombre();
        camaraX = 640;
        camaraY = 400;
        armario = armarioTemp = false;
        cinematicaInicio = false;
        timer = 0;
        cargar();
    }

    public InfoJuego(String juego) {
        cargarJuego(juego);
        nombre = juego;
        timer = 0;
        cargar();
    }

    public String getFecha(){
        String[] fecha = fechaS.split("/");
        String mes = getMes(Integer.parseInt(fecha[1]));
        return fecha[0] + "/"+ mes+"/"+fecha[2];
    }

    private String getMes(int i) {
        switch (i){
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "Jun";
            case 6:
                return "Jul";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            default:
                return "Dec";
        }
    }

    private void cargar(){
        manager = new AssetManager();
        manager.load("SavedFile.png", Texture.class);
        manager.finishLoading();
        guardado = new Sprite(manager.get("SavedFile.png", Texture.class));
        guardado.setPosition(900,200);
    }

    public void setAbner(Abner abner){
        this.abner = abner;
    }

    public void draw(SpriteBatch batch){
        if(mensaje){
            guardado.setAlpha(alpha);
            guardado.draw(batch);
            guardado();
        }
    }

    private void guardado() {
        switch (transicion){
            case AUMENTANDO:
                alpha += Gdx.graphics.getDeltaTime()/2;
                if(alpha>=1){
                    alpha = 1;
                    transicion = LightsGone.Transicion.DISMINUYENDO;
                }
                break;
            case DISMINUYENDO:
                timer+=Gdx.graphics.getDeltaTime();
                if(timer>=2){
                    alpha -= Gdx.graphics.getDeltaTime()/2;
                    timer = 2;
                    if(alpha<=0){
                        alpha = 0;
                        mensaje=false;
                    }
                }
        }
    }

    private void cargarJuego(String juego) {

        try{
            FileHandle fileHandle;
            fileHandle = Gdx.files.local(juego);
            String archivo = fileHandle.readString();
            String[] lineas = archivo.split("\n");
            fechaS = lineas[0].trim();
            mapa = Integer.parseInt(lineas[1].trim());
            x = Float.parseFloat(lineas[2].trim());
            y = Float.parseFloat(lineas[3].trim());
            vida = Integer.parseInt(lineas[4].trim());
            pogo = pogoTemp=Boolean.parseBoolean(lineas[5].trim());
            lanzapapas = lanzapapasTemp = Boolean.parseBoolean(lineas[6].trim());
            capita = capitaTemp = Boolean.parseBoolean(lineas[7].trim());
            camaraX = Float.parseFloat(lineas[8].trim());
            camaraY = Float.parseFloat(lineas[9].trim());
            vidas = Integer.parseInt(lineas[10].trim());
            lampara = lamparaTemp = Boolean.parseBoolean(lineas[11].trim());
            armario = armarioTemp = Boolean.parseBoolean(lineas[12].trim());
            cinematicaInicio = Boolean.parseBoolean(lineas[13].trim());
            vida1 = Boolean.parseBoolean(lineas[14].trim());
            vida2 = Boolean.parseBoolean(lineas[15].trim());
            vida3 = Boolean.parseBoolean(lineas[16].trim());
            vida4 = Boolean.parseBoolean(lineas[17].trim());

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
            save.writeString(vidas + "\n", true);
            save.writeString(lampara + "\n",true);
            save.writeString(armario+"\n", true);
            save.writeString(cinematicaInicio+"\n", true);
            save.writeString(vida1+"\n", true);
            save.writeString(vida2+"\n", true);
            save.writeString(vida3+"\n", true);
            save.writeString(vida4+"\n", true);

        }
        catch (Exception e){
            Gdx.app.log("Exception ", e.getMessage());
        }
        finally {
            mensaje = true;
            transicion = LightsGone.Transicion.AUMENTANDO;
        }

    }

    private void actualizarDatos() {
        mapa = LightsGone.mapaActual;
        vida = abner.getcantVida();
        pogo = abner.getPogo();
        capita = abner.getCapita();
        lanzapapas = abner.getLanzapapas();
        x = abner.getX();
        y = abner.getY();
        camaraX = abner.getCamaraX();
        camaraY = abner.getCamaraY();
        vidas = abner.getVidas();
        lampara = abner.getLampara();
        armario = abner.getArmario();
        cinematicaInicio = LightsGone.cinematicaInicio;
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
            if(numero.equals('1'))return nombre + "2.txt";
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

    public boolean isLampara(){
        return lampara;
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

    public int getVidas() {
        return vidas;
    }

    public boolean isPogoTemp() {
        return pogoTemp;
    }

    public boolean isCapitaTemp() {
        return capitaTemp;
    }

    public boolean isLanzapapasTemp() {
        return lanzapapasTemp;
    }

    public boolean isLamparaTemp() {
        return lamparaTemp;
    }

    public boolean isArmarioTemp(){
        return armarioTemp;
    }

    public boolean isArmario(){
        return armario;
    }

    public boolean isCinematicaInicio(){
        return cinematicaInicio;
    }

    public void actualizarDatosTemp() {
        lamparaTemp = abner.getLampara();
        pogoTemp = abner.getPogo();
        lanzapapasTemp = abner.getLanzapapas();
        armarioTemp = abner.getArmario();
        vida1 = abner.getVida1();
        vida2 = abner.getVida2();
        vida3 = abner.getVida3();
        vida4 = abner.getVida4();
    }

    public boolean isVida1(){
        return vida1;
    }

    public boolean isVida2(){
        return vida2;
    }

    public boolean isVida3(){
        return vida3;
    }

    public boolean isVida4(){
        return vida4;
    }
}
