package p8.demo.p8sokoban;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by roza on 27/12/2016
 */
public class Menu  extends Activity {

    public Button jouer;
    public Button apropos;
    public Button principe;
    public Button quitter;
    public RadioGroup son;
    public RadioButton on;
    private static final int DIALOG_ALERT = 0;
    private static final int APROPOS_ALERT = 1;
    public  AlertDialog.Builder dialogBuilder;
    private boolean stopmusic=true;

    MediaPlayer musique;


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ALERT:

                dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setMessage("Déplacez les cases à gauche ou à droite pour créer des liens de 3 cases identiques et les faire disparaitres");
                dialogBuilder.setCancelable(true);
                dialogBuilder.setNegativeButton("Revenir", new CancelOnClickListener());
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
                break;
            case APROPOS_ALERT :
                dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setMessage("Developpé par:\n AREZKI ROZA \n AOUA KAHINA");
                dialogBuilder.setCancelable(true);
                dialogBuilder.setNegativeButton("Revenir", new CancelOnClickListener());
                AlertDialog dialog1 = dialogBuilder.create();
                dialog1.show();
                break;
        }
        return super.onCreateDialog(id);
    }

    private final class CancelOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {

        }
    }

    private final class OkOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // initialise notre activity avec le constructeur parent
        super.onCreate(savedInstanceState);
        // charge le fichier main.xml comme vue de l'activit�
        setContentView(R.layout.menu);
        // recuperation de la vue une voie cree � partir de son id
        jouer = (Button) findViewById(R.id.jouer);
        apropos = (Button) findViewById(R.id.apropos);
        principe = (Button) findViewById(R.id.principe);
        son = (RadioGroup) findViewById(R.id.son);
        quitter= (Button) findViewById(R.id.quitter);
        on= (RadioButton)findViewById(R.id.on);


        final Intent puzzle=new Intent(this,p8_Sokoban.class);

        jouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopmusic=false;
                startActivity(puzzle);

            }
        });

        musique= MediaPlayer.create(getBaseContext(),R.raw.son);
        musique.start();

        son.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.on :
                        musique.start();
                        break;
                    case R.id.off :
                        musique.pause();

                        break;

                }
            }
        });

        principe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ALERT);
            }
        });

        apropos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(APROPOS_ALERT);
            }
        });

        quitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musique.pause();
                finish();
            }
        });
    }

    public void onResume(Bundle savedInstanceState){
        super.onResume();
    }
    public void onStop(){
        super.onStop();
        if (stopmusic) {
            musique.pause();
        }
    }
    public void onRestart(){
        super.onRestart();
        if (on.isChecked()){
            musique.start();
        }else{
            musique.pause();
        }
    }


}
