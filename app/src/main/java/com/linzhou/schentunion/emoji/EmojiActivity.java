package com.linzhou.schentunion.emoji;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cooloongwu.emoji.entity.Emoji;
import com.cooloongwu.emoji.utils.EmojiTextUtils;
import com.linzhou.schentunion.R;
import com.linzhou.schentunion.utils.L;

/**
 * Emoji测试类
 */

public class EmojiActivity extends AppCompatActivity implements EmojiFragment.OnEmojiClickListener{

    private TextView tv;
    private EditText et;
    private Button bt;
    private LinearLayout ll;
    private CheckBox ck;

    private EmojiFragment ef=new EmojiFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji);
        tv= (TextView) findViewById(R.id.tv);
        et = (EditText) findViewById(R.id.et);
        bt = (Button) findViewById(R.id.bt);
        ll= (LinearLayout) findViewById(R.id.ll);
        ck= (CheckBox) findViewById(R.id.ck);

        ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.ll,ef);
                    fragmentTransaction.commit();
                    showMultiLayout();
                }else {
                    hideMultiLayout();
                }
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(EmojiTextUtils.getEditTextContent(et.getText().toString(),EmojiActivity.this,tv));
            }
        });

        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ck.setChecked(false);
                hideMultiLayout();
            }
        });
    }



    private void showMultiLayout() {
        //显示多功能布局，隐藏键盘
        ll.setVisibility(View.VISIBLE);
        KeyboardUtils.updateSoftInputMethod(this, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        KeyboardUtils.hideKeyboard(getCurrentFocus());
    }

    /**
     * 隐藏多功能布局
     */
    private void hideMultiLayout() {
        ll.setVisibility(View.GONE);
        KeyboardUtils.updateSoftInputMethod(this, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onEmojiDelete() {
        String text = et.getText().toString();
        if (text.isEmpty()) {
            return;
        }
        if ("]".equals(text.substring(text.length() - 1, text.length()))) {
            int index = text.lastIndexOf("[");
            if (index == -1) {
                int action = KeyEvent.ACTION_DOWN;
                int code = KeyEvent.KEYCODE_DEL;
                KeyEvent event = new KeyEvent(action, code);
                et.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                displayEditTextView();
                return;
            }
            et.getText().delete(index, text.length());
            displayEditTextView();
            return;
        }
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        et.onKeyDown(KeyEvent.KEYCODE_DEL, event);
    }
    private void displayEditTextView() {
        try {
            et.setText(EmojiTextUtils.getEditTextContent(et.getText().toString().trim(), EmojiActivity.this, et));
            //et.setSelection(et.getText().length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEmojiClick(Emoji emoji) {
        L.d("onEmojiClick");
        int selection = et.getSelectionStart();
        String str = et.getText().toString();
        String text = str.substring(0,selection)+emoji.getContent()
                +str.substring(selection,str.length());
        et.setText(EmojiTextUtils.getEditTextContent(text,this,et));
        et.setSelection(selection+emoji.getContent().length());
    }
}
