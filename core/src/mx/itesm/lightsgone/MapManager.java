package mx.itesm.lightsgone;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

/**
 * Created by allanruiz on 07/11/16.
 */

public class MapManager {
    private OrthographicCamera camara;
    private SpriteBatch batch;
    private final float[] cuartoAbnerX ={530, 2295}, pasilloX = {270, 4140}, salaX = {450, 450,2280,2280}, cocina1X = {315,1305}, cocina2X = {5895,5895}, cocina3X = {630}, jardin1X = {1395, 20745,2170}, jardin2X ={540,18360}, jardin3X = {7740,400}, armario1X = {12510, 315};
    private final boolean[] cuartoAbnerB = {true, false}, pasilloB = {true, false}, salaB={true, true, false, false}, cocina1B = {true, true},
            cocina2B = {false,false}, cocina3B = {true}, jardin1B = {true, false, false}, jardin2B = {true, true}, jardin3B = {true, true}, armario1B = {false, true};
    private final int[] cuartoAbner = {9,1}, pasillo = {0,2}, sala = {1,0,6,3}, cocina1 = {2,4}, cocina2 = {3,5}, cocina3 = {4}, jardin1 = {2,7,8}, jardin2 = {6,8}, jardin3 = {6,7}, armario1 = {0,10};
    private final float[] cuartoAbnerY = {Nivel0.YBAJA, Nivel0.YBAJA}, pasilloY = {Nivel0.YBAJA, Nivel0.YBAJA}, salaY = {Nivel0.YALTA, Nivel0.YBAJA, Nivel0.YSALA, Nivel0.YMEDIA}, cocina1Y = {Nivel0.YBAJA, Nivel0.YCOCINA1}, cocina2Y ={Nivel0.YBAJA, Nivel0.YCOCINA2},cocina3Y = {Nivel0.YCOCINA3},
            jardin1Y = {Nivel0.YCOCINA2, Nivel0.YJARDIN1,Nivel0.YBAJA}, jardin2Y = {Nivel0.YJARDIN2, Nivel0.YJARDIN2},jardin3Y={Nivel0.YBAJA, Nivel0.YJARDIN3}, armario1Y = {Nivel0.YARMARIO, Nivel0.YSALA};
    private final float[][] posicionX= {cuartoAbnerX, pasilloX, salaX, cocina1X, cocina2X, cocina3X, jardin1X, jardin2X, jardin3X, armario1X};
    private final int[][] numPuertas = {cuartoAbner, pasillo, sala, cocina1, cocina2, cocina3, jardin1, jardin2, jardin3, armario1};
    private final boolean[][] right= {cuartoAbnerB, pasilloB, salaB, cocina1B, cocina2B, cocina3B, jardin1B, jardin2B, jardin3B, armario1B};
    private final float[][] posicionY = {cuartoAbnerY, pasilloY, salaY, cocina1Y, cocina2Y, cocina3Y, jardin1Y, jardin2Y, jardin3Y, armario1Y};
    private static Array<Enemigo> enemigos;

    public MapManager(OrthographicCamera camara, SpriteBatch batch){
        this.camara = camara;
        this.batch = batch;
    }

    public Mapa getNewMapa(String nombre, int mapaActual, Abner abner, GameInfo gameInfo){
        Mapa mapa = new Mapa(nombre, batch, camara,numPuertas[mapaActual], right[mapaActual], posicionX[mapaActual], posicionY[mapaActual]);
        mapa.reiniciar(gameInfo);
        setExtras(mapa, mapaActual, abner);
        return mapa;
    }

    public Mapa getMapa(String nombre, int mapaActual, Abner abner, GameInfo gameInfo){
        Mapa mapa = new Mapa(nombre, batch, camara,numPuertas[mapaActual], right[mapaActual], posicionX[mapaActual], posicionY[mapaActual]);
        mapa.reiniciarTemp(gameInfo);
        setExtras(mapa, mapaActual, abner);
        return mapa;
    }

