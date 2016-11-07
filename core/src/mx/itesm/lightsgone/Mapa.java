package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;

/**
 * Created by allanruiz on 13/10/16.
 */
public class Mapa {
    private TiledMap mapa;
    private TiledMapRenderer renderer;
    private static AssetManager manager = new AssetManager();
    private static OrthographicCamera camara;
    private Array<TiledMapTileLayer> puertas,plataformaY, plataformaX,encima, items, guardado, muerte, dano, escaleras;
    private Array<CajaMovil> cajas;
    private Array<Enemigo> enemigos, enemigosActuales;
    private SpriteBatch batch;
    private int[] numPuertas;
    private float[] posicionY, posicionX;
    private boolean[] right;
    private String nombre;
    private Array<Sprite> plataformasInclinada;
    public Mapa(String mapa, SpriteBatch batch, OrthographicCamera camara, int[] numPuertas,boolean[] right,float[] posicionX ,float... posicionY){
        this.mapa = cargarMapa(mapa);
        this.nombre = mapa;
        Mapa.camara = camara;
        this.batch = batch;
        renderer = new OrthogonalTiledMapRenderer(this.mapa, batch);
        this.numPuertas = numPuertas;
        this.posicionY = posicionY;
        this.posicionX = posicionX;
        this.right = right;
        cargarCapas();
    }

    private void cargarCapas() {
        plataformaY = new Array<TiledMapTileLayer>(3);
        plataformaX = new Array<TiledMapTileLayer>(2);
        items = new Array<TiledMapTileLayer>(5);
        encima = new Array<TiledMapTileLayer>(1);
        puertas = new Array<TiledMapTileLayer>(5);
        guardado = new Array<TiledMapTileLayer>(1);
        muerte = new Array<TiledMapTileLayer>(2);
        dano = new Array<TiledMapTileLayer>(1);
        escaleras = new Array<TiledMapTileLayer>(2);
        plataformaY.add((TiledMapTileLayer)mapa.getLayers().get("PlataformaPiso"));
        plataformaY.add((TiledMapTileLayer)mapa.getLayers().get("Plataformas"));
        plataformaY.add((TiledMapTileLayer)mapa.getLayers().get("Plataformas2"));
        plataformaX.add((TiledMapTileLayer)mapa.getLayers().get("ParedPuerta"));
        plataformaX.add((TiledMapTileLayer)mapa.getLayers().get("PlataformasNoCruzar"));
        encima.add((TiledMapTileLayer)mapa.getLayers().get("CapaEncima"));
        items.add((TiledMapTileLayer)mapa.getLayers().get("Malteada"));
        items.add((TiledMapTileLayer)mapa.getLayers().get("Pogo"));
        items.add((TiledMapTileLayer)mapa.getLayers().get("VidaExtra"));
        items.add((TiledMapTileLayer)mapa.getLayers().get("LanzaPapa"));
        items.add((TiledMapTileLayer)mapa.getLayers().get("Capita"));
        puertas.add((TiledMapTileLayer)mapa.getLayers().get("PuertaCerrada"));
        puertas.add((TiledMapTileLayer)mapa.getLayers().get("Puerta1"));
        puertas.add((TiledMapTileLayer)mapa.getLayers().get("Puerta2"));
        puertas.add((TiledMapTileLayer)mapa.getLayers().get("Puerta3"));
        puertas.add((TiledMapTileLayer) mapa.getLayers().get("Puerta4"));
        guardado.add((TiledMapTileLayer)mapa.getLayers().get("Guardado"));
        muerte.add((TiledMapTileLayer)mapa.getLayers().get("SopaMortal"));
        muerte.add((TiledMapTileLayer)mapa.getLayers().get("ZonaMuerte"));
        dano.add((TiledMapTileLayer)mapa.getLayers().get("ZonaDaño"));
        escaleras.add((TiledMapTileLayer)mapa.getLayers().get("Escalera"));
        escaleras.add((TiledMapTileLayer)mapa.getLayers().get("Escalera1"));
    }


    private TiledMap cargarMapa(String mapa) {
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        manager.load(mapa, TiledMap.class);
        manager.finishLoading();
        return manager.get(mapa);
    }

    public void setPlataformasInclinada(Sprite... sprites){
        plataformasInclinada = new Array<Sprite>(sprites.length);
        for (Sprite sprite:sprites)
            plataformasInclinada.add(sprite);
    }

