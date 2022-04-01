package com.example.todowall;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WallpaperFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WallpaperFragment extends Fragment {

    Button btnChange;
    ImageView img;
    MyDatabaseHelper db;

    static Context context;

    static ArrayList<String> todo_id, todo_title, todo_status;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WallpaperFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WallpaperFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WallpaperFragment newInstance(String param1, String param2) {
        WallpaperFragment fragment = new WallpaperFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallpaper, container, false);

        btnChange = view.findViewById(R.id.btnSelect);
        img = view.findViewById(R.id.imgWall);

        context = getContext();

        todo_id = new ArrayList<>();
        todo_title = new ArrayList<>();
        todo_status = new ArrayList<>();

        String uri = null;
        byte[] imgByte = new byte[0];
        Bitmap b = null;

        MyDatabaseHelper myDB = new MyDatabaseHelper(getContext());
//        Cursor cursor = myDB.getWall();
//
//        while (cursor.moveToNext()) {
//            uri = cursor.getString(0);
//            imgByte = cursor.getBlob(1);
//        }

//        if(uri!=null) {
//            img.setImageURI(Uri.parse(uri));
//            Toast.makeText(getContext(), "" + uri, Toast.LENGTH_SHORT).show();

            img.setImageBitmap(myDB.getImage());

//            Bitmap bmp = drawTextToBitmap(getContext(),myDB.getImage(),"Hello Android");
//
//            final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext().getApplicationContext());
//
//            try {
//                wallpaperManager.setBitmap(bmp, null, true, WallpaperManager.FLAG_LOCK);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


//        }


        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);

                // pass the constant to compare it
                // with the returned requestCode
                startActivityForResult(Intent.createChooser(i, "Select Picture"), 200);
            }
        });

        return view;
    }

    public static Bitmap drawTextToBitmap(Context mContext, Bitmap resourceId, String mText, ArrayList<String> todo_title, ArrayList<String> todo_status) {
        try {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap bitmap = resourceId;
            android.graphics.Bitmap.Config bitmapConfig =   bitmap.getConfig();
            // set default bitmap config if none
            if(bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.rgb(255, 255, 255));
            // text size in pixels
            paint.setTextSize((int) (bitmap.getWidth()/20));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width())/20;
            int y = (bitmap.getHeight() - (bitmap.getHeight() + bounds.height())/10)-(todo_title.size()*30);

//            canvas.drawText(mText, x, y, paint);
//            canvas.drawText(mText, x, y+30, paint);

            canvas.drawText("To-Do's:", x, y-70, paint);

            for(int i=0; i<todo_title.size(); i++) {
                if(todo_status.get(i).equals("unchecked")) {
                    paint.setStrikeThruText(false);
                    canvas.drawText("☐ ", x, y, paint);
                    canvas.drawText(todo_title.get(i), x+70, y, paint);
                } else {
                    paint.setStrikeThruText(false);
                    canvas.drawText("☑ ", x, y, paint);
                    paint.setStrikeThruText(true);
                    canvas.drawText(todo_title.get(i), x+70, y, paint);
                }

                y += 70;
            }

            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception

            return null;
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == 200) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
//                    img.setImageURI(selectedImageUri);

//                    Bitmap bmp = drawTextToBitmap(getContext(),db.getImage(),"Hello");
//                    img.setImageBitmap(bmp);

                    Resources resources = getContext().getResources();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    db = new MyDatabaseHelper(getContext());
                    byte[] image = getBitmapAsByteArray(bitmap);

                    while (image.length > 1000000){
                        Bitmap bit = BitmapFactory.decodeByteArray(image, 0, image.length);
                        Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int)(bit.getWidth()*0.6), (int)(bit.getHeight()*0.6), true);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        image = stream.toByteArray();
                    }

                    db.changeWall(image);

                    img.setImageBitmap(db.getImage());
                    storeDataInArrays();

//                    final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext().getApplicationContext());
//
//                    try {
//                        wallpaperManager.setBitmap(bmp, null, true, WallpaperManager.FLAG_LOCK);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 40, outputStream);

        return outputStream.toByteArray();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    static void storeDataInArrays() {
        String tasks = "";

        todo_id.clear();
        todo_title.clear();
        todo_status.clear();

        MyDatabaseHelper myDB = new MyDatabaseHelper(context);
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0) {
            Toast.makeText(context, "no data", Toast.LENGTH_SHORT).show();

        } else {
            while (cursor.moveToNext()) {
                todo_id.add(cursor.getString(0));
                todo_title.add(cursor.getString(1));
                todo_status.add(cursor.getString(2));

                if(cursor.getString(2).equals("unchecked")) {
                    tasks += cursor.getString(1)+"\n\n";
                }
            }

        }

        try {
            Bitmap bmp = drawTextToBitmap(context,myDB.getImage(),tasks,todo_title,todo_status);

            final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context.getApplicationContext());


            wallpaperManager.setBitmap(bmp, null, true, WallpaperManager.FLAG_LOCK);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}