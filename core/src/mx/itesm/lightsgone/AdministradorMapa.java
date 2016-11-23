package mx.itesm.lightsgone;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

/**
 * Created by allanruiz on 07/11/16.
 */

public class AdministradorMapa {
    private OrthographicCamera camara;
    private SpriteBatch batch;
    private final float[] cuartoAbnerX ={530, 2295, 530}, pasilloX = {270, 4140,2160}, salaX = {450, 450,2280,2280}, cocina1X = {315,1305}, cocina2X = {5895,5895}, cocina3X = {630}, jardin1X = {1395, 20745,2170}, jardin2X ={540,18360}, jardin3X = {7740,400}, armario1X = {12510, 315},
                armario2X = {14400, 270}, armario3X = {9450, 270}, armario4X = {1600,1600}, sotano1X = {8900,990}, sotano2X = {630,8100}, sotano3X = {7515}, caminoX = {585,585}, atico1X = {1260,1260}, atico2X = {3240};
    private final boolean[] cuartoAbnerB = {true, false, true}, pasilloB = {true, false,true}, salaB={true, true, false, false}, cocina1B = {true, true},
            cocina2B = {false,false}, cocina3B = {true}, jardin1B = {true, false, false}, jardin2B = {true, true}, jardin3B = {true, true}, armario1B = {false, true}, armario2B = {false, true}, armario3B ={false, true}, armario4B = {false, false},
            sotano1B = {false, true}, sotano2B = {true, false}, sotano3B = {false},caminoB = {true, true}, atico1B = {true, true}, atico2B = {false};
    private final int[] cuartoAbner = {9,1,12}, pasillo = {0,2,16}, sala = {1,13,6,3}, cocina1 = {2,4}, cocina2 = {3,5}, cocina3 = {4}, jardin1 = {2,7,8}, jardin2 = {6,8}, jardin3 = {6,7}, armario1 = {0,10}, armario2 = {9,11}, armario3 = {10,12}, armario4 = {11,0}, sotano1 = {2,14}, sotano2 = {13,15}, sotano3 = {14}, camino = {1,17}, atico1 = {16,18}, atico2={17};
    private final float[] cuartoAbnerY = {LightsGone.YBAJA, LightsGone.YBAJA, LightsGone.YBAJA}, pasilloY = {LightsGone.YBAJA, LightsGone.YBAJA, LightsGone.YBAJA}, salaY = {LightsGone.YALTA, LightsGone.YBAJA, LightsGone.YSALA, LightsGone.YMEDIA}, cocina1Y = {LightsGone.YBAJA, LightsGone.YCOCINA1}, cocina2Y ={LightsGone.YBAJA, LightsGone.YCOCINA2},cocina3Y = {LightsGone.YCOCINA3},
            jardin1Y = {LightsGone.YCOCINA2, LightsGone.YJARDIN1, LightsGone.YBAJA}, jardin2Y = {LightsGone.YJARDIN2, LightsGone.YJARDIN2},jardin3Y={LightsGone.YBAJA, LightsGone.YJARDIN3}, armario1Y = {LightsGone.YARMARIO, 990}, armario2Y = {945,1035}, armario3Y = {945,945}, armario4Y = {LightsGone.YBAJA, LightsGone.YBAJA}, sotano1Y = {405,585}, sotano2Y = {4005,450}, sotano3Y = {4455},
            caminoY = {LightsGone.YBAJA, 3510}, atico1Y = {180,720}, atico2Y = {180};
    private final float[][] posicionX= {cuartoAbnerX, pasilloX, salaX, cocina1X, cocina2X, cocina3X, jardin1X, jardin2X, jardin3X, armario1X, armario2X, armario3X, armario4X, sotano1X, sotano2X, sotano3X,caminoX, atico1X, atico2X};
    private final int[][] numPuertas = {cuartoAbner, pasillo, sala, cocina1, cocina2, cocina3, jardin1, jardin2, jardin3, armario1, armario2, armario3, armario4, sotano1, sotano2, sotano3, camino, atico1, atico2};
    private final boolean[][] right= {cuartoAbnerB, pasilloB, salaB, cocina1B, cocina2B, cocina3B, jardin1B, jardin2B, jardin3B, armario1B, armario2B, armario3B, armario4B, sotano1B, sotano2B, sotano3B, caminoB, atico1B, atico2B};
    private final float[][] posicionY = {cuartoAbnerY, pasilloY, salaY, cocina1Y, cocina2Y, cocina3Y, jardin1Y, jardin2Y, jardin3Y, armario1Y, armario2Y, armario3Y, armario4Y, sotano1Y, sotano2Y, sotano3Y, caminoY, atico1Y, atico2Y};
    private static Array<Enemigo> enemigos;

