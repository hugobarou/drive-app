package com.drive.app.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.drive.app.R;
import com.drive.app.model.File;
import com.drive.app.model.Folder;
import com.drive.app.view.ExampleDialog;
import com.drive.app.view.FileDialog;
import com.drive.app.view.UploadDialog;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DriveActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener, UploadDialog.UploadDialogListener {

    private TextView pathTextView;
    private ListView listViewDrive;
    private Button returnButton;
    private Button createButton;
    private Button addFileButton;
    private Folder currentFolder;
    private RequestQueue queue;
    private String token;
    private String drive;
    private ArrayList<Object> combinedList;
    private FolderAdapter folderAdapter;
    private final String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        pathTextView = findViewById(R.id.activity_drive_path_txt);
        listViewDrive = findViewById(R.id.activity_drive_lst_view);
        returnButton = findViewById(R.id.button_return);
        createButton = findViewById(R.id.button_create);
        addFileButton = findViewById(R.id.button_add_file);
        token = getIntent().getStringExtra("token");
        drive = getIntent().getStringExtra("drive");
        queue = Volley.newRequestQueue(this);

        getFiles(drive + "/");
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentFolder.getFolderPath().equals(drive + "/")){
                    getFiles(currentFolder.getSavePath());
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFolderDialog();
                getFiles(currentFolder.getFolderPath());
            }
        });

        addFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUploadDialog();
                getFiles(currentFolder.getFolderPath());
            }
        });
    }

    @Override
    public void applyTexts(String folderName) {
        createFolder(folderName, currentFolder.getFolderPath());
    }

    @Override
    public void applyFile(String file) {
        uploadFile(file, currentFolder.getFolderPath());
    }

    public void openFolderDialog() {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "folder dialog");
    }

    public void openUploadDialog(){
        UploadDialog uploadDialog = new UploadDialog();
        uploadDialog.show(getSupportFragmentManager(), "upload dialog");
    }

    public void getFiles(String path) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("path", path);

        JsonObjectRequest req = new JsonObjectRequest(url + "api/folder", new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                currentFolder = Folder.fromJson(response);
                combinedList = new ArrayList<Object>();
                combinedList.addAll(currentFolder.getLstFolder());
                combinedList.addAll(currentFolder.getLstFile());

                pathTextView.setText(currentFolder.getFolderPath());

                folderAdapter = new FolderAdapter(getApplicationContext(), R.layout.single_item, combinedList);
                listViewDrive.setAdapter(folderAdapter);

                listViewDrive.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        long viewId = view.getId();
                        if (viewId == R.id.textView2) {
                            delete(combinedList.get(position));
                            folderAdapter.remove(folderAdapter.getItem(position));
                            folderAdapter.notifyDataSetChanged();
                            //getFiles(currentFolder.getFolderPath());
                        } else {
                            if(combinedList.get(position).getClass() == Folder.class){
                                Folder folder = (Folder) combinedList.get(position);
                                getFiles(folder.getFolderPath());
                            }
                            if(combinedList.get(position).getClass() == File.class){
                                File file = (File) combinedList.get(position);
                                Bundle args = new Bundle();
                                args.putString("filePath", file.getFilePath());
                                FileDialog fileDialog = new FileDialog();
                                fileDialog.setArguments(args);
                                fileDialog.show(getSupportFragmentManager(), "file dialog");
                            }
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        queue.add(req);
    }

    public void createFolder(String name, String path){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("path", path);

        JsonObjectRequest req = new JsonObjectRequest(url + "api/folder/create", new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                getFiles(currentFolder.getFolderPath());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        queue.add(req);
    }

    public void uploadFile(String file, String path){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("file", file);
        params.put("fileName", String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".jpg");
        params.put("path", path);

        JsonObjectRequest req = new JsonObjectRequest(url + "api/upload/encoded", new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                getFiles(currentFolder.getFolderPath());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        queue.add(req);
    }

    public void delete(Object object){
        String uri = url;
        String path = "";
        if(object.getClass() == Folder.class){
            Folder folder = (Folder) object;
            uri = uri + "api/folder/delete";
            path = folder.getFolderPath();

        }
        if(object.getClass() == File.class){
            File file = (File) object;
            uri = uri + "api/upload/delete";
            path = file.getFilePath();
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("path", path);

        JsonObjectRequest req = new JsonObjectRequest(uri, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        queue.add(req);
    }
}