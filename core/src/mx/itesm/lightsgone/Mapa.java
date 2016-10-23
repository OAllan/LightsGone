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
    private Array<TiledMapTileLayer> puertas,plataformaY, plataformaX,encima, items, guardado;
    private Array<Enemigo> enemigos;
    private SpriteBatch batch;
    private int[] numPuertas;
    private float[] posicionY;
    private boolean[] right;
    private Array<Sprite> plataformasInclinada;
    public Mapa(String mapa, SpriteBatch batch, OrthographicCamera camara, int[] numPuertas,boolean[] right,float... posicionY){
        this.mapa = cargarMapa(mapa);
        Mapa.camara = camara;
        this.batch = batch;
        renderer = new OrthogonalTiledMapRenderer(this.mapa, batch);
        this.numPuertas = numPuertas;
        this.posicionY = posicionY;
        this.right = right;
        cargarCapas();
    }

    private void cargarCapas() {
        plataformaY = new Array<TiledMapTileLayer>(3);
        plataformaX = new Array<TiledMapTileLayer>(2);
        items = new Array<TiledMapTileLayer>(3);
        encima = new Array<TiledMapTileLayer>(1);
        puertas = new Array<TiledMapTileLayer>(5);
        guardado = new Array<TiledMapTileLayer>(1);
        plataformaY.add((TiledMapTileLayer)mapa.getLayers().get("PlataformaPiso"));
        plataformaY.add((TiledMapTileLayer)mapa.getLayers().get("Plataformas"));
        plataformaY.add((TiledMapTileLayer)mapa.getLayers().get("Plataformas2"));
        plataformaX.add((TiledMapTileLayer)mapa.getLayers().get("ParedPuerta"));
        plataformaX.add((TiledMapTileLayer)mapa.getLayers().get("PlataformasNoCruzar"));
        encima.add((TiledMapTileLayer)mapa.getLayers().get("CapaEncima"));
        items.add((TiledMapTileLayer)mapa.getLayers().get("Malteada"));
        items.add((TiledMapTileLayer)mapa.getLayers().get("Pogo"));
        items.add((TiledMapTileLayer)mapa.getLayers().get("VidaExtra"));
        puertas.add((TiledMapTileLayer)mapa.getLayers().get("PuertaCerrada"));
        puertas.add((TiledMapTileLayer)mapa.getLayers().get("Puerta1"));
        puertas.add((TiledMapTileLayer)mapa.getLayers().get("Puerta2"));
        puertas.add((TiledMapTileLayer)mapa.getLayers().get("Puerta3"));
        puertas.add((TiledMapTileLayer) mapa.getLayers().get("Puerta4"));
        guardado.add((TiledMapTileLayer)mapa.getLayers().get("Guardado"));
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

    public boolean colisionX(float x, float y){
        return colision(x, y, plataformaX);
    }

    public boolean colisionY(float x, float y){
        return colision(x,y,plataformaY);
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

    public int colisionPuerta(float x, float y) {
        for(TiledMapTileLayer capa: puertas){
            if(capa!=null){
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

    public float getPosition(int i) {
        return posicionY[index(i)];
    }

    public void setEnemigos(Array<Enemigo> enemigos){
        this.enemigos = enemigos;
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
        return enemigos;
    }

    public void dispose() {
        mapa.dispose();
    }

    public void reiniciar(GameInfo gameInfo) {
        MapLayers layers = mapa.getLayers();
        for(MapLayer mapLayer: layers){
            mapLayer.setVisible(true);
        }
        if(gameInfo.isPogo()&&mapa.getLayers().get("Pogo")!=null)
            mapa.getLayers().get("Pogo").setVisible(false);
        if(gameInfo.isCapita()&&mapa.getLayers().get("Capita")!=null)
            mapa.getLayers().get("Capita").setVisible(false);
        if(gameInfo.isLanzapapas()&&mapa.getLayers().get("Lanzapapas")!=null)
            mapa.getLayers().get("Lanzapapas").setVisible(false);

    }
}