    public AdministradorMapa(OrthographicCamera camara, SpriteBatch batch){
        this.camara = camara;
        this.batch = batch;
    }

    public Mapa getNewMapa(String nombre, int mapaActual, Abner abner, InfoJuego gameInfo){
        Mapa mapa = new Mapa(nombre, batch, camara,numPuertas[mapaActual], right[mapaActual], posicionX[mapaActual], posicionY[mapaActual]);
        mapa.reiniciar(gameInfo);
        setExtras(mapa, mapaActual, abner);
        return mapa;
    }

    public Mapa getMapa(String nombre, int mapaActual, Abner abner, InfoJuego gameInfo){
        Mapa mapa = new Mapa(nombre, batch, camara,numPuertas[mapaActual], right[mapaActual], posicionX[mapaActual], posicionY[mapaActual]);
        mapa.reiniciarTemp(gameInfo);
        setExtras(mapa, mapaActual, abner);
        return mapa;
    }

    public static void setExtras(Mapa mapa, int mapaActual, Abner abner) {
        enemigos = new Array<Enemigo>();
        switch (mapaActual){
            case 0:
                //enemigos.add(new Enemigo.PlantaCarnivora(450,150,abner,mapa));
                enemigos.add(new Enemigo.Sopa(900,150,abner,mapa));


                mapa.setEnemigos(enemigos);
                break;

            case 1:
                //enemigos.add(new Enemigo.Cucarachon(1500,150,abner,mapa));
                //enemigos.add(new Enemigo.Gnomo(2500,150,abner,mapa));
                //mapa.setEnemigos(enemigos);
                //enemigos.add(new Enemigo.Telarana(1500,150,abner,mapa));
                //enemigos.add(new Enemigo.Telarana(3000,150,abner,mapa));
                //enemigos.add(new Enemigo.Telarana(4000,150,abner,mapa));
                //enemigos.add(new Enemigo.Fantasma(1500,675,abner,mapa));
                //enemigos.add(new Enemigo.ParedPicos(2300,100,abner,mapa));
               // enemigos.add(new Enemigo.Sandwich(1500,150,abner,mapa));


                mapa.setEnemigos(enemigos);
                break;

            case 3:
                //Moscas
                //enemigos.add(new Enemigo.Cucarachon(2295,293,abner,mapa));
               //enemigos.add(new Enemigo.Sopa(2295,293,abner,mapa));
                enemigos.add(new Enemigo.Mosca(2295, 250, abner, mapa));
                enemigos.add(new Enemigo.Mosca(2745, 2400, abner, mapa));
                enemigos.add(new Enemigo.Mosca(3420, 2400, abner, mapa));


                //Flamas
                enemigos.add(new Enemigo.Fuego(7650, 1010, abner, mapa));
                enemigos.add(new Enemigo.Fuego(8300, 1010, abner, mapa));
                enemigos.add(new Enemigo.Fuego(8660, 1010, abner, mapa));
                enemigos.add(new Enemigo.Fuego(9020, 1010, abner, mapa));
                enemigos.add(new Enemigo.Fuego(9110, 1010, abner, mapa));
                enemigos.add(new Enemigo.Fuego(9200, 1010, abner, mapa));
                enemigos.add(new Enemigo.Fuego(9290, 1010, abner, mapa));

                //Panes tostadores
                enemigos.add(new Enemigo.PanTostadora(7612, 2035, abner, mapa));
                enemigos.add(new Enemigo.PanTostadora(5850, 1995, abner, mapa));
                enemigos.add(new Enemigo.PanTostadora(4680, 1995, abner, mapa));

                //Tostadores
                enemigos.add(new Enemigo.Tostadora(7612, 2025, abner, mapa));
                enemigos.add(new Enemigo.Tostadora(5850, 1980, abner, mapa));
                enemigos.add(new Enemigo.Tostadora(4680, 1980, abner, mapa));

                //Brocolis
                enemigos.add(new Enemigo.Brocoli(3285, 500, abner, mapa));
                enemigos.add(new Enemigo.Brocoli(4410, 90, abner, mapa));
                enemigos.add(new Enemigo.Brocoli(5085, 90, abner, mapa));
                enemigos.add(new Enemigo.Sandwich(6435, 900, abner, mapa));
                enemigos.add(new Enemigo.Brocoli(9900, 1620, abner, mapa));
                enemigos.add(new Enemigo.Brocoli(8865, 2025, abner, mapa));
                mapa.setEnemigos(enemigos);
                break;
            case 4:
                //Enemigos cocina 2
                //Sopas
                enemigos.add(new Enemigo.Sopa(5265,180,abner,mapa));
                enemigos.add(new Enemigo.Sopa(3875,225,abner,mapa));
                enemigos.add(new Enemigo.Sopa(1395,1125,abner,mapa));
                //Brocolis
                enemigos.add(new Enemigo.Sandwich(765,495,abner,mapa));
                enemigos.add(new Enemigo.Lata(LightsGone.LATAX,mapa.getHeight(),mapa));
                enemigos.add(new Enemigo.Lata(LightsGone.LATAX, mapa.getHeight()+3300, mapa));
                enemigos.add(new Enemigo.Lata(LightsGone.LATAX, mapa.getHeight()+6600, mapa));
                enemigos.add(new Enemigo.Lata(2640, 160, mapa,""));
                enemigos.add(new Enemigo.Lata(1110, 160, mapa,""));
                mapa.setEnemigos(enemigos);
                Sprite sprite = new Sprite(LightsGone.plataforma);
                sprite.setRotation(12);
                sprite.setPosition(LightsGone.ANCHO_MUNDO + 470, LightsGone.ALTO_MUNDO * 3 - 850);
                mapa.setPlataformasInclinada(sprite);
                break;
            case 5:
                //Enemigos cocina 3
                //Moscas
                enemigos.add(new Enemigo.Mosca(3330,700,abner,mapa));
                enemigos.add(new Enemigo.Mosca(5355,1050,abner,mapa));


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
                mapa.setCajas(new CajaMovil(14850, 1700, mapa, true));
                enemigos.add(new Enemigo.Scarecrow(2745,1300,abner,mapa));
                enemigos.add(new Enemigo.ProyectilHongo(5865,1370,abner,mapa));
                enemigos.add(new Enemigo.Hongo(5805,1300,abner,mapa));
                enemigos.add(new Enemigo.ProyectilHongo(7560,1370,abner,mapa));
                enemigos.add(new Enemigo.Hongo(7470,1300,abner,mapa));
                enemigos.add(new Enemigo.PlantaCarnivora(11835,1800,abner,mapa));
                enemigos.add(new Enemigo.ProyectilHongo(12210,1870,abner,mapa));
                enemigos.add(new Enemigo.Hongo(12150,1800,abner,mapa));
                enemigos.add(new Enemigo.Serpiente(13095,1800,abner,mapa));
                enemigos.add(new Enemigo.Serpiente(17505,1260,abner,mapa));
                enemigos.add(new Enemigo.Espinas(19200,1260,abner,mapa));
                enemigos.add(new Enemigo.GnomoL(19400,2560,abner,mapa));
                enemigos.add(new Enemigo.Espinas(20610,1260,abner,mapa));
                mapa.setEnemigos(enemigos);
                break;
            case 7:
                //Jardin 2
                mapa.setCajas(new CajaMovil(13815, 315, mapa, true));
                enemigos.add(new Enemigo.Cucarachon(90,315,abner,mapa));
                enemigos.add(new Enemigo.EspinasD(5670,540,abner,mapa));
                enemigos.add(new Enemigo.EspinasD(6840,540,abner,mapa));
                enemigos.add(new Enemigo.EspinasD(7605,540,abner,mapa));
                enemigos.add(new Enemigo.EspinasD(8775,540,abner,mapa));
                enemigos.add(new Enemigo.Serpiente(6165,540,abner,mapa));
                enemigos.add(new Enemigo.PlantaCarnivora(7200,540,abner,mapa));
                enemigos.add(new Enemigo.Serpiente(8190,540,abner,mapa));
                enemigos.add(new Enemigo.ProyectilHongo(9645,835,abner,mapa));
                enemigos.add(new Enemigo.Hongo(9585,765,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(9990,315,abner,mapa));
                enemigos.add(new Enemigo.ProyectilHongo(15045,385,abner,mapa));
                enemigos.add(new Enemigo.Hongo(14985,315,abner,mapa));
                enemigos.add(new Enemigo.PlantaCarnivora(15975,315,abner,mapa));
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
            case 9:
                //Armario 1
                enemigos.add(new Enemigo.GeneradorCajasPayaso(2385,315,mapa, abner));
                enemigos.add(new Enemigo.GeneradorCajasPayaso(6075, 315, mapa, abner));
                enemigos.add(new Enemigo.MonstruoRopa(4185,270,abner));
                enemigos.add(new Enemigo.MonstruoRopa(10215,270,abner));
                enemigos.add(new Enemigo.MonstruoRopa(990,540,abner));
                mapa.setEnemigos(enemigos);
                mapa.setCajas(new CajaMovil(8055,270,mapa, false), new CajaMovil(8055, 400, mapa, false));
                mapa.setCajasFijas(new Caja(6200, 270), new Caja(6200, 270 + 164), new Caja(5795, 270),new Caja(5795, 270+164));
                break;
            case 10:
                //Armario2
                enemigos.add(new Enemigo.GeneradorCajasPayaso(12465,540,mapa,abner));
                enemigos.add(new Enemigo.GeneradorCajasPayaso(7920,1170,mapa,abner));
                enemigos.add(new Enemigo.GeneradorCajasPayaso(7470,585,mapa,abner));
                enemigos.add(new Enemigo.GeneradorCajasPayaso(7110,1215,mapa,abner));
                enemigos.add(new Enemigo.GeneradorCajasPayaso(4680,500,mapa,abner));
                enemigos.add(new Enemigo.GeneradorCajasPayaso(4970,315,mapa,abner));
                enemigos.add(new Enemigo.Robot(11115, 540, abner));
                enemigos.add(new Enemigo.Robot(5534, 315, abner));
                enemigos.add(new Enemigo.Oso(720,1035, abner, mapa));
                mapa.setEnemigos(enemigos);
                mapa.setCajasFijas(new Caja(8400, 765), new Caja(4680, 315));
                break;
            case 11:
                //Armario3
                enemigos.add(new Enemigo.GeneradorCajasPayaso(7470,900,mapa,abner));
                enemigos.add(new Enemigo.GeneradorCajasPayaso(3150,225,mapa,abner));
                enemigos.add(new Enemigo.Robot(990, 900,abner));
                enemigos.add(new Enemigo.Oso(7965,945, abner, mapa));
                mapa.setEnemigos(enemigos);
                mapa.setCajasFijas(new Caja(6920,900), new Caja(6920, 900+164), new Caja(7190, 900), new Caja(7190, 900+164), new Caja(4050,190), new Caja(4050,190+164));
                mapa.setCajas(new CajaMovil(4100, 190+164*2,mapa, false));
                break;
            case 13:
                //Sotano 1
                enemigos.add(new Enemigo.Fantasma(8910,630,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(8900,495,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(7785,900,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(7425,675,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(6165,405,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(3510,855,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(2250,855,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(1930,855,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(1758,675,abner,mapa));
                enemigos.add(new Enemigo.Mosca(4770,540,abner,mapa));
                enemigos.add(new Enemigo.Mosca(2340,720,abner,mapa));
                mapa.setEnemigos(enemigos);
                break;
            case 14:
                //Sotano 2
                enemigos.add(new Enemigo.Fantasma(225,3825,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(945,3420,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(315,2430,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(990,2610,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(270,1800,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(900,1800,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(495,1305,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(1800,1440,abner,mapa));
                enemigos.add(new Enemigo.Telarana(1800,2160,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(2295,450,abner,mapa));
                enemigos.add(new Enemigo.Alfombra(4320,450,abner));
                enemigos.add(new Enemigo.Alfombra(5040,450,abner));
                enemigos.add(new Enemigo.Alfombra(6705,450,abner));
                mapa.setEnemigos(enemigos);
                break;
            case 15:
                //Sotano 3
                enemigos.add(new Enemigo.ParedPicos(7545,3555,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(6345,5400,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(4545,5400,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(1575,5400,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(315,5000,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(990,4455,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(1395,3735,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(3690,3960,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(3690,3645,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(6165,3945,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(6530,3680,abner,mapa));
                enemigos.add(new Enemigo.Telarana(2070,4230,abner,mapa));
                enemigos.add(new Enemigo.Alfombra(5445,3510,abner));
                enemigos.add(new Enemigo.Alfombra(3375,3510,abner));

                enemigos.add(new Enemigo.Fantasma(5310,2385,abner,mapa));
                enemigos.add(new Enemigo.Fantasma(4980,2010,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(765,2250,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(1395,2250,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(2340,2250,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(3285,2250,abner,mapa));
                enemigos.add(new Enemigo.Mosca(2790,2430,abner,mapa));
                enemigos.add(new Enemigo.Mosca(3780,1485,abner,mapa));

                enemigos.add(new Enemigo.Cucarachon(900,180,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(1100,180,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(1300,180,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(1500,180,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(1700,180,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(1900,180,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(2100,180,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(2300,180,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(2500,180,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(2700,180,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(2900,180,abner,mapa));
                enemigos.add(new Enemigo.Cucarachon(3100,180,abner,mapa));
                mapa.setEnemigos(enemigos);
                break;
            case 16:
                enemigos.add(new Enemigo.OndaCoco(300,mapa.getHeight(), abner));
                mapa.setEnemigos(enemigos);
                break;

        }
    }

    public static void quitarEnemigo(Enemigo ene){
        if (enemigos.contains(ene,true)){
            if(ene.getClass().getSimpleName().equals("Hongo")){
                enemigos.removeIndex(enemigos.indexOf(ene,true)-1);
            }
            if(ene.getClass().getSimpleName().equals("Telarana")){
                enemigos.removeIndex(enemigos.indexOf(ene,true)-1);
            }
            if(!(ene instanceof Enemigo.GnomoL) && !(ene instanceof Enemigo.Gnomo)){
                LightsGone.agregarItem(ene);
            }
            enemigos.removeIndex(enemigos.indexOf(ene,true));
        }
    }

    public static void crearNuevaMosca(float x,float y,Enemigo.Mosca ene){
            enemigos.add(new Enemigo.Mosca(x,y,ene.getAbner(),ene.getMapa()));

    }

    public static void crearArana(Enemigo.Telarana ene){
        enemigos.add(new Enemigo.Arana(ene.getX()+ene.sprite.getWidth()/2,ene.getY()+ene.sprite.getHeight()/2,ene.getAbner(),ene.getMapa(),ene.getRectangle()));

    }

    public static void  invocarLluviaDeGnomos(Abner abner,Mapa mapa){
        Random rnd=new Random();
        for (int i = 0; i <4 ; i++) {
            enemigos.add(new Enemigo.GnomoL(rnd.nextInt(1410)+19200,rnd.nextInt(4000)+1900,abner,mapa));
        }
    }


}
