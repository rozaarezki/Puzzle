package p8.demo.p8sokoban;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SokobanView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Bitmap 		rouge;
    private Bitmap 		bleu;
    private Bitmap 		vide;
    private Bitmap 		vert;
    private Bitmap 	    orange;
    private Bitmap      fond;
    private Bitmap 		win;
    private static int DIALOG_ALERT = 0;
    public  AlertDialog.Builder dialogBuilder;
    boolean etape=false;
    boolean etape1=false;
    boolean execute=true;
    boolean execute1=true;
    boolean sup=true;
    p8_Sokoban activ;
    p8_Sokoban activ1;

    public double xposition=0 ,yposition=0;
    int xx=0;
    int yy=0;
    int level=0;
    Paint text = new Paint();
    int coup=0;

    private Context 	mContext;
    private Resources 	mResou;


    int[][] carte;

    // ancres pour pouvoir centrer la carte du jeu
    int        carteTopAnchor;                   // coordonn�es en Y du point d'ancrage de notre carte
    int        carteLeftAnchor;                  // coordonn�es en X du point d'ancrage de notre carte

    // taille de la carte
    static final int    carteWidth    = 6;
    static final int    carteHeight   = 7;
    static final int    carteTileSize = 50;

    // constante modelisant les differentes types de cases
    static final int    CST_bleu     = 0;
    static final int    CST_rouge  = 1;
    static final int    CST_vert     = 2;
    static final int    CST_orange     = 3;
    static final int    CST_vide      = 4;




    // tableau de reference du terrain
    int [][][] ref    = {
            {
                    {CST_vide, CST_vide, CST_vide,CST_vide, CST_vide, CST_vide },
                    {CST_vide, CST_vide, CST_vide,CST_vide, CST_vide, CST_vide },
                    {CST_vide, CST_vide, CST_bleu,CST_vide, CST_vide, CST_vide  },
                    {CST_vide, CST_vide, CST_vert, CST_vide, CST_vide, CST_vide },
                    {CST_vide, CST_vide, CST_vert, CST_rouge, CST_vide, CST_vide },
                    {CST_vide, CST_vide, CST_rouge, CST_vert, CST_rouge, CST_vide},
                    {CST_vide, CST_bleu, CST_vert, CST_bleu, CST_bleu, CST_vide }
            },{
            {CST_vide, CST_vide, CST_vide,CST_vide, CST_vide, CST_vide },
            {CST_vide, CST_vide, CST_vide,CST_vide, CST_vide, CST_vide },
            {CST_vide, CST_vide, CST_bleu,CST_bleu, CST_vide, CST_vide  },
            {CST_vide, CST_bleu, CST_rouge, CST_bleu, CST_vide, CST_vide },
            {CST_vide, CST_rouge, CST_bleu, CST_vert, CST_vide, CST_vide },
            {CST_vide, CST_bleu, CST_vert, CST_bleu, CST_vert, CST_vide},
            {CST_rouge, CST_bleu, CST_rouge, CST_bleu, CST_rouge, CST_rouge }
    },{
            {CST_vide, CST_vide, CST_vide,CST_vide, CST_vide, CST_vide },
            {CST_vide, CST_vide, CST_vide,CST_vide, CST_vide, CST_vide },
            {CST_vide, CST_vide, CST_bleu,CST_vide, CST_vide, CST_vide  },
            {CST_vide, CST_vide, CST_vert, CST_vide, CST_vide, CST_vide },
            {CST_vide, CST_vide, CST_vert, CST_rouge, CST_vide, CST_vide },
            {CST_vide, CST_rouge, CST_rouge, CST_vert, CST_rouge, CST_vide},
            {CST_vide, CST_bleu, CST_vert, CST_bleu, CST_bleu, CST_vide }
    }

    };






    // thread utiliser pour animer les zones de depot des diamants
    private     boolean in      = true;
    private     Thread  cv_thread;
    SurfaceHolder holder;

    Paint paint;




    public SokobanView(Context context, AttributeSet attrs) {
        super(context, attrs);

        activ= new p8_Sokoban();
        activ1= new p8_Sokoban();

        // permet d'ecouter les surfaceChanged, surfaceCreated, surfaceDestroyed
        holder = getHolder();
        holder.addCallback(this);

        // chargement des images
        mContext	= context;
        mResou 		= mContext.getResources();
        bleu 		= BitmapFactory.decodeResource(mResou, R.drawable.bleu);
        rouge		= BitmapFactory.decodeResource(mResou, R.drawable.rouge);
        vert		= BitmapFactory.decodeResource(mResou, R.drawable.vert);
        orange   	= BitmapFactory.decodeResource(mResou, R.drawable.orange);
        vide 		= BitmapFactory.decodeResource(mResou, R.drawable.vide1);
        win 		= BitmapFactory.decodeResource(mResou, R.drawable.win);
        fond        = BitmapFactory.decodeResource(mResou, R.drawable.fond);



        // initialisation des parmametres du jeu
        initparameters();

        // creation du thread
        cv_thread   = new Thread(this);
        // prise de focus pour gestion des touches
        setFocusable(true);


    }

    // chargement du niveau a partir du tableau de reference du niveau
    private void loadlevel() {
        for (int i=0; i< carteWidth; i++) {
            for (int j=0; j< carteHeight; j++) {
                carte[j][i]= ref[level][j][i];
            }
        }
    }

    // initialisation du jeu
    public void initparameters() {

        carte           = new int[carteHeight][carteWidth];
        loadlevel();
        carteTopAnchor  = 109;
        carteLeftAnchor = (getWidth())/carteWidth;
        Log.i("hauteur","=="+getHeight());
        Log.i("largeur", "==" + getWidth());


        if ((cv_thread!=null) && (!cv_thread.isAlive())) {
            cv_thread.start();
            Log.e("-FCT-", "cv_thread.start()");
        }
    }



    // dessin du gagne si gagne
    private void paintwin(Canvas canvas) {
        canvas.drawBitmap(win, carteLeftAnchor + 3 * carteTileSize, carteTopAnchor + 4 * carteTileSize, null);

    }


    // dessin de la carte du jeu
    private void paintcarte(Canvas canvas) {
        for (int i=0; i< carteHeight; i++) {
            for (int j=0; j<carteWidth ; j++) {
                switch (carte[i][j]) {
                    case CST_bleu:
                        canvas.drawBitmap(bleu,j*carteTileSize, carteTopAnchor+i*carteTileSize, null);
                        break;
                    case CST_vert:
                        canvas.drawBitmap(vert,j*carteTileSize,  carteTopAnchor+i*carteTileSize, null);
                        break;
                    case CST_vide:
                        canvas.drawBitmap(vide,j*carteTileSize, carteTopAnchor+ i*carteTileSize,null);
                        break;
                    case CST_orange:
                        canvas.drawBitmap(orange,j*carteTileSize, carteTopAnchor+ i*carteTileSize,null);
                        break;
                    case CST_rouge:
                        canvas.drawBitmap(rouge,j*carteTileSize, carteTopAnchor+ i*carteTileSize,null);
                        break;

                }
            }
        }
    }

    private void paintfond(Canvas canvas){
        Rect src=new Rect();
        src.set(0,0,fond.getWidth(),fond.getHeight());
        canvas.drawBitmap(fond,src,canvas.getClipBounds(),null);
    }



    private void paintcoup(Canvas canvas){
        text.setTextSize(13);
        text.setStyle(Paint.Style.FILL_AND_STROKE);
        if (coup==2 ){
            if (isWon()) {
                text.setColor(Color.WHITE);
                canvas.drawText("Bien joué", 10, carteTopAnchor / 5, text);
            }else{
                text.setColor(Color.WHITE);
                canvas.drawText("Perdu", 10, carteTopAnchor / 5, text);

                activ1.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (execute1
                                ) {

                            showAbout2();
                            execute1 = false;
                        }

                    }
                });
                if (etape1 ){
                    coup=0;
                    loadlevel();
                    execute1 = false;
                    etape1=false;
                }
            }
        }
        if (coup==1 ){
            if (isWon()) {
                text.setColor(Color.WHITE);
                canvas.drawText("Bien joué", 10, carteTopAnchor / 5, text);
            }else{
                execute1=true;
                text.setColor(Color.WHITE);
                canvas.drawText("Il vous reste 1 Coup à jouer, niveau: "+(Integer.toString(level+1)),  10, carteTopAnchor / 5, text);
            }
        }
        if (coup==0 ){

            text.setColor(Color.WHITE);
            canvas.drawText("Il vous reste 2 Coups à jouer, niveau: "+(Integer.toString(level+1)), 10, carteTopAnchor / 5, text);

        }




    }





    public boolean isWon() {
        boolean win=true;
        int i=0;
        int j=0;
        while(win & i<carteHeight){
            j=0;
            while(win & j<carteWidth){
                if(carte[i][j]!=CST_vide){
                    win=false;
                }
                j++;
            }
            i++;
        }

        return win;
    }

    private void nDraw(Canvas canvas) {
        paintfond(canvas);

        paintcoup(canvas);

        if (isWon()) {

            activ.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (execute &(level<3)) {
                        showAbout();
                        execute=false;
                    }

                }
            });


            if (etape) {



                if (level < 2) {
                    level++;
                    loadlevel();
                    paintcarte(canvas);
                    coup = 0;
                    etape=false;
                }
            }else {
                paintwin(canvas);
                Log.e("dialog","quitter");
            }


        } else {


            paintcarte(canvas);
            execute=true;


            if(sup) {
                Log.e("ndraw", "eleminer");
                eleminer();
            }



        }


    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("-> FCT <-", "surfaceChanged " + width + " - " + height);
        initparameters();
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceCreated");
    }


    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceDestroyed");
    }




    public void run() {
        Canvas c = null;
        while (in) {
            try {
                cv_thread.sleep(300);

                try {
                    c = holder.lockCanvas(null);
                    nDraw(c);
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                }
            } catch(Exception e) {
                Log.e("-> RUN <-", "PB DANS RUN");
            }
        }
    }


    public void eleminer() {
        Log.e("eleminer","debut");
        sup=false;
        int xdebut=0; int ydebut=0;
        int xfin =0; int yfin=0;
        int valeur=CST_vide;
        for(int i =0 ; i < carteHeight ; i++){
            ydebut=i;
            xdebut=0;
            xfin=0;
            valeur=CST_vide;
            for(int j = 0; j < carteWidth; j++){
                Log.i("hello horizontal","valeur="+valeur+"  carte=="+"["+i+"]"+"["+j+"]==="+carte[i][j]+ "  xdebu=="+xdebut+"  xfin=="+xfin);

                if(carte[i][j] != valeur){
                    Log.i("valeur != de carte ij","nbr carte=="+(xfin-xdebut));
                    if((xfin-xdebut)<2){
                        xdebut=j;
                        xfin=xdebut;
                        valeur=carte[i][j];

                    }else{

                        if(carte[ydebut][xfin]!= CST_vide){

                            int brique=0;

                            for(int k=xdebut ;k<xfin+1;k++){
                                int c=1;
                                brique=carte[ydebut][k];
                                carte[ydebut][k]=CST_vide;
                                int y1=0;
                                int y2=0;
                                while((ydebut-c)>0 &  c<3) {
                                    if(carte[ydebut-c][k]== brique) {

                                        y1 = (ydebut - c);
                                    }

                                    c++;
                                }
                                c=1;
                                while((ydebut+c) <carteHeight &  c<3) {
                                    if (carte[ydebut+c][k]==brique) {
                                        y2 = ydebut + c;
                                        Log.e("yes","carte["+(ydebut+c)+"]["+k+"]=="+carte[ydebut+c][k]+"brique=="+brique);
                                    }
                                    c++;
                                }
                                Log.e("trouver","LL2LLL"+"y2=="+y2+"  y1==="+y1);
                                int mlk=(y2 - y1);
                                if(mlk>=2){
                                    Log.e("trouver","LLLLL"+"y2=="+y2+"  y1==="+y1+"kkkkx=="+k);

                                    for (int l = y1; l < (y2 + 1); l++) {
                                        if (l-(mlk+1)>=0) {
                                            carte[l][k] = carte[l - (mlk+1)][k];
                                            carte[l-(mlk+1)][k]=CST_vide;
                                        } else {
                                            carte[l][k] = CST_vide;
                                        }


                                    }

                                }else{
                                    carte[ydebut][k]=CST_vide;
                                    int p=ydebut;
                                    while (p > 1 & carte[p-1][k]!= CST_vide){
                                        carte[p][k]=carte[p-1][k];
                                        carte[p-1][k]=CST_vide;
                                        p-=1;
                                    }
                                }

                            }

                            if(j<4){
                                xdebut=j;
                                xfin=j;
                            }else{
                                xfin=0;
                                xdebut=0;
                            }
                        }else{
                            valeur=carte[i][j];
                            xdebut=j;
                            xfin=xdebut;
                        }

                    }
                }else{
                    xfin=j;
                }

            }


            if(carte[ydebut][xfin]!= CST_vide) {
                int y1 = ydebut, y2 = ydebut;

                if (xfin - xdebut >= 2) {
                    int brique=0;
                    for (int k = xdebut; k < xfin + 1; k++) {
                        int c = 0;
                        y1=ydebut;y2=ydebut;
                        brique=carte[ydebut][k];
                        while((ydebut-c)>0 &  c<3) {
                            if(carte[ydebut-c][k]==brique) {
                                y1 = ydebut - c;
                            }

                            c++;
                        }
                        c=1;
                        while((ydebut+c) <carteHeight &  c<3) {
                            if (carte[ydebut+c][k]==brique) {
                                y2 = ydebut + c;
                            }
                            c++;
                        }
                        if (y2 - y1 >= 2) {
                            for (int l = y1; l < y2 + 1; l++) {
                                if (l - ((y2 - y1)+1) >= 0) {
                                    carte[l][k] = carte[l - ((y2 - y1)+1)][k];
                                    carte[l - ((y2 - y1)+1)][k] = CST_vide;
                                } else {
                                    carte[l][k] = CST_vide;
                                }


                            }

                        } else {
                            carte[ydebut][k] = CST_vide;
                            int p = ydebut;
                            while (p > 1 & carte[p - 1][k] != CST_vide) {
                                carte[p][k] = carte[p - 1][k];
                                carte[p - 1][k] = CST_vide;
                                p -= 1;
                            }
                        }

                    }


                }
            }


        }
        Log.e("eleminer","milieu");
        xdebut=ydebut=yfin=0;
        for(int j =0 ; j < carteWidth ; j++){
            xdebut=j;
            ydebut=0;
            yfin=0;
            valeur=carte[j][0];
            for(int i = 0; i < carteHeight; i++){
                Log.i("helloVerticale","valeur="+valeur+"  carte=="+"["+i+"]"+"["+j+"]==="+carte[i][j]+ "  ydebu=="+ydebut+"  yfin=="+yfin);

                if(carte[i][j] != valeur){
                    Log.i("valeur != de carte ij","nbr carte=="+(yfin-ydebut));
                    if((yfin-ydebut)<2){
                        ydebut=i;
                        yfin=ydebut;
                        valeur=carte[i][j];

                    }else {
                        Log.i("hey je suis dans case", "cas==" + (yfin - ydebut));

                        if(carte[yfin][xdebut]!= CST_vide){
                            if ((yfin - ydebut>=2)) {

                                int mlk = ((yfin - ydebut) + 1);


                                for (int p=ydebut;p<(yfin+1);p++){
                                    carte[p][xdebut]=CST_vide;
                                }

                                int[] vect;
                                vect= new int[carteHeight];
                                for(int h=0;h<carteHeight;h++){
                                    vect[h]=carte[h][xdebut];
                                    Log.e("vertical",""+mlk+"vect["+h+"]=="+vect[h]);
                                }


                                for (int o=0;o<(mlk);o++){
                                    for (int d=(yfin) ;d>0;d--){
                                        carte[d][xdebut]=vect[d-1];
                                        vect[d]=vect[d-1];
                                        Log.e("vertica444l1",""+o+"xfin=="+yfin+"vect["+d+"]=="+vect[d]);
                                    }
                                }


                            }



                            if (i < 4) {
                                ydebut = i;
                                yfin = i;
                            } else {
                                yfin = 0;
                                ydebut = 0;
                            }

                        }else{
                            valeur=carte[i][j];
                            ydebut=i;
                            yfin=ydebut;
                        }
                    }
                }else{
                    yfin=i;
                }

            }
            if(carte[yfin][xdebut]!= CST_vide){
                if ((yfin - ydebut>=2)) {

                    int mlk = ((yfin - ydebut) + 1);


                    for (int p=ydebut;p<(yfin+1);p++){
                        carte[p][xdebut]=CST_vide;
                    }

                    int[] vect;
                    vect= new int[carteHeight];
                    for(int i=0;i<carteHeight;i++){
                        vect[i]=carte[i][xdebut];
                        Log.e("vertical",""+mlk+"vect["+i+"]=="+vect[i]);
                    }

                    for (int o=0;o<(mlk);o++){
                        for (int d=(yfin) ;d>0;d--){
                            carte[d][xdebut]=vect[d-1];
                            vect[d]=vect[d-1];

                            Log.e("vertica444l1",""+o+"xfin=="+yfin+"vect["+d+"]=="+vect[d]);
                        }
                    }


                }
            }
            sup=true;


        }
        Log.e("eleminer","fin");

    }

    public boolean onTouchEvent (MotionEvent event) {
        if (coup < 2) {
            Log.i("-> FCT <-", "onTouchEvent: x=" + event.getX());
            Log.i("-> FCT <-", "onTouchEvent: y=" + event.getY());

            int x = 0;
            int y = 0;
            boolean vide = true;
            int permuter = 0;
            int action = event.getAction();
            xposition= event.getX();
            yposition= event.getY();
            x = (int) (xposition / carteTileSize);
            y = (int) ((yposition- carteTopAnchor) / carteTileSize);
            Log.i("hello", "x=" + x + " ,y=" + y);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    xx = x;
                    yy = y;
                    Log.i("hello", "down" + xx + " ," + yy);


                    return true;

                case MotionEvent.ACTION_MOVE:
                    Log.i("hello", "move" + xx + " ," + yy);
                    return true;

                case MotionEvent.ACTION_UP:
                    Log.i("hello", "UP" + xx + " ," + yy);
                    if ((xx - x) < 0) {


                        Log.i("hello", "right");
                        if (yy >= 0) {
                            Log.i("x et y =", xx + " = =" + yy);
                            if (carte[yy][xx] == CST_vide) {

                            } else {
                                coup++;
                                if (xx < carteWidth) {

                                    permuter = carte[yy][xx];
                                    carte[yy][xx] = carte[yy][xx + 1];
                                    carte[yy][xx + 1] = permuter;
                                    if (carte[yy][xx] == CST_vide) {
                                        if (yy > 0) {
                                            if (carte[yy - 1][xx] != CST_vide) {
                                                int z = yy;
                                                while (z > 0) {
                                                    carte[z][xx] = carte[z - 1][xx];
                                                    carte[z - 1][xx] = CST_vide;
                                                    z -= 1;
                                                }

                                            }
                                        }
                                    }
                                    if (yy < carteHeight - 1) {
                                        if (carte[yy + 1][xx + 1] == CST_vide) {
                                            int x1 = xx + 1;
                                            int y1 = yy + 1;

                                            while (vide & y1 < carteHeight) {
                                                if (carte[y1][x1] == CST_vide) {
                                                    carte[y1][x1] = carte[y1 - 1][x1];
                                                    carte[y1 - 1][x1] = CST_vide;
                                                } else {
                                                    vide = false;
                                                }
                                                y1 += 1;
                                            }

                                        }
                                    }
                                }
                            }
                        }


                    } else {
                        if ((xx - x) > 0) {
                            Log.i("hello", "left");

                            if (yy >= 0) {
                                Log.i("x et y =", xx + " = =" + yy);
                                if (carte[yy][xx] == CST_vide) {

                                } else {
                                    coup++;
                                    if (xx > 0) {
                                        permuter = carte[yy][xx];
                                        carte[yy][xx] = carte[yy][xx - 1];
                                        carte[yy][xx - 1] = permuter;
                                        if (carte[yy][xx] == CST_vide) {
                                            if (yy > 0) {
                                                if (carte[yy - 1][xx] != CST_vide) {
                                                    int z = yy;
                                                    while (z > 0) {
                                                        carte[z][xx] = carte[z - 1][xx];
                                                        carte[z - 1][xx] = CST_vide;
                                                        z -= 1;
                                                    }

                                                }
                                            }
                                        }
                                        if (yy < carteHeight - 1) {
                                            if (carte[yy + 1][xx - 1] == CST_vide) {
                                                int x1 = xx - 1;
                                                int y1 = yy + 1;

                                                while (vide & y1 < carteHeight) {
                                                    if (carte[y1][x1] == CST_vide) {
                                                        carte[y1][x1] = carte[y1 - 1][x1];
                                                        carte[y1 - 1][x1] = CST_vide;
                                                    } else {
                                                        vide = false;
                                                    }
                                                    y1 += 1;
                                                }

                                            }
                                        }
                                    }
                                }
                            }

                        } else {
                            Log.i("hello", "meme");
                        }
                    }

                    return true;

            }
        }

        return super.onTouchEvent(event);

    }


    private void showAbout(){
        final boolean[] reponse = new boolean[1];
        AlertDialog.Builder about = new AlertDialog.Builder(mContext);
        about.setTitle("continuer");
        TextView l_viewabout = new TextView(mContext);
        l_viewabout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        l_viewabout.setPadding(20, 10, 20, 10);
        l_viewabout.setTextSize(20);

        l_viewabout.setText("Voulez vous continuer");


        l_viewabout.setMovementMethod(LinkMovementMethod.getInstance());
        about.setView(l_viewabout);
        about.setPositiveButton("oui", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                etape = true;
                execute1=false;
            }

        });
        about.setNegativeButton("NON", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                etape = false;
                activ.finish();

            }

        });
        about.show();

    }



    private void showAbout2(){
        final boolean[] reponse = new boolean[1];
        AlertDialog.Builder about = new AlertDialog.Builder(mContext);
        about.setTitle("Rejouer");
        TextView l_viewabout = new TextView(mContext);
        l_viewabout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        l_viewabout.setPadding(20, 10, 20, 10);
        l_viewabout.setTextSize(20);

        l_viewabout.setText("Voulez vous rejouer");


        l_viewabout.setMovementMethod(LinkMovementMethod.getInstance());
        about.setView(l_viewabout);
        about.setPositiveButton("oui", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                etape1 = true;

            }

        });
        about.setNegativeButton("NON", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                etape1 = false;
                activ.finish();

            }

        });
        about.show();

    }
}