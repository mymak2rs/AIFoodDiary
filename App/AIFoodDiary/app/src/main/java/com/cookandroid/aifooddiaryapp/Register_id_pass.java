package com.cookandroid.aifooddiaryapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

// intent_s 는 인텐트 보낼 떄 쓰는 변수, intent_r은 인텐트 받을 때 쓸 변수!!!!!!!!!!!!.

public class Register_id_pass extends AppCompatActivity {
    // 버튼 생성
    Button btn_next, btn_validate;
    EditText et_id, et_pass;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_id_pass);

        //위젯 연결
        btn_next=(Button)findViewById(R.id.btn_next);
        et_id = (EditText) findViewById(R.id.et_id);
        et_pass = (EditText) findViewById(R.id.et_pass);
        btn_validate = (Button) findViewById(R.id.btn_validate);

        // 아이디 중복체크 버튼 이벤트 처리
        btn_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // editText에 입력된 아이디 변수에 저장
                String userID = et_id.getText().toString();

                if(userID.equals("")) { // 입력된 값이 공백일시
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register_id_pass.this);
                    dialog = builder.setMessage("아이디는 빈 칸일 수 없습니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success=jsonResponse.getBoolean("success");
                            if(success){
                                AlertDialog.Builder builder=new AlertDialog.Builder( Register_id_pass.this );
                                dialog=builder.setMessage("사용할 수 있는 아이디입니다.")
                                        .setPositiveButton("확인",null)
                                        .create();
                                dialog.show();
                                btn_next.setVisibility(View.VISIBLE);
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder( Register_id_pass.this );
                                dialog=builder.setMessage("사용할 수 없는 아이디입니다.")
                                        .setNegativeButton("확인",null)
                                        .create();
                                dialog.show();
                                btn_next.setVisibility(View.INVISIBLE);
                            }
                        }
                        catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                ValidateRequest validateRequest = new ValidateRequest(userID,responseListener);
                RequestQueue queue = Volley.newRequestQueue(Register_id_pass.this);
                queue.add(validateRequest);

            }
        });

        //다음 버튼 이벤트 처리
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력받은 userID와 userPass에 대해 변수 선언
                String userID = et_id.getText().toString();
                String userPass = et_pass.getText().toString();

                // 입력받은 userID와 userPass를 다음 화면 인자로 넣어주며 전환
                Intent intent_s = new Intent(Register_id_pass.this, Register_name.class);
                intent_s.putExtra("userID", userID);
                intent_s.putExtra("userPass", userPass);
                startActivity(intent_s);
            }
        });
    }
}