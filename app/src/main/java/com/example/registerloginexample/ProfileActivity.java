package com.example.registerloginexample;

import static com.sendbird.uikit.consts.StringSet.uri;
import static java.sql.DriverManager.println;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private ImageView ivProfile;
    private TextView tv_id, tv_age;

    private String filePath;
    private Bitmap bitmap;

    ImageView imageView;
    TextView textView;
    private static final String ROOT_URL = "http://10.0.2.2:3000/uploaded-image/profile/image/upload";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tv_id = findViewById(R.id.tv_id);
        tv_age = findViewById(R.id.tv_age);
        ivProfile = findViewById(R.id.ivProfile);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        String userAge = intent.getStringExtra("userAge");

        tv_id.setText(userID);
        tv_age.setText(userAge);

        ivProfile.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                navigateGallery();
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showPermissionContextPopup();
            } else {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1000:
                // grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                navigateGallery();
                break;
            default:
                // 처리하지 않는 경우
        }

    }

    private void navigateGallery() {
        Intent data = new Intent(Intent.ACTION_PICK);
        data.setType("image/*");

//        Uri picUri = data.getData();
//        filePath = getPath(picUri);
//        Log.i("image path", picUri.toString());
//        if (filePath != null) {
//            try {
//
//                //textView.setText("File Selected");
//                Log.d("filePath", String.valueOf(filePath));
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
//                //uploadBitmap(bitmap);
//                //imageView.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

//        uploadProfileImageRequest(intent.getData());
        startActivityForResult(data, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case 2000:
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    Log.i("ImageUri", selectedImageUri.toString());
                    // 이미지 경로를 얻는 부분 추가
                    String imagePath = getPath(selectedImageUri);
                    if (imagePath != null) {
                        Log.i("Image Path", imagePath);
                        // 여기에서 imagePath를 사용하거나 필요한 곳에 전달할 수 있습니다.
                        try {

                            //textView.setText("File Selected");
                            Log.d("filePath", String.valueOf(filePath));
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            uploadBitmap(bitmap);
                            //imageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "사진의 경로를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    }

                    // 이미지를 ImageView에 표시
                    ivProfile.setImageURI(selectedImageUri);
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // Uri에서 실제 파일 경로를 얻는 메서드
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            Log.i("filePath", filePath);
            return filePath;
        }

        return null;
    }

    private void showPermissionContextPopup() {
        new AlertDialog.Builder(this)
                .setTitle("권한이 필요합니다.")
                .setMessage("프로필 이미지를 바꾸기 위해서는 갤러리 접근 권한이 필요합니다.")
                .setPositiveButton("동의하기", (dialog, which) -> {
                    ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
                })
                .setNegativeButton("취소하기", (dialog, which) -> {
                    // 취소 동작 추가
                })
                .create()
                .show();
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    private void uploadBitmap(final Bitmap bitmap) {

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ROOT_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Log.i("img jsobject", obj.toString());
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError",""+error.getMessage());
                    }
                }) {


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                SharedPreferences preferences = getSharedPreferences("myPref", MODE_PRIVATE);
                String accessToken = preferences.getString("access_token", "dd");

                Map headers = new HashMap();
                //headers.put("Content-Type", "application/json");
                // 로컬 스토리지에 저장되는 쿠키
                headers.put("Cookie", "access_token=" + accessToken );
                return headers;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

//    public void uploadProfileImageRequest(String image) {
//        String url = "http://10.0.2.2:3000/uploaded-image/profile/image/upload";
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                url,
//                new Response.Listener<String>() { //응답을 잘 받았을 때 이 메소드가 자동으로 호출
//                    @Override
//                    public void onResponse(String response) {
//                        JSONObject jsonObject = null;
//                        try {
//                            jsonObject = new JSONObject(response);
//                            Log.i("upload image complete.",jsonObject.toString());
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                },
//                new Response.ErrorListener() { //에러 발생시 호출될 리스너 객체
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        VolleyLog.v("error response", error);
//                        Toast.makeText(getApplicationContext(), "프로필 이미지 업로드에 실패하였습니다", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
//        ) {
//
//            public void getByteData(): java.util.ArrayList<Pair<String, DataPart>> {
//                //여기서 이미지파일을 넣은 컬렉션을 리턴하면 된다.
//                Object ArrayList;
//                Object params = ArrayList< Pair <String, DataPart>>()
//                params.add(Pair(/*이미지를 구분할 태그*/,
//                        DataPart(/*이미지파일 이름*/,/*이미지를 바이트어레이로 변환한 것*/ )))
//
//                return params
//            }
//
//            @Override
//            public String getBodyContentType() {
//                return mMimeType;
//            }
//
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                return mMultipartBody;
//            }
//
//            @Override
//            public Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String,String>();
//                // body값 http 요청 본문에 포함
//                params.put("lat", latitude);
//
//                return params;
//            }
//
//
//        };
//        request.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
//        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
//        AppHelper.requestQueue.add(request);
//        println("요청 보냄.");
}
