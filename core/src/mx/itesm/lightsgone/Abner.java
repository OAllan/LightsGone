package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

/**
 * Created by allanruiz on 19/09/16.
 */
public class Abner {
    public static final int X = 450;
    public static final int SALTOMAX = 280;
    public static final int CAMARAINICIAL = 640;
    private final Sprite fondo;
    private float xOriginal, yOriginal;
    private Sprite sprite;
    private int cont = 8;
    private float y = 135f, saltoMov = 8f, gravedad = 13f, alturaMax, alpha = 1;
    private Texture neutral, saltar1, saltar2, pResortera, dano;
    private float mov = 10f;
    private final float MOVY = (0.2125f)*mov;
    private Salto salto;
    private int cantVida, vidas;
    private Animation caminar, atacar;
    private OrthographicCamera camara;
    private ArrayList<Proyectil> proyectiles;
    private Mapa mapa;
    private float timerAnimation, timerAnimationA, timerDano, timerDanoAlpha;
    private Ataque estadoAtaque;
    private Vertical estadoSalto;
    private Horizontal estadoHorizontal;
    private Rectangle ProyectilRectangulo=new Rectangle();
    public boolean pogo, capita, lanzapapas, danoA, danoB, pisoLata;
    private boolean muerte;
    private Texture saltarPogo1, saltarPogo2;
    private boolean arrastrado, arrastradoPiso;
    private  int direccion;
    private float movPiso;

    public Abner(Texture texture, Texture correr1, Texture correr2, Texture saltar1, Texture saltar2, Texture resortera1,
                 Texture resortera2, Texture resortera3, Texture pResortera,Texture saltarPogo1,Texture saltarPogo2,Texture dano,OrthographicCamera camara, Mapa mapa, GameInfo gameInfo, Sprite fondo){
        this.neutral = texture;
        xOriginal = texture.getWidth();
        yOriginal = texture.getHeight();
        this.saltar1 = saltar1;
        this.saltar2 = saltar2;
        this.pResortera = pResortera;
        this.caminar = new Animation(0.15f, new TextureRegion(correr1), new TextureRegion(correr2));
        caminar.setPlayMode(Animation.PlayMode.LOOP);
        this.atacar = new Animation(0.2f, new TextureRegion(resortera1),new TextureRegion(resortera2),new TextureRegion(resortera3));
        this.atacar.setPlayMode(Animation.PlayMode.NORMAL);
        sprite = new Sprite(neutral);
        sprite.setPosition(gameInfo.getX(), gameInfo.getY());
        timerAnimation = 0;
        timerAnimationA = 0;
        salto = Salto.BAJANDO;
        this.camara = camara;
        this.camara.position.set(gameInfo.getCamaraX(), gameInfo.getCamaraY(), 0);
        this.proyectiles = new ArrayList<Proyectil>(50);
        this.mapa =mapa;
        alturaMax = sprite.getY() + SALTOMAX;
        estadoHorizontal = Horizontal.DESACTIVADO;
        estadoAtaque = Ataque.DESACTIVADO;
        estadoSalto = Vertical.DESACTIVADO;
        cantVida = gameInfo.getVida();
        this.pogo = gameInfo.isPogo();
        this.capita = gameInfo.isCapita();
        this.lanzapapas = gameInfo.isLanzapapas();
        this.saltarPogo1 = saltarPogo1;
        this.saltarPogo2 = saltarPogo2;
        arrastrado = false;
        this.vidas = gameInfo.getVidas();
        this.dano = dano;
        danoA = false;
        timerDano = 0.5f;
        timerDanoAlpha =3;
        this.fondo = fondo;
    }


    public void draw(SpriteBatch batch, boolean right){
        sprite.draw(batch);
        actualizar(right);
    }

