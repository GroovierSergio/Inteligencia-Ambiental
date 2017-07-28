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
        int coordenada1 = getIntent().getExtras().getInt("similar1");
        int coordenada2 = getIntent().getExtras().getInt("similar2");

        String RealCoord = Utils.IntToCoord(coordenada);
        String RealCoord1 = Utils.IntToCoord(coordenada1);
        String RealCoord2 = Utils.IntToCoord(coordenada2);

        String[] coor_array = RealCoord.split(",");
        String[] coor_array1 = RealCoord1.split(",");
        String[] coor_array2= RealCoord2.split(",");


        String coor_arrayX = coor_array[0];
        String coor_arrayX1 = coor_array1[0];
        String coor_arrayX2 = coor_array2[0];

        String coor_arrayY = coor_array[1];
        String coor_arrayY1 = coor_array1[1];
        String coor_arrayY2 = coor_array2[1];

        Toast.makeText(this,"Las coordenadas son: "+coor_arrayX +" y "+ coor_arrayY,Toast.LENGTH_LONG).show();
        int x=Integer.parseInt(coor_arrayX);
        int y= Integer.parseInt(coor_arrayY);

        int x1=Integer.parseInt(coor_arrayX1);
        int y1= Integer.parseInt(coor_arrayY1);

        int x2=Integer.parseInt(coor_arrayX2);
        int y2= Integer.parseInt(coor_arrayY2);

        try
        {
            if(x==4)
            {
                imagenes[x-4][y-1].setImageResource(R.drawable.circulo);
            }else if(x==3)
            {
                imagenes[x-2][y-1].setImageResource(R.drawable.circulo);
            }else if(x==2)
            {
                imagenes[x][y-1].setImageResource(R.drawable.circulo);
            }else if (x==1)
            {
                imagenes[x+2][y-1].setImageResource(R.drawable.circulo);
            }

            if(x1==4)
            {
                imagenes[x1-4][y1-1].setImageResource(R.drawable.circulovecino);
            }else if(x1==3)
            {
                imagenes[x1-2][y1-1].setImageResource(R.drawable.circulovecino);
            }else if(x1==2)
            {
                imagenes[x1][y1-1].setImageResource(R.drawable.circulovecino);

            }else if (x1==1)
            {
                imagenes[x1+2][y1-1].setImageResource(R.drawable.circulovecino);
            }

            if(x2==4)
            {
                imagenes[x2-4][y2-1].setImageResource(R.drawable.circulovecino);
            }else if(x2==3)
            {
                imagenes[x2-2][y2-1].setImageResource(R.drawable.circulovecino);
            }else if(x2==2)
            {
                imagenes[x2][y2-1].setImageResource(R.drawable.circulovecino);
            }else if (x2==1)
            {
                imagenes[x2+2][y2-1].setImageResource(R.drawable.circulovecino);
            }



        }catch (Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
     
        
    }
}
