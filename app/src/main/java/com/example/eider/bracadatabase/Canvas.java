package com.example.eider.bracadatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class Canvas extends AppCompatActivity {
    GridLayout lienzoImagenes;
    ImageView[][] imagenes= new ImageView[4][4];
    TextView tvCoordenadasX;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);
        
        lienzoImagenes = (GridLayout)findViewById(R.id.lienzoImagen);

        try {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    imagenes[i][j] = new ImageView(getApplicationContext());
                    imagenes[i][j].setPadding(1, 1, 1, 1);
                    imagenes[i][j].setImageResource(R.drawable.casilla);
                    // imagenes[i][j].setId(i);
                    //Lugar donde pongo la accion que me marca en un toast los id de los imageVie
                    lienzoImagenes.addView(imagenes[i][j]);
                }
            }
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }

        tvCoordenadasX=(TextView)findViewById(R.id.tvCoordenadasX);
        String acomodo="    \t 1 \t\t\t 2 \t\t\t 3 \t\t  4\t\t\t";
        tvCoordenadasX.setText(acomodo);
          
        int coordenada = getIntent().getExtras().getInt("coordenada");
        String RealCoord = Utils.IntToCoord(coordenada);
        String[] coor_array = RealCoord.split(",");
        String coor_arrayX = coor_array[0];
        String coor_arrayY = coor_array[1];
        Toast.makeText(this,"Las coordenadas son: "+coor_arrayX +" y "+ coor_arrayY,Toast.LENGTH_LONG).show();
        int x=Integer.parseInt(coor_arrayX);
        int y= Integer.parseInt(coor_arrayY);
        try
        {
            imagenes[x-1][y-1].setImageResource(R.drawable.circulo);
            
        }catch (Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
     
        
    }
}
