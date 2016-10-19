package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private Array<TiledMapTileLayer> puertas,plataformaY, plataformaX,encima, items;
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
        items = new Array<TiledMapTileLayer>(1);
        encima = new Array<TiledMapTileLayer>(1);
        puertas = new Array<TiledMapTileLayer>(5);
        plataformaY.add((TiledMapTileLayer)mapa.getLayers().get("PlataformaPiso"));
        plataformaY.add((TiledMapTileLayer)mapa.getLayers().get("Plataformas"));
        plataformaY.add((TiledMapTileLayer)mapa.getLayers().get("Plataformas2"));
        plataformaX.add((TiledMapTileLayer)mapa.getLayers().get("ParedPuerta"));
        plataformaX.add((TiledMapTileLayer)mapa.getLayers().get("PlataformasNoCruzar"));
        encima.add((TiledMapTileLayer)mapa.getLayers().get("CapaEncima"));
        items.add((TiledMapTileLayer)mapa.getLayers().get("Malteada"));
        puertas.add((TiledMapTileLayer)mapa.getLayers().get("PuertaCerrada"));
        puertas.add((TiledMapTileLayer)mapa.getLayers().get("Puerta1"));
        puertas.add((TiledMapTileLayer)mapa.getLayers().get("Puerta2"));
        puertas.add((TiledMapTileLayer)mapa.getLayers().get("Puerta3"));
        puertas.add((TiledMapTileLayer) mapa.getLayers().get("Puerta4"));
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
            if(capa!=null){
                TiledMapTileLayer.Cell cell = capa.getCell((int) (x / capa.getTileWidth()), (int) (y / capa.getTileHeight()));
                if (cell != null) return true;
            }
        }
        return false;
    }

    public boolean colisionItem(float x, float y){
        return colision(x,y,items);
    }

    public void draw(){
        renderer.setView(camara);
        renderer.render();

        batch.begin();
        if(plataformasInclinada != null) {
            for (Sprite sprite: plataformasInclinada)
                sprite.draw(batch);
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
        mapa.getLayers().remove(mapa.getLayers().get(layer));
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

    private int index(int i) {
        int j;
        for(j = 0;j<numPuertas.length;j++){
            if(numPuertas[j]==i)
                break;
        }
        return j;
    }

    public boolean getRight(int i) {
        return right[index(i)];
    }

    public boolean colisionInclinada(float x, float y){
        if(plataformasInclinada!=null){
            for(Sprite sprite1: plataformasInclinada){
                float ancho = sprite1.getBoundingRectangle().getWidth();
                float alto = sprite1.getBoundingRectangle().getHeight();
                float extremoderecho = sprite1.getX()+ancho;
                float rec_y = 1160;
                float inc_y = alto/ancho;
                float ys = (x-sprite1.getX())*inc_y;
                float altura = rec_y + ys+10;
                if((sprite1.getX()<=x&&x<=extremoderecho)&&(rec_y<=y&&y<=altura))
                    return true;
            }
        }
        return false;

    }


    public void dispose() {
        mapa.dispose();
    }
}