    public void setCajas(CajaMovil... cajas){
        this.cajas = new Array<CajaMovil>(cajas.length);
        for(CajaMovil caja: cajas)
            this.cajas.add(caja);
    }

    public boolean colisionX(float x, float y){
        return colision(x, y, plataformaX);
    }

    public float colisionY(float x, float y){
        if(cajas!=null){
            for(CajaMovil caja:cajas){
                if(caja.getRectangle().contains(x,y))
                    return caja.getRectangle().getY()+caja.getRectangle().getHeight()-20;
            }
        }

        for (TiledMapTileLayer capa: plataformaY){
            if(capa!=null){
                int celdaX = (int) (x / capa.getTileWidth());
                int celdaY = (int) (y / capa.getTileHeight());
                TiledMapTileLayer.Cell cell = capa.getCell(celdaX, celdaY);
                if (cell != null) return (celdaY+1) * capa.getTileHeight();
            }
        }

        return -1;

    }

    public boolean colision(float x, float y, Array<TiledMapTileLayer> capas){
        for(TiledMapTileLayer capa: capas){
            if(capa!=null&&capa.isVisible()){
                TiledMapTileLayer.Cell cell = capa.getCell((int) (x / capa.getTileWidth()), (int) (y / capa.getTileHeight()));
                if (cell != null) return true;
            }
        }
        return false;
    }

    public boolean colisionMuerte(float x, float y){
        return colision(x,y,muerte);
    }

    public boolean colisionItem(float x, float y, String name){
        return colision(x,y,items, name);
    }

    private boolean colision(float x, float y, Array<TiledMapTileLayer> items, String name) {
        for(TiledMapTileLayer capa: items){
            if(capa!=null&&capa.isVisible()){
                TiledMapTileLayer.Cell cell = capa.getCell((int) (x / capa.getTileWidth()), (int) (y / capa.getTileHeight()));
                if (cell != null&&capa.getName().equalsIgnoreCase(name)) return true;
            }
        }
        return false;
    }

    public void draw(){
        renderer.setView(camara);
        renderer.render();
        batch.begin();
        if(plataformasInclinada != null) {
            for (Sprite sprite: plataformasInclinada)
                sprite.draw(batch);
        }
        if(enemigos!=null){
            for(Enemigo enemigo: enemigos)
                enemigo.draw(batch);
        }
        if(cajas!=null){
            for(CajaMovil caja: cajas){
                caja.draw(batch);
            }
        }
        batch.end();

    }

    public void drawE(){
        for(TiledMapTileLayer layer: encima) {
            if(layer!=null)
                renderer.renderTileLayer(layer);
        }
    }

    public void remove(String layer){
        mapa.getLayers().get(layer).setVisible(false);
    }

    public boolean colisionEscalera(float x, float y){
        return colision(x,y,escaleras);
    }

    public int colisionPuerta(float x, float y) {
        for(TiledMapTileLayer capa: puertas){
            if(capa!=null&&capa.isVisible()){
                TiledMapTileLayer.Cell cellDer = capa.getCell((int)(x/capa.getTileWidth()), (int)(y/capa.getTileHeight()));
                TiledMapTileLayer.Cell cellIzq = capa.getCell((int)((x-125)/capa.getTileWidth()), (int)(y/capa.getTileHeight()));
                if((cellDer != null||cellIzq!=null) && capa.getName().equals("PuertaCerrada"))
                    return -1;
                if((cellDer != null||cellIzq!=null)){
                    return numPuertas[Integer.parseInt(""+capa.getName().charAt(capa.getName().length()-1))-1];
                }
            }
        }
        return -1;
    }

    public float getWidth(){
        TiledMapTileLayer layer = (TiledMapTileLayer)mapa.getLayers().get(0);
        return layer.getWidth()*layer.getTileWidth();
    }

    public float getHeight(){
        TiledMapTileLayer layer = (TiledMapTileLayer)mapa.getLayers().get(0);
        return layer.getHeight()*layer.getTileHeight();
    }

    public float getPositionY(int i) {
        return posicionY[index(i)];
    }

    public float getPositionX(int i){
        return posicionX[index(i)];
    }

