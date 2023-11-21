package com.example.registerloginexample;

import static java.sql.DriverManager.println;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ProfileActivity extends AppCompatActivity {
    private ImageView ivProfile;
    private TextView tv_id, tv_age;

    private String filePath;
    private Bitmap bitmap;

    ImageView imageView;
    TextView textView;
    JSONObject userInfo;
    private static final String ROOT_URL = "http://10.0.2.2:3000/uploaded-image/profile/image/upload";

    String[] permission_list = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

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

        ImageView ivImage;

        setContentView(R.layout.activity_profile);
        ivImage = findViewById(R.id.ivProfile);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);
        AtomicReference<String> imageUrl = new AtomicReference<>(null);


        downloadProfileImageRequest((jsonObject) -> {
            Log.i("final Log", jsonObject.getString("image_url"));
            imageUrl.set(jsonObject.getString("image_url"));

            if (imageUrl.get() != null && !imageUrl.get().isEmpty()) {
                // Load user's image from the provided URL
                Glide.with(this)
                        .load(imageUrl.get())
                        .apply(options)
                        .into(ivImage);

            } else {
                // If image_url is null or empty, load the default image URL
                String defaultImageUrl = "https://lupo-image.s3.ap-northeast-2.amazonaws.com/posts/1699865832675_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA2023-11-03%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE2.02.20.png";
                Glide.with(this)
                        .load(defaultImageUrl)
                        .apply(options)
                        .into(ivImage);
            }
        });
        //Log.i("final log", userInfo.toString());
//        try {
//            userInfo.get("image_url");
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
        // Check if user's image_url exists
        Log.i("get image url", imageUrl.get() == null ? "is null" : imageUrl.get());


        // Glide로 이미지 표시하기
        //String imageUrl = "https://lupo-image.s3.ap-northeast-2.amazonaws.com/posts/1699865832675_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA2023-11-03%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE2.02.20.png";
        //Glide.with(this).load(imageUrl).apply(options).into(ivImage);

        ivImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                navigateGallery();
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showPermissionContextPopup();
            } else {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            }
        });

        // auth/me image_url


    }
    public void checkPermission(){
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        //안드로이드6.0 (마시멜로) 이후 버전부터 유저 권한설정 필요
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for(String permission : permission_list){
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);

            if(chk == PackageManager.PERMISSION_DENIED){
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list,0);
            }
        }
    }


    interface Callback {
        void execute(JSONObject jsonArray) throws JSONException;
    };



        // 서버 URL 설정
        private static final String URL = "http://10.0.2.2:3000/auth/me";
        private Map<String, String> params;

            // POST는 사실 GET이었다 -> api 관리자 마음대로 설정할 수 있다.

        public void downloadProfileImageRequest(Callback callback){
            Log.i("step1", "");
            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    URL,
                    new Response.Listener<String>() { //응답을 잘 받았을 때 이 메소드가 자동으로 호출
                        @Override
                        public void onResponse(String response) {
                            Log.i("step2", "");
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);
                                userInfo = jsonObject;
                                Log.i("profile api response", jsonObject.toString() +"-----------\n" + jsonObject.getString("image_url"));
                                callback.execute(jsonObject);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    },
                    new Response.ErrorListener() { //에러 발생시 호출될 리스너 객체
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.v("error response", error);
                            Log.i("profile download error", error.toString());
                            Toast.makeText(getApplicationContext(), "내 정보 가져오기 실패", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    //sharedPreference - 공유된 파일에 accesstoken을 저장시킴 -> shared이므로 모든 activity에서 공유
                    SharedPreferences preferences = getSharedPreferences("myPref", MODE_PRIVATE);
                    // 로그인 후 파일 안에 들어가면 access token에 토큰 만료일과 아이디, 비밀번호가 들어있어서 어떤 user인지 확인 가능
                    String accessToken = preferences.getString("access_token", "dd");

                    Map headers = new HashMap();
                    //headers.put("Content-Type", "application/json");
                    // 로컬 스토리지에 저장되는 쿠키
                    headers.put("Cookie", "access_token=" + accessToken );
                    return headers;
                }
            };

            request.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
            AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
            AppHelper.requestQueue.add(request);
            println("요청 보냄.");
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
//                Toast.makeText(getApplicationContext(),"앱권한설정하세요",Toast.LENGTH_LONG).show();
//                finish();
        }

    }

    private void navigateGallery() {
            Log.i("step50", "");
        Intent data = new Intent(Intent.ACTION_PICK);
        data.setType("image/*");

        Log.i("data100", data.toString());

//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        photoPickerIntent.setType("image/*");
//        photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //mActPanelFragment.startActivityForResult(photoPickerIntent, ActivityConstantUtils.GALLERY_INTENT_REQUEST_CODE);
        startActivityForResult(data, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("step49", "");
        if (resultCode != Activity.RESULT_OK) {
            Log.i("error resultCode", Integer.toString(resultCode));
            return;
        }

        switch (requestCode) {
            case 2000:
                Log.i("step10", "");
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
        Log.i("uri", uri.toString());
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

//        if (cursor != null) {
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            String filePath = cursor.getString(column_index);
//            cursor.close();
//            Log.i("filePath", filePath);
//            return filePath;
//        }
        if (cursor != null) {
            try {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } finally {
                cursor.close();
            }
        }


        return null;
    }

    private void showPermissionContextPopup() {
        new AlertDialog.Builder(this)
                .setTitle("권한이 필요합니다.")
                .setMessage("프로필 이미지를 바꾸기 위해서는 갤러리 접근 권한이 필요합니다.")
                .setPositiveButton("동의하기", (dialog, which) -> {
                    ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES}, 1000);
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
}