    public static void setExtras(Mapa mapa, int mapaActual, Abner abner) {
        enemigos = new Array<Enemigo>();
        switch (mapaActual){
            case 3:
                //Moscas
                enemigos.add(new Enemigo.Mosca(2295, 293, abner, mapa));
                enemigos.add(new Enemigo.Mosca(2745, 2295, abner, mapa));
                enemigos.add(new Enemigo.Mosca(3420, 2295, abner, mapa));


                //Flamas
                enemigos.add(new Enemigo.Fuego(7650, 1010, abner, mapa));
                enemigos.add(new Enemigo.Fuego(8300, 1010, abner, mapa));
                enemigos.add(new Enemigo.Fuego(8660, 1010, abner, mapa));
                enemigos.add(new Enemigo.Fuego(9020, 1010, abner, mapa));
                enemigos.add(new Enemigo.Fuego(9110, 1010, abner, mapa));
                enemigos.add(new Enemigo.Fuego(9200, 1010, abner, mapa));
                enemigos.add(new Enemigo.Fuego(9290, 1010, abner, mapa));

                //Panes tostadores
                enemigos.add(new Enemigo.PanTostadora(7635, 2035, abner, mapa));
                enemigos.add(new Enemigo.PanTostadora(5873, 1995, abner, mapa));
                enemigos.add(new Enemigo.PanTostadora(4703, 1995, abner, mapa));

                //Tostadores
                enemigos.add(new Enemigo.Tostadora(7612, 2025, abner, mapa));
                enemigos.add(new Enemigo.Tostadora(5850, 1980, abner, mapa));
                enemigos.add(new Enemigo.Tostadora(4680, 1980, abner, mapa));

                //Brocolis
                enemigos.add(new Enemigo.Brocoli(3285, 500, abner, mapa));
                enemigos.add(new Enemigo.Brocoli(4410, 90, abner, mapa));
                enemigos.add(new Enemigo.Brocoli(5085, 90, abner, mapa));
                enemigos.add(new Enemigo.Brocoli(6435, 900, abner, mapa));
                enemigos.add(new Enemigo.Brocoli(9900, 1620, abner, mapa));
                enemigos.add(new Enemigo.Brocoli(8865, 2025, abner, mapa));
                mapa.setEnemigos(enemigos);
                break;
            case 4:
                //Enemigos cocina 2
                //Sopas
                enemigos.add(new Enemigo.Sopa(5265,180,abner,mapa));
                enemigos.add(new Enemigo.Sopa(4005,225,abner,mapa));
                enemigos.add(new Enemigo.Sopa(1395,1125,abner,mapa));
                //Brocolis
                enemigos.add(new Enemigo.Brocoli(765,495,abner,mapa));
                enemigos.add(new Enemigo.Lata(Nivel0.LATAX,mapa.getHeight(),mapa));
                enemigos.add(new Enemigo.Lata(Nivel0.LATAX, mapa.getHeight()+3300, mapa));
                enemigos.add(new Enemigo.Lata(Nivel0.LATAX, mapa.getHeight()+6600, mapa));
                enemigos.add(new Enemigo.Lata(2640, 160, mapa,""));
                enemigos.add(new Enemigo.Lata(1110, 160, mapa,""));
                mapa.setEnemigos(enemigos);
                Sprite sprite = new Sprite(Nivel0.plataforma);
                sprite.setRotation(12);
                sprite.setPosition(Nivel0.ANCHO_MUNDO + 470, Nivel0.ALTO_MUNDO * 3 - 850);
                mapa.setPlataformasInclinada(sprite);
                break;
            case 5:
                //Enemigos cocina 3
                //Moscas
                enemigos.add(new Enemigo.Mosca(3330,765,abner,mapa));
                enemigos.add(new Enemigo.Mosca(5355,1125,abner,mapa));


                //Sopas
                enemigos.add(new Enemigo.Sopa(2700,585,abner,mapa));
                enemigos.add(new Enemigo.Sopa(4635,945,abner,mapa));
                enemigos.add(new Enemigo.Sopa(5805,1530,abner,mapa));

                //Panes Tostadores
                enemigos.add(new Enemigo.PanTostadora(4298,780,abner,mapa));
                enemigos.add(new Enemigo.PanTostadora(6143,1320,abner,mapa));

                //Tostadores
                enemigos.add(new Enemigo.Tostadora(4275,765,abner,mapa));
                enemigos.add(new Enemigo.Tostadora(6120,1305,abner,mapa));
                mapa.setEnemigos(enemigos);
                break;
            case 6:
                //Jardin 1
                mapa.setCajas(new CajaMovil(14850, 1700, mapa));
                enemigos.add(new Enemigo.Scarecrow(2745,1300,abner,mapa));
                enemigos.add(new Enemigo.ProyectilHongo(5865,1370,abner,mapa));
                enemigos.add(new Enemigo.Hongo(5805,1300,abner,mapa));
                enemigos.add(new Enemigo.ProyectilHongo(7560,1370,abner,mapa));
                enemigos.add(new Enemigo.Hongo(7470,1300,abner,mapa));
                enemigos.add(new Enemigo.ProyectilHongo(11895,1870,abner,mapa));
                enemigos.add(new Enemigo.Hongo(11835,1800,abner,mapa));
                enemigos.add(new Enemigo.ProyectilHongo(12210,1870,abner,mapa));
                enemigos.add(new Enemigo.Hongo(12150,1800,abner,mapa));
                enemigos.add(new Enemigo.Serpiente(13095,1800,abner,mapa));
                enemigos.add(new Enemigo.Serpiente(17505,1260,abner,mapa));
                enemigos.add(new Enemigo.Espinas(19200,1260,abner,mapa));
                enemigos.add(new Enemigo.Espinas(20610,1260,abner,mapa));
                mapa.setEnemigos(enemigos);
                break;
            case 7:
                //Jardin 2
                mapa.setCajas(new CajaMovil(13815, 315, mapa));
                enemigos.add(new Enemigo.Cucarachon(90,315,abner,mapa));
                enemigos.add(new Enemigo.Serpiente(6165,540,abner,mapa));
                enemigos.add(new Enemigo.ProyectilHongo(7260,610,abner,mapa));
                enemigos.add(new Enemigo.Hongo(7200,540,abner,mapa));
                enemigos.add(new Enemigo.Serpiente(8190,540,abner,mapa));
                enemigos.add(new Enemigo.ProyectilHongo(9645,835,abner,mapa));
                enemigos.add(new Enemigo.Hongo(9585,765,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(9990,315,abner,mapa));
                enemigos.add(new Enemigo.ProyectilHongo(15045,385,abner,mapa));
                enemigos.add(new Enemigo.Hongo(14985,315,abner,mapa));
                enemigos.add(new Enemigo.ProyectilHongo(16045,385,abner,mapa));
                enemigos.add(new Enemigo.Hongo(15975,315,abner,mapa));
                mapa.setEnemigos(enemigos);
                break;
            case 8:
                //Jardin 3
                enemigos.add(new Enemigo.Scarecrow(855,1350,abner,mapa));
                enemigos.add(new Enemigo.Gnomo(2925,1350,abner,mapa));
                enemigos.add(new Enemigo.Gnomo(3645,1350,abner,mapa));
                enemigos.add(new Enemigo.Gnomo(4275,1350,abner,mapa));
                enemigos.add(new Enemigo.Gnomo(3645,1350,abner,mapa));
                enemigos.add(new Enemigo.Gnomo(4995,1350,abner,mapa));
                mapa.setEnemigos(enemigos);
                break;
        }
    }

    public static void quitarEnemigo(Enemigo ene){
        if (enemigos.contains(ene,true)){
            if(ene.getClass().getSimpleName().equals("Hongo")){
                enemigos.removeIndex(enemigos.indexOf(ene,true)-1);
            }
            enemigos.removeIndex(enemigos.indexOf(ene,true));
        }
    }


}
