package mx.itesm.lightsgone;

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
    private Array<TiledMapTileLayer> plataformaY, plataformaX,encima, items;
    private static AssetManager manager = new AssetManager();
    private static OrthographicCamera camara;
    private Array<TiledMapTileLayer> puertas;
    private Array<Enemigo> enemigos;
    private SpriteBatch batch;

    public Mapa(String mapa, SpriteBatch batch, OrthographicCamera camara){
        this.mapa = cargarMapa(mapa);
        Mapa.camara = camara;
        renderer = new OrthogonalTiledMapRenderer(this.mapa, batch);
        setPlataformaY("Plataformas2", "Plataformas", "PlataformaPiso");
        setPlataformaX("ParedPuerta", "PlataformasNoCruzar");
        setEncima("CapaEncima");
        setItem("Malteada");
        puertas = new Array<TiledMapTileLayer>(3);
        puertas.add((TiledMapTileLayer) this.mapa.getLayers().get("PuertaCerrada"));
        puertas.add((TiledMapTileLayer) this.mapa.getLayers().get("PuertaDerecha"));
        puertas.add((TiledMapTileLayer) this.mapa.getLayers().get("PuertaIzquierda"));
    }

    private TiledMap cargarMapa(String mapa) {
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        manager.load(mapa, TiledMap.class);
        manager.finishLoading();
        return manager.get(mapa);
    }


    private void setPlataformaY(String... capas){
        plataformaY = new Array<TiledMapTileLayer>(capas.length);
        for(String s: capas)
            plataformaY.add((TiledMapTileLayer)mapa.getLayers().get(s));

    }

    private void setPlataformaX(String... capas){
        plataformaX = new Array<TiledMapTileLayer>(capas.length);
        for(String s: capas)
            plataformaX.add((TiledMapTileLayer)mapa.getLayers().get(s));

    }

    private void setEncima(String... capas){
        encima = new Array<TiledMapTileLayer>(capas.length);
        for(String s: capas){
            encima.add((TiledMapTileLayer)mapa.getLayers().get(s));
        }
    }

    private void setItem(String... capas){
        items = new Array<TiledMapTileLayer>(capas.length);
        for(String s: capas){
            items.add((TiledMapTileLayer) mapa.getLayers().get(s));
        }
    }

    public boolean colisionX(float x, float y){
        return colision(x,y,plataformaX);
    }

    public boolean colisionY(float x, float y){
        return colision(x,y,plataformaY);
    }

    public boolean colision(float x, float y, Array<TiledMapTileLayer> capas){
        for(TiledMapTileLayer capa: capas){
            TiledMapTileLayer.Cell cell = capa.getCell((int) (x / capa.getTileWidth()), (int) (y / capa.getTileHeight()));
            if (cell != null) return true;

        }
        return false;
    }

    public boolean colisionItem(float x, float y){
        return colision(x,y,items);
    }

    public void draw(){
        renderer.setView(camara);
        renderer.render();
    }

    public void drawE(){
        for(TiledMapTileLayer layer: encima)
            renderer.renderTileLayer(layer);
    }

    public void remove(String layer){
        mapa.getLayers().remove(mapa.getLayers().get(layer));
    }

    public int colisionPuerta(float x, float y) {
        for(TiledMapTileLayer capa: puertas){
            if(capa!=null){
                TiledMapTileLayer.Cell cellDer = capa.getCell((int)(x/capa.getTileWidth()), (int)(y/capa.getTileHeight()));
                TiledMapTileLayer.Cell cellIzq = capa.getCell((int)((x-250)/capa.getTileWidth()), (int)(y/capa.getTileHeight()));
                if((cellDer != null||cellIzq!=null) && capa.getName().equals("PuertaCerrada"))
                    return 0;
                if((cellDer != null||cellIzq!=null) && capa.getName().equals("PuertaIzquierda"))
                    return -1;
                if((cellDer != null||cellIzq!=null) && capa.getName().equals("PuertaDerecha"))
                    return 1;
            }
        }
        return 0;
    }

    public float getWidth(){
        TiledMapTileLayer layer = (TiledMapTileLayer)mapa.getLayers().get(0);
        return layer.getWidth()*layer.getTileWidth();
    }

    public float getHeight(){
        TiledMapTileLayer layer = (TiledMapTileLayer)mapa.getLayers().get(0);
        return layer.getHeight()*layer.getTileHeight();
    }
}
