package com.example.gocar;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;

public class CustomListView extends ArrayAdapter<String> {
    private String[] Model_Name;
    private String[] Production_Year;
    private String[] Fuel_Level;
    private String[] image_path;
    private String[] Latitude;
    private String[] Longitude;
    private Activity context;
    Bitmap bitmap;


    public CustomListView(Activity context, String[] Model_Name,String[] Production_Year,String[] Latitude, String[] Longitude,String[] image_path,String[] Fuel_Level ) {
        super(context, R.layout.layout,Model_Name);
        this.context=context;
        this.Model_Name=Model_Name;
        this.Production_Year=Production_Year;
        this.Latitude=Latitude;
        this.Longitude=Longitude;
        this.image_path=image_path;
        this.Fuel_Level=Fuel_Level;



    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View r=convertView;
        ViewHolder viewHolder=null;
        if(r==null){
            LayoutInflater layoutInflater=context.getLayoutInflater();
            r=layoutInflater.inflate(R.layout.layout,null, true);
            viewHolder= new ViewHolder(r);
            r.setTag(viewHolder);

        } else {
            viewHolder=(ViewHolder) r.getTag();
        }
        viewHolder.txvwModel.setText(Model_Name[position]);
        viewHolder.txvwYear.setText(Production_Year[position]);
        viewHolder.txvwFuel.setText(Fuel_Level[position]);
        new GetImageFromURL(viewHolder.imvwImage).execute(image_path[position]);



        return r;
    }

    class ViewHolder{
        TextView txvwModel;
        TextView txvwYear;
        TextView txvwFuel;
        ImageView imvwImage;

        ViewHolder(View v){
            txvwModel=(TextView)v.findViewById(R.id.Model);
            txvwYear=(TextView)v.findViewById(R.id.Year);
            imvwImage=(ImageView)v.findViewById(R.id.imageview);
            txvwFuel=(TextView)v.findViewById(R.id.Fuel);
        }



    }

    public class GetImageFromURL extends AsyncTask<String,Void, Bitmap>
    {

        ImageView imageView;

        public GetImageFromURL(ImageView imgv){
            this.imageView=imgv;
        }
        @Override
        protected Bitmap doInBackground(String... url) {
            String urldisplay=url[0];
            bitmap=null;

            try{
                InputStream ist=new java.net.URL(urldisplay).openStream();
                bitmap= BitmapFactory.decodeStream(ist);


            }catch (Exception ex){
                ex.printStackTrace();

            }

            return bitmap;
        }
        protected void onPostExecute(Bitmap bitmap){
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }


    }

}