    private void actualizar(boolean right) {

        Gdx.app.log("Abner: ", sprite.getY()+" Camara: "+ camara.position.x);


        if(sprite.getY()<=0||mapa.colisionMuerte(sprite.getX(),sprite.getY()))
            muerte = true;

        if(cantVida<=0&&vidas<=0)
            muerte =true;

        else if(cantVida<=0&&vidas>=1) {
            vidas--;
            cantVida = 99;
        }

        if(danoA){
            sprite.setTexture(dano);
            sprite.setSize(dano.getWidth(), dano.getHeight());
            timerDano-= Gdx.graphics.getDeltaTime();
            if(timerDano<=0){
                timerDano=0.5f;
                sprite.setSize(xOriginal, yOriginal);
                danoA = false;
                danoB = true;
            }
        }

        if(danoB){
            timerDanoAlpha -= Gdx.graphics.getDeltaTime();
            for(float time = 0;time<3;time+=0.3f) {
                if (timerDanoAlpha<time){
                    if(alpha==0.7f)
                        alpha = 1;
                    else if(alpha==1)
                        alpha = 0.7f;
                }
            }
            sprite.setAlpha(alpha);
            if(timerDanoAlpha<=0){
                timerDanoAlpha = 3;
                danoB = false;
                sprite.setAlpha(1);
            }
        }

        if(mapa.colisionDano(sprite.getX()+sprite.getWidth()/2, sprite.getY()+20)&&!danoA&!danoB){
            cantVida-=5;
            danoA = true;
        }


        if(mapa.colisionItem(sprite.getX()+sprite.getWidth(),sprite.getY(),"VidaExtra")){
            mapa.remove("VidaExtra");
            if(vidas<3)
                vidas++;
        }

        if(mapa.colisionItem(sprite.getX()+sprite.getWidth(), sprite.getY(), "Malteada")){
            mapa.remove("Malteada");
            if((cantVida + 10)<=99) cantVida+=10;
            else cantVida = 99;
        }

        if(mapa.colisionItem(sprite.getX()+(3*sprite.getWidth()/4), sprite.getY()+20,"Pogo")) {
            mapa.remove("Pogo");
            pogo = true;
        }

        if(mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad))&&estadoHorizontal == Horizontal.ACTIVADO)
            estadoHorizontal = Horizontal.INCLINADO;



        if(arrastrado){
            sprite.translate(-mov,-MOVY);
            camara.translate(-mov,0);
        }
        if(arrastradoPiso){
            if(!mapa.colisionX((sprite.getX() + (sprite.getWidth() / 2)) + mov, sprite.getY()+20)&&!mapa.colisionX((sprite.getX() + (sprite.getWidth() / 2)) - mov, sprite.getY()+20)){
                sprite.translate(movPiso, 0);
                if(limiteCamaraX())
                    camara.translate(movPiso,0);
            }
            else
                arrastradoPiso =false;
        }

        if(estadoHorizontal == Horizontal.INCLINADO){
            sprite.setRotation(12);
            if(estadoSalto==Vertical.DESACTIVADO&&estadoAtaque!= Ataque.ACTIVADO&&!danoA){
                timerAnimation += Gdx.graphics.getDeltaTime();
                sprite.setTexture(caminar.getKeyFrame(timerAnimation).getTexture());
                walk(right);
            }
            else
                walk(right);
            if(!arrastrado){
                sprite.translate(0, right?MOVY:-MOVY);
                camara.translate(0, right?MOVY:-MOVY);
            }
            alturaMax = sprite.getY() +SALTOMAX;
        }


        else if(estadoHorizontal == Horizontal.ACTIVADO){
            sprite.setRotation(0);
            if(estadoSalto==Vertical.DESACTIVADO&&estadoAtaque!= Ataque.ACTIVADO&&!danoA){
                timerAnimation += Gdx.graphics.getDeltaTime();
                sprite.setTexture(caminar.getKeyFrame(timerAnimation).getTexture());
                walk(right);
            }
            else
                walk(right);
        }

        if(estadoSalto==Vertical.POGO) {
            if (estadoAtaque != Ataque.ACTIVADO) {
                if (cont >= 0) {
                    sprite.setTexture(saltarPogo1);
                    sprite.setSize(saltarPogo1.getWidth(), saltarPogo1.getHeight());
                    cont--;
                } else {
                    sprite.setTexture(saltarPogo2);
                    sprite.setSize(saltarPogo2.getWidth(), saltarPogo2.getHeight());
                    jump(alturaMax + SALTOMAX);
                }
            }
        }

        else if(estadoSalto == Vertical.ACTIVADO){
            if(estadoAtaque != Ataque.ACTIVADO&&!danoA){
                if(cont>=0){
                    sprite.setTexture(saltar1);
                    cont--;
                }
                else {
                    sprite.setTexture(saltar2);
                    jump(alturaMax);
                }
            }
            else
                jump(alturaMax);
        }


        if(estadoAtaque == Ataque.ACTIVADO&&estadoSalto!= Vertical.POGO){
            if(!danoA)
                attack(right);
            else
                estadoAtaque = Ataque.DESACTIVADO;
        }
        else{
            estadoAtaque = Ataque.DESACTIVADO;
        }

        if(estadoAtaque != Ataque.ACTIVADO && estadoSalto == Vertical.DESACTIVADO && estadoHorizontal==Horizontal.DESACTIVADO&&!danoA){
            if(mapa.colisionY(sprite.getX()+sprite.getWidth()/2,sprite.getY()-(saltoMov+gravedad))||mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad))||pisoLata)sprite.setTexture(neutral);
            else{
                estadoSalto = Vertical.ACTIVADO;
                salto = Salto.BAJANDO;
            }
        }

        if(limiteCamara()){
            camara.position.set(camara.position.x, 265 + sprite.getY(), 0);
        }
        camara.update();
    }

    private void attack(boolean right){
        timerAnimationA += Gdx.graphics.getDeltaTime();
        sprite.setTexture(atacar.getKeyFrame(timerAnimationA).getTexture());
        if(timerAnimationA>=0.1&&timerAnimationA<(0.1+Gdx.graphics.getDeltaTime())){
            proyectiles.add(new Proyectil(pResortera, sprite.getX() + 142, sprite.getY()+138, right));
        }
        else if (timerAnimationA>atacar.getAnimationDuration()) {
            estadoAtaque = Ataque.DESACTIVADO;
            timerAnimationA = 0;
        }
    }


    public void walk(boolean right){

        if (right){
            if(sprite.isFlipX()){
                sprite.flip(true, false);
            }
            if (!mapa.colisionX((sprite.getX() + (sprite.getWidth() / 2)) + mov, sprite.getY()+20)&&!mapa.colisionInclinada((sprite.getX() + (sprite.getWidth() / 2)) + mov, sprite.getY())&&!arrastrado&&!arrastradoPiso){
                if(mapa.colisionY(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad)) || estadoSalto != Vertical.DESACTIVADO|| mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad))||pisoLata){
                    sprite.translate(mov, 0);
                    if(limiteCamaraX()){
                        camara.translate(mov,0);
                        fondo.translate(mov, 0);
                    }
                    else if(sprite.getX()<=530)
                        camara.position.x = Nivel0.ANCHO_MUNDO/2;
                }
                else {
                    estadoSalto = Vertical.ACTIVADO;
                    salto = Salto.BAJANDO;
                }
            }
        }
        else {
            if(!sprite.isFlipX()){
                sprite.flip(true, false);
            }
            if (!mapa.colisionX((sprite.getX() + (sprite.getWidth() / 2)) - mov, sprite.getY()+20)&&!mapa.colisionInclinada((sprite.getX() + (sprite.getWidth() / 2))- mov, sprite.getY())&&!arrastrado&&!arrastradoPiso) {
                if (mapa.colisionY(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad)) || estadoSalto != Vertical.DESACTIVADO|| mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad))||pisoLata){
                    sprite.translate(-mov, 0);
                    if(limiteCamaraX()){
                        camara.translate(-mov,0);
                        fondo.translate(-mov, 0);
                    }
                }
                else {
                    estadoSalto = Vertical.ACTIVADO;
                    salto = Salto.BAJANDO;
                }
            }
        }

        camara.update();


    }

    private boolean limiteCamaraX() {
        return sprite.getX()>530&&sprite.getX()<mapa.getWidth()-770;
    }

    public void setEstadoAtaque(Ataque estado) {
        this.estadoAtaque = estado;
    }
    public void setEstadoVertical(Vertical estado) {
        this.estadoSalto = estado;
    }

    public void setEstadoHorizontal(Horizontal estado) {
        this.estadoHorizontal = estado;
    }

    public boolean colisionEnemigo(Enemigo enemigo){
        if(enemigo.toString().equalsIgnoreCase("Lata")){
            Enemigo.Lata lata= (Enemigo.Lata)enemigo;
            if(lata.getBoundingRectangle().contains(sprite.getX() + (3 * sprite.getWidth() / 4), sprite.getY() + 10)&&!lata.piso()) {
                arrastrado = true;
            }
            else{
                arrastrado=false;
            }
            if((lata.getBoundingRectangle().contains(sprite.getX()+(sprite.getWidth()/2)-mov, sprite.getY()+10)||lata.getBoundingRectangle().contains(sprite.getX()+(3*sprite.getWidth()/4)+mov, sprite.getY()+10))&&lata.piso()){
                arrastradoPiso = true;
                movPiso = lata.getMov();
            }
            else{
                arrastradoPiso = false;
            }
            if(lata.getBoundingRectangle().contains(sprite.getX()+(3*sprite.getWidth()/6), sprite.getY()-(saltoMov)))
                pisoLata = true;
            else
                pisoLata = false;
        }
        return arrastrado||pisoLata;

    }

    public boolean isJumping(){
        return (estadoSalto != Vertical.DESACTIVADO);
    }


    public void jump(float alturaMax){

        switch (salto){
            case SUBIENDO:
                sprite.setY(sprite.getY() + saltoMov);
                if(limiteCamara())
                    fondo.translate(0,saltoMov);
                if(sprite.getY()>= alturaMax){
                    sprite.setY(alturaMax);
                    salto = Salto.BAJANDO;
                }
                break;
            case BAJANDO:

                if (mapa.colisionY(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad))||mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (estadoSalto==Vertical.POGO?saltoMov + gravedad+10:saltoMov + gravedad))||pisoLata) {
                    estadoSalto = Vertical.DESACTIVADO;
                    this.alturaMax = sprite.getY() + SALTOMAX;
                    sprite.setSize(xOriginal, yOriginal);
                }
                else{
                    sprite.setY(sprite.getY() - (saltoMov + gravedad));
                    if(limiteCamara())
                        fondo.translate(0,-(saltoMov+gravedad));
                }
                break;

        }
    }

    public void setDano(boolean dano){
        this.danoA = dano;
    }

    public boolean getDano(){
        return this.danoA||this.danoB;
    }

    public void setSalto(Salto salto) {
        this.salto = salto;
    }

    public float getX(){
        return sprite.getX();
    }

    private boolean limiteCamara(){
        if(mapa.getHeight()<= 1260)
            return false;
        return sprite.getY()<mapa.getHeight()-810 && sprite.getY()>=135;
    }

    public boolean isAttacking() {
        return (estadoAtaque == Ataque.ACTIVADO);
    }

    public Rectangle getBoundingRectangle(){
        return new Rectangle(sprite.getX()+70,sprite.getY(),sprite.getWidth()-44,sprite.getHeight()-59);
    }

    public ArrayList<Proyectil> getProyectiles() {
        return proyectiles;
    }

    public void setMapa(Mapa mapa){
        this.mapa = mapa;
    }

    public float getY() {
        return sprite.getY();
    }

    public int getcantVida(){
        return cantVida;
    }

    public boolean guardar(){
        return mapa.colisionGuardado(sprite.getX()+sprite.getWidth()/2, sprite.getY()+sprite.getHeight()/2);
    }

    public void setCantVida(int vida){
        if(vida>99)
            cantVida=99;
        else
            cantVida = vida;
    }

    public int cambioNivel() {
        return mapa.colisionPuerta(sprite.getX()+3*(sprite.getWidth()/4) + mov, sprite.getY());
    }

    public boolean isDead(){
        return muerte;
    }

    public Rectangle getProyectilRectangulo(){
        return ProyectilRectangulo;
    }

    public void borrarProyectiles(){
        if(!proyectiles.isEmpty())
            proyectiles.remove(0);
    }

    public void ajusteCamara(int direccion){

            //sprite.setX(sprite.getX() - (float).1);
            camara.position.set(camara.position.x - (float) 1 * direccion, sprite.getY(), 0);

    }

    public void setX(float x){
        sprite.setX(x);
    }

    public void setInitialPosition(int i) {
        float y = mapa.getPositionY(i);
        float x = mapa.getPositionX(i);
        sprite.setSize(xOriginal, yOriginal);
        sprite.setPosition(x,y);
        if(x<530){
            fondo.setPosition(0,0);
            camara.position.set(Nivel0.ANCHO_MUNDO/2, camara.position.y, 0);
        }
        else if(x>mapa.getWidth()-770) {
            fondo.setPosition(mapa.getWidth()-fondo.getWidth(), 0);
            camara.position.set(mapa.getWidth() - Nivel0.ANCHO_MUNDO / 2, camara.position.y, 0);
        }
        else {
            fondo.setPosition(x-530,0);
            camara.position.set(110 + x, camara.position.y, 0);
        }

        if(y>mapa.getHeight()-810){
            camara.position.set(camara.position.x, mapa.getHeight()-Nivel0.ALTO_MUNDO/2, 0);
            fondo.setPosition(fondo.getX(), mapa.getHeight()-fondo.getHeight());
        }
        else if(y<=135) {
            camara.position.set(camara.position.x, Nivel0.ALTO_MUNDO / 2, 0);
            fondo.setPosition(fondo.getX(), 0);
        }
        else{
            camara.position.set(camara.position.x, 265+y, 0);
            fondo.setPosition(fondo.getX(), y-135);
        }
        alturaMax = sprite.getY() + SALTOMAX;
        this.y = y;
    }

    public boolean getPogo() {
        return pogo;
    }

    public boolean getCapita() {
        return capita;
    }

    public boolean getLanzapapas() {
        return lanzapapas;
    }

    public float getCamaraX() {
        return camara.position.x;
    }

    public float getCamaraY(){
        return camara.position.y;
    }

    public float getHeight(){return sprite.getHeight();}

    public float getWidth(){return sprite.getWidth();}

    public void reiniciar(GameInfo gameInfo) {
        sprite.setPosition(gameInfo.getX(), gameInfo.getY());
        camara.position.set(gameInfo.getCamaraX(), gameInfo.getCamaraY(), gameInfo.getY());
        alturaMax = sprite.getY() + SALTOMAX;
        estadoHorizontal = Horizontal.DESACTIVADO;
        estadoAtaque = Ataque.DESACTIVADO;
        estadoSalto = Vertical.DESACTIVADO;
        cantVida = 99;
        pogo = gameInfo.isPogo();
        capita = gameInfo.isCapita();
        lanzapapas = gameInfo.isLanzapapas();
        muerte = false;
        danoB = false;
        danoA = false;
        sprite.setSize(xOriginal, yOriginal);
    }

    public int getVidas() {
        return vidas;
    }

    public enum Salto{
        SUBIENDO,
        BAJANDO
    }

    public enum Horizontal{
        ACTIVADO,
        INCLINADO,
        DESACTIVADO
    }

    public enum Ataque{
        ACTIVADO,
        DESACTIVADO
    }

    public enum Vertical{
        ACTIVADO,
        DESACTIVADO,
        POGO
    }


}
