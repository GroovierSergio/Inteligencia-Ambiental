package com.example.eider.bracadatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

GridLayout lienzoImagenes;
ImageView[][] imagenes= new ImageView[4][4];
TextView tvCoordenadasX;

public class Canvas extends AppCompatActivity {

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
                    //Lugar donde pongo la accion que me marca en un toast los id de los imageView
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


        String coordenada = getIntent().getExtras().getString("coordenada");
        String[] coor_array = coordenada.replace("[","%").split("%");
        String[] coor_array2 = coor_array[1].split(",");
        String[] coor_array3 = coor_array2[1].split("]");
        Toast.makeText(this,"Las coordenadas son: "+coor_array2[0] +" y "+ coor_array3[0],Toast.LENGTH_LONG).show();
        int x=Integer.parseInt(coor_array2[0]);
        int y= Integer.parseInt(coor_array3[0]);
        try
        {
            imagenes[x-1][y-1].setImageResource(R.drawable.circulo);
        }catch (Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
     
        
    }
}