    public void setEnemigos(Array<Enemigo> enemigos){
        this.enemigos = enemigos;
        copiarEnemigos();
    }

    private void copiarEnemigos() {
        if(enemigos!=null){
            this.enemigosActuales = new Array<Enemigo>(enemigos.size);
            for(Enemigo enemigo: enemigos) {
                enemigosActuales.add(enemigo);
            }
        }
    }

    private int index(int i) {
        int j;
        for(j = 0;j<numPuertas.length;j++){
            if(numPuertas[j]==i)
                break;
        }
        return j;
    }

    public boolean colisionGuardado(float x, float y){
        return colision(x, y, guardado);
    }

    public boolean getRight(int i) {
        return right[index(i)];
    }

    public boolean colisionInclinada(float x, float y){
        if(plataformasInclinada!=null){
            for(Sprite sprite1: plataformasInclinada){
                float ancho = sprite1.getBoundingRectangle().getWidth();
                float extremoderecho = sprite1.getX()+ancho;
                float rec_y = 1160;
                float inc_y = 0.2125f;
                float ys = (x-sprite1.getX())*inc_y;
                float altura = rec_y + ys+121;
                if((sprite1.getX()<=x&&x<=extremoderecho)&&(rec_y<=y&&y<=altura))
                    return true;
            }
        }
        return false;

    }

    public boolean colisionLata(float x, float y){
        TiledMapTileLayer layer = (TiledMapTileLayer)mapa.getLayers().get("DetallesFondo2");
        if(layer!= null){
            TiledMapTileLayer.Cell cell = layer.getCell((int)(x/layer.getTileWidth()),(int)(y/layer.getTileHeight()));
            return cell!=null;
        }
        return false;
    }



    public Array<Enemigo> getEnemigos(){
        return enemigosActuales;
    }

    public void dispose() {
        mapa.dispose();
    }

    public void reiniciar(GameInfo gameInfo) {
        MapLayers layers = mapa.getLayers();
        copiarEnemigos();
        if(gameInfo.isPogo()&&layers.get("Pogo")!=null) {
            layers.get("Pogo").setVisible(false);
        }
        if(gameInfo.isCapita()&&layers.get("Capita")!=null) {
            layers.get("Capita").setVisible(false);
        }
        if(gameInfo.isLanzapapas()&&layers.get("LanzaPapa")!=null) {
            mapa.getLayers().get("LanzaPapa").setVisible(false);
        }
        if(gameInfo.isLamparaTemp()&&layers.get("Lampara")!=null){
            mapa.getLayers().get("Lampara").setVisible(false);
        }

    }

    public void reiniciarTemp(GameInfo gameInfo) {
        MapLayers layers = mapa.getLayers();
        copiarEnemigos();

        if(gameInfo.isPogoTemp()&&layers.get("Pogo")!=null) {
            layers.get("Pogo").setVisible(false);
        }
        if(gameInfo.isCapitaTemp()&&layers.get("Capita")!=null) {
            layers.get("Capita").setVisible(false);
        }
        if(gameInfo.isLanzapapasTemp()&&layers.get("LanzaPapa")!=null) {
            mapa.getLayers().get("LanzaPapa").setVisible(false);
        }
        if(gameInfo.isLamparaTemp()&&layers.get("Lampara")!=null){
            mapa.getLayers().get("Lampara").setVisible(false);
        }

    }

    public boolean colisionDano(float x, float y){
        return colision(x,y,dano);
    }

    public boolean getPosEnemigo(int x,int y){
        if(((TiledMapTileLayer)mapa.getLayers().get("Plataformas")).getCell(x,y+1).getTile().getProperties().get("final").equals(true))
            return true;
        return false;

    }

    public String getName() {
        return nombre;
    }

    public void colisionCaja(float x, float y, boolean right, float velocidad){
        if(cajas!=null){
            for(CajaMovil caja: cajas){
                caja.mover(x,y,right, velocidad);
            }
        }
    }

    public boolean colisionCaja(float x, float y) {
        return colision(x,y,muerte) || colision(x,y,plataformaY);
    }

    public boolean colisionTecho(float x, float y) {
        return colision(x,y,plataformaX, "PlataformasNoCruzar");
    }

    public boolean colisionPuertaCerrada(float x, float y) {
        return colision(x,y,puertas, "PuertaCerrada");
    }
}
