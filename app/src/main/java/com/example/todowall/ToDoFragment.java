package com.example.todowall;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQuery;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ToDoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToDoFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton addBtn;

    static TextView noTask;

    MyDatabaseHelper myDB;
    static ArrayList<String> todo_id, todo_title, todo_status;
    static CustomAdapter customAdapter;

    static Context context;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ToDoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ToDoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToDoFragment newInstance(String param1, String param2) {
        ToDoFragment fragment = new ToDoFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do, container, false);

        context = getContext();

        recyclerView = view.findViewById(R.id.recyclerView);
        addBtn = view.findViewById(R.id.add_button);

        noTask = view.findViewById(R.id.txtNoTask);

        myDB = new MyDatabaseHelper(getContext());
        todo_id = new ArrayList<>();
        todo_title = new ArrayList<>();
        todo_status = new ArrayList<>();

        storeDataInArrays();

        customAdapter = new CustomAdapter(getContext(), todo_id, todo_title, todo_status);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        getContext(), R.style.BottomSheetDialogTheme
                );
                View bottomSheetView = LayoutInflater.from(getContext())
                        .inflate(R.layout.layout_add_todo,
                                (LinearLayout)view.findViewById(R.id.addBottomSheet));

                bottomSheetView.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText title = bottomSheetView.findViewById(R.id.txtTodo);

                        MyDatabaseHelper myDB = new MyDatabaseHelper(getContext());
                        myDB.addTodo(title.getText().toString());

                        storeDataInArrays();

                        customAdapter.notifyDataSetChanged();

                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    static void storeDataInArrays() {
        todo_id.clear();
        todo_title.clear();
        todo_status.clear();

        MyDatabaseHelper myDB = new MyDatabaseHelper(context);
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0) {
            Toast.makeText(context, "no data", Toast.LENGTH_SHORT).show();
            noTask.setVisibility(View.VISIBLE);
        } else {
            while (cursor.moveToNext()) {
                todo_id.add(cursor.getString(0));
                todo_title.add(cursor.getString(1));
                todo_status.add(cursor.getString(2));
            }
            noTask.setVisibility(View.GONE);
        }

        try {
            Bitmap bmp = drawTextToBitmap(context,myDB.getImage(),"tasks",todo_title,todo_status);

            final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context.getApplicationContext());


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if(bmp!=null)
                    wallpaperManager.setBitmap(bmp, null, true, WallpaperManager.FLAG_LOCK);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    static void updateStatus(int position, String status) {
        MyDatabaseHelper myDB = new MyDatabaseHelper(context);
        myDB.updateData(todo_id.get(position), todo_title.get(position), status);

        storeDataInArrays();

        customAdapter.notifyDataSetChanged();


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                customAdapter.notifyDataSetChanged();
            }
        }, 5000);

    }

    final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();

            new AlertDialog.Builder(getContext())
                    .setTitle("Delete item?")
                    .setMessage("Are you sure you want to delete to-do item?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyDatabaseHelper myDB = new MyDatabaseHelper(context);
                            myDB.deleteOneRow(todo_id.get(position));

                            storeDataInArrays();

                            customAdapter.notifyDataSetChanged();

                            Snackbar snackbar = Snackbar.make(recyclerView, "pos" +position, Snackbar.LENGTH_LONG);

                            snackbar.show();
                        }
                    }).setNegativeButton("No", null).show();

            }
    };


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

}