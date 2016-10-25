package mx.itesm.lightsgone;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

/**
 * Created by allanruiz on 19/09/16.
 */
public class Abner {
    public static final int X = 350;
    public static final int SALTOMAX = 280;
    private float xOriginal, yOriginal;
    private Sprite sprite;
    private int cont = 8;
    private float y = 135f, saltoMov = 8f, gravedad = 13f, alturaMax;
    private Texture neutral, saltar1, saltar2, pResortera;
    private float mov = 25f;
    private final float MOVY = (0.2125f)*mov;
    private Salto salto;
    private int cantVida;
    private Animation caminar, atacar;
    private OrthographicCamera camara;
    private ArrayList<Proyectil> proyectiles;
    private Mapa mapa;
    private float timerAnimation, timerAnimationA;
    private Ataque estadoAtaque;
    private Vertical estadoSalto;
    private Horizontal estadoHorizontal;
    private Rectangle ProyectilRectangulo=new Rectangle();
    public boolean pogo, capita, lanzapapas;
    private boolean muerte;
    private Texture saltarPogo1, saltarPogo2;
    private boolean arrastrado;

    public Abner(Texture texture, Texture correr1, Texture correr2, Texture saltar1, Texture saltar2, Texture resortera1,
                 Texture resortera2, Texture resortera3, Texture pResortera,Texture saltarPogo1,Texture saltarPogo2,OrthographicCamera camara, Mapa mapa, GameInfo gameInfo){
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
    }


    public void draw(SpriteBatch batch, boolean right){
        sprite.draw(batch);
        actualizar(right);
    }

    private void actualizar(boolean right) {

        if(cantVida<= 0||sprite.getY()<=0)
            muerte =true;

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

        if(estadoHorizontal == Horizontal.INCLINADO){
            sprite.setRotation(12);
            if(estadoSalto==Vertical.DESACTIVADO&&estadoAtaque!= Ataque.ACTIVADO){
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
            if(estadoSalto==Vertical.DESACTIVADO&&estadoAtaque!= Ataque.ACTIVADO){
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
            if(estadoAtaque != Ataque.ACTIVADO){
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
            attack(right);
        }
        else{
            estadoAtaque = Ataque.DESACTIVADO;
        }

        if(estadoAtaque != Ataque.ACTIVADO && estadoSalto == Vertical.DESACTIVADO && estadoHorizontal==Horizontal.DESACTIVADO){
            if(mapa.colisionY(sprite.getX()+sprite.getWidth()/2,sprite.getY()-(saltoMov+gravedad))||mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad)))sprite.setTexture(neutral);
            else{
                estadoSalto = Vertical.ACTIVADO;
                salto = Salto.BAJANDO;
            }
        }

        if(limiteCamara())
            camara.position.set(camara.position.x, 265 + sprite.getY(), 0);
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
            if (!mapa.colisionX((sprite.getX() + (sprite.getWidth() / 2)) + mov, sprite.getY()+20)&&!mapa.colisionInclinada((sprite.getX() + (sprite.getWidth() / 2)) + mov, sprite.getY())&&!arrastrado){
                if(mapa.colisionY(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad)) || estadoSalto != Vertical.DESACTIVADO|| mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad))){
                    sprite.translate(mov, 0);
                    if(limiteCamaraX())
                        camara.translate(mov,0);
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
            if (!mapa.colisionX((sprite.getX() + (sprite.getWidth() / 2)) - mov, sprite.getY()+20)&&!mapa.colisionInclinada((sprite.getX() + (sprite.getWidth() / 2))- mov, sprite.getY())&&!arrastrado) {
                if (mapa.colisionY(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad)) || estadoSalto != Vertical.DESACTIVADO|| mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad))){
                    sprite.translate(-mov, 0);
                    if(limiteCamaraX())
                        camara.translate(-mov,0);
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
            if(lata.getBoundingRectangle().contains(sprite.getX()+(3*sprite.getWidth()/4), sprite.getY()+10)) {
                arrastrado = true;
            }
            else{
                arrastrado=false;
            }
        }
        return arrastrado;

    }

    public boolean isJumping(){
        return (estadoSalto != Vertical.DESACTIVADO);
    }


    public void jump(float alturaMax){

        switch (salto){
            case SUBIENDO:
                sprite.setY(sprite.getY() + saltoMov);
                if(sprite.getY()>= alturaMax){
                    sprite.setY(alturaMax);
                    salto = Salto.BAJANDO;
                }
                break;
            case BAJANDO:

                if (mapa.colisionY(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad))||mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (estadoSalto==Vertical.POGO?saltoMov + gravedad+10:saltoMov + gravedad))) {
                    estadoSalto = Vertical.DESACTIVADO;
                    this.alturaMax = sprite.getY() + SALTOMAX;
                    sprite.setSize(xOriginal, yOriginal);
                }
                else
                    sprite.setY(sprite.getY() - (saltoMov + gravedad));
                break;

        }
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
        Gdx.app.log("Mapa: ", mapa.getHeight()+" Abner: "+ sprite.getY()+ " y: "+y);
        return sprite.getY()<mapa.getHeight()-810 && sprite.getY()>=135;
    }

    public boolean isAttacking() {
        return (estadoAtaque == Ataque.ACTIVADO);
    }

    public Rectangle getBoundingRectangle(){
        return sprite.getBoundingRectangle();
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
        cantVida=vida;
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

    public void impactoFuegoX(){

        for (int i=0; i<=50;i++) {
            sprite.setX(sprite.getX() - (float).1);
            camara.position.set(camara.position.x -(float) .1, sprite.getY(), 0);
        }
    }

    public void setInitialPosition(int i) {
        float y = mapa.getPosition(i);
        boolean right = mapa.getRight(i);
        if(right){
            sprite.setPosition(X, y);
            camara.position.set(640, 275+sprite.getY(),0);
        }
        else{
            sprite.setPosition(mapa.getWidth()-450, y);
            camara.position.set(mapa.getWidth()-640, 275+sprite.getY(),0);
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
