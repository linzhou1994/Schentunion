package com.linzhou.schentunion.activity;
/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.fragment
 *创建者:  linzhou
 *创建时间:17/04/27
 *描述:   修改个人信息页
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.AppConfig;
import com.linzhou.schentunion.base.BaseActivity;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.data.Student;
import com.linzhou.schentunion.emoji.KeyboardUtils;
import com.linzhou.schentunion.utils.L;
import com.linzhou.schentunion.utils.PicassoUtils;
import com.linzhou.schentunion.utils.UtilTools;
import com.linzhou.schentunion.view.CustomDialog;
import com.linzhou.schentunion.websocekt.MyWebSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Date;

import cn.qqtheme.framework.picker.DatePicker;
import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateStudentActivity extends BaseActivity implements View.OnClickListener{

    private CircleImageView profile_image;
    private RadioGroup mRadioGroup;
    private EditText et_username;
    private EditText et_sex;
    private EditText et_age;
    private EditText et_tel;
    private EditText et_email;
    private EditText et_college;
    private EditText et_profession;
    private EditText et_nummber;
    private EditText et_gdata;
    private EditText et_idcard;
    private EditText et_introduce;
    private TextView edit_user;
    private boolean update = false;//是否处于编辑模式

    private CustomDialog dialog;

    private ImageButton databt;

    private Bitmap bitmap;

    private Button btn_camera;
    private Button btn_picture;
    private Button btn_cancel;


    @Override
    protected int getlayout() {
        return R.layout.activity_updatestudent;
    }

    @Override
    protected void initview() {


        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        edit_user = (TextView) findViewById(R.id.edit_user);
        mRadioGroup = (RadioGroup) findViewById(R.id.mRadioGroup);

        databt = (ImageButton) findViewById(R.id.databt);

        et_username = (EditText) findViewById(R.id.et_username);
        et_sex = (EditText) findViewById(R.id.et_sex);
        et_age = (EditText) findViewById(R.id.et_age);
        et_tel = (EditText) findViewById(R.id.et_tel);
        et_email = (EditText) findViewById(R.id.et_email);
        et_college = (EditText) findViewById(R.id.et_college);
        et_profession = (EditText) findViewById(R.id.et_profession);
        et_nummber = (EditText) findViewById(R.id.et_nummber);
        et_gdata = (EditText) findViewById(R.id.et_gdata);
        et_idcard = (EditText) findViewById(R.id.et_idcard);
        et_introduce = (EditText) findViewById(R.id.et_introduce);

        //初始化dialog
        dialog = new CustomDialog(this, 0, 0,
                R.layout.dialog_photo, R.style.pop_anim_style, Gravity.BOTTOM, 0);
        //提示框以外点击无效
        dialog.setCancelable(false);

        btn_camera = (Button) dialog.findViewById(R.id.btn_camera);
        btn_picture = (Button) dialog.findViewById(R.id.btn_picture);
        btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);

        setEnabled(false);
        //让键盘消失
//        KeyboardUtils.updateSoftInputMethod(this, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
//       KeyboardUtils.hideKeyboard(getCurrentFocus());

        PicassoUtils.loadImageViewSize(UpdateStudentActivity.this, StaticClass.HTTPIMAGE+AppConfig.student.prcture, 96, 96, profile_image);
    }

    @Override
    protected void initData() {
        et_username.setText(AppConfig.student.username);
        et_sex.setText(AppConfig.student.sex == 0 ? "男" : "女");
        et_age.setText(AppConfig.student.age + "");
        et_tel.setText(AppConfig.student.tel);
        et_email.setText(AppConfig.student.email);
        et_college.setText(AppConfig.student.college);
        et_profession.setText(AppConfig.student.profession);
        et_nummber.setText(AppConfig.student.nummber);
        et_gdata.setText(UtilTools.dateToString(AppConfig.student.gdata, "yyyy-MM"));
        et_idcard.setText(AppConfig.student.idcard);
        et_introduce.setText(AppConfig.student.introduce);

    }

    @Override
    protected void setListener() {
        super.setListener();
        profile_image.setOnClickListener(this);
        edit_user.setOnClickListener(this);
        databt.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        btn_picture.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }


    //控制焦点
    private void setEnabled(boolean is) {
        et_username.setEnabled(is);
        et_sex.setEnabled(false);
        et_sex.setVisibility(is ? View.GONE : View.VISIBLE);
        mRadioGroup.setVisibility(is ? View.VISIBLE : View.GONE);

        et_tel.setEnabled(is);
        et_email.setEnabled(is);
        et_idcard.setEnabled(false);
        et_introduce.setEnabled(is);
        et_age.setEnabled(is);
        et_college.setEnabled(false);
        et_profession.setEnabled(false);
        et_nummber.setEnabled(is);
        et_gdata.setEnabled(false);
//        if (is) databt.setVisibility(View.VISIBLE);
//        else databt.setVisibility(View.GONE);
        databt.setVisibility(is ? View.VISIBLE : View.GONE);

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.edit_user:
                if (!update) {
                    update = !update;
                    setEnabled(true);
                    edit_user.setText("确认修改");
                } else {
                    update = !update;
                    setEnabled(false);
                    edit_user.setText("编辑资料");
                    updatestudent();
                }
                break;
            case R.id.profile_image:
                if (update)
                    dialog.show();
                break;
            case R.id.btn_cancel:
                dialog.dismiss();
                break;
            case R.id.btn_camera:
                toCamera();
                break;
            case R.id.btn_picture:
                toPicture();
                break;
            case R.id.databt:
                showtime();
                break;
        }

    }

    private Date time = null;

    private void showtime() {

        DatePicker picker = new DatePicker(this, DatePicker.YEAR_MONTH);

        picker.setGravity(Gravity.CENTER);
        picker.setWidth((int) (picker.getScreenWidthPixels() * 0.8));
        picker.setRangeStart(2016, 10, 14);
        picker.setRangeEnd(2050, 11, 11);
        picker.setSelectedItem(2017, 9);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthPickListener() {
            @Override
            public void onDatePicked(String year, String month) {
                //showToast(year + "-" + month);
                time = UtilTools.stringToDate(year, month);
                et_gdata.setText(UtilTools.dateToString(time, "yyyy-MM"));
                L.d(Integer.parseInt(year) + "-" + Integer.parseInt(month) + "     " + UtilTools.dateToString(time));
            }
        });
        picker.show();

    }

    private int sex;
    private Student student;

    private void updatestudent() {
        student = new Student();
        if (!TextUtils.isEmpty(et_username.getText().toString()) &&
                !TextUtils.isEmpty(et_tel.getText().toString()) &&
                !TextUtils.isEmpty(et_email.getText().toString()) &&
                !TextUtils.isEmpty(et_introduce.getText().toString()) &&
                !TextUtils.isEmpty(et_age.getText().toString())
                ) {
            student.username = et_username.getText().toString();
            student.sex = AppConfig.student.sex;
            mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    if (checkedId == R.id.rb_boy) {
                        student.sex = 0;
                    } else if (checkedId == R.id.rb_girl) {
                        student.sex = 1;
                    }
                }
            });
            student.tel = et_tel.getText().toString();
            student.email = et_email.getText().toString();
            student.introduce = et_introduce.getText().toString();
            student.age = Integer.parseInt(et_age.getText().toString());
            student.gdata = (time == null ? AppConfig.student.gdata : time);
            L.d("gdata: "+ student.gdata.getTime());
            JSONObject jo = new JSONObject();
            try {
                jo.put(StaticClass.TYPE, StaticClass.UPDATESTUDENT);
                JSONObject content = new JSONObject();
                content.put("age", student.age);
                content.put("email", student.email);
                content.put("gdate", student.gdata.getTime());
                content.put("username", student.username);
                content.put("introduce", student.introduce);
                content.put("sex", student.sex);
                content.put("tel", student.tel);
                jo.put(StaticClass.CONTENT, content);
                MyWebSocket.webSocket.sendMessage(jo.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if (bitmap!=null)
            UtilTools.uploadBitmap(StaticClass.UPLOADIMAGE,
                    AppConfig.student.id, bitmap, new UtilTools.UploadListener() {
                @Override
                public void uploadSuccess() {
                    JSONObject jo = new JSONObject();
                    try {
                        jo.put(StaticClass.TYPE,StaticClass.DETAIL);
                        jo.put(StaticClass.CONTENT,new JSONObject());
                        MyWebSocket.webSocket.sendMessage(jo.toString());
                        L.d("uploadSuccess");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void uploadFailure(String e) {
                    L.d(e);
                }
            });
        else L.d("bitmap == null");



    }

    public static final String PHOTO_IMAGE_FILE_NAME = "fileImg.jpg";
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int IMAGE_REQUEST_CODE = 101;
    public static final int RESULT_REQUEST_CODE = 102;
    private File tempFile = null;

    //跳转相机
    private void toCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断内存卡是否可用，可用的话就进行储存
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Environment.getExternalStorageDirectory(),PHOTO_IMAGE_FILE_NAME)));
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
        dialog.dismiss();
    }

    //跳转相册
    private void toPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
        dialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != this.RESULT_CANCELED) {
            switch (requestCode) {
                //相册数据
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                //相机数据
                case CAMERA_REQUEST_CODE:
                    tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_IMAGE_FILE_NAME);
                    startPhotoZoom(Uri.fromFile(tempFile));
                    break;
                case RESULT_REQUEST_CODE:
                    //有可能点击舍弃
                    if (data != null) {
                        //拿到图片设置
                        setImageToView(data);
                        //既然已经设置了图片，我们原先的就应该删除
                        if (tempFile != null) {
                            tempFile.delete();
                        }
                    }
                    break;
            }
        }
    }

    //裁剪
    private void startPhotoZoom(Uri uri) {
        if (uri == null) {
            L.e("uri == null");
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //设置裁剪
        intent.putExtra("crop", "true");
        //裁剪宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪图片的质量
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        //发送数据
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    //设置图片
    private void setImageToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            bitmap = bundle.getParcelable("data");
            profile_image.setImageBitmap(bitmap);
        }
    }


    @Override
    public void remessage(String mes) {
        super.remessage(mes);

        try {
            JSONObject jo = new JSONObject(mes);
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.UPDATESTUDENT)) {
                JSONObject content = jo.optJSONObject(StaticClass.CONTENT);
                int result = content.optInt("result");
                if (result == 1) {

                    AppConfig.student.age = student.age;
                    AppConfig.student.email = student.email;
                    AppConfig.student.gdata = student.gdata;
                    AppConfig.student.username = student.username;
                    AppConfig.student.introduce = student.introduce;
                    AppConfig.student.sex = student.sex;
                    AppConfig.student.tel = student.tel;
                    jo.put(StaticClass.TYPE,StaticClass.DETAIL);
                    jo.put(StaticClass.CONTENT,new JSONObject());
                    MyWebSocket.webSocket.sendMessage(jo.toString());
                    initData();
                } else Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
            }
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.LOGOUT)){
                finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
