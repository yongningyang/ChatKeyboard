package cn.hadcn.keyboard_example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.hadcn.keyboard.ChatKeyboardLayout;
import cn.hadcn.keyboard.RecordingLayout;
import cn.hadcn.keyboard.media.MediaBean;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaBean
        .MediaListener, ChatKeyboardLayout.OnChatKeyBoardListener {
    ChatKeyboardLayout keyboardLayout = null;
    SimpleChatAdapter mAdapter;
    RecordingLayout rlRecordArea;
    String mVoicePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        keyboardLayout = (ChatKeyboardLayout) findViewById(R.id.kv_bar);
        keyboardLayout.showEmoticons();

        ArrayList<MediaBean> popupModels = new ArrayList<>();
        popupModels.add(new MediaBean(0, R.drawable.icon_camera, "拍照", this));
        popupModels.add(new MediaBean(1, R.drawable.icon_photo, "照片", this));
        popupModels.add(new MediaBean(2, R.drawable.icon_camera, "拍照", this));
        popupModels.add(new MediaBean(3, R.drawable.icon_photo, "照片", this));
        popupModels.add(new MediaBean(4, R.drawable.icon_camera, "拍照", this));
        popupModels.add(new MediaBean(5, R.drawable.icon_photo, "照片", this));
        popupModels.add(new MediaBean(6, R.drawable.icon_camera, "拍照", this));
        popupModels.add(new MediaBean(7, R.drawable.icon_photo, "照片", this));
        popupModels.add(new MediaBean(8, R.drawable.icon_camera, "拍照", this));
        popupModels.add(new MediaBean(9, R.drawable.pic_select_n, "照片", this));
        keyboardLayout.showMedias(popupModels);

        ListView listView = (ListView) findViewById(R.id.list_view);
        mAdapter = new SimpleChatAdapter(this);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                keyboardLayout.hideKeyboard();
                return false;
            }
        });
        listView.setAdapter(mAdapter);

        rlRecordArea = (RecordingLayout) findViewById(R.id.recording_area);

        keyboardLayout.setOnChatKeyBoardListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void hideShow(View view) {
        if (keyboardLayout.isLayoutVisible()) {
            keyboardLayout.hideLayout();
        } else {
            keyboardLayout.showLayout();
        }
    }

    public void popBack(View view) {
        if (keyboardLayout.isKeyboardPopped()) {
            keyboardLayout.hideKeyboard();
        } else {
            keyboardLayout.popKeyboard();
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onMediaClick(int id) {

    }

    @Override
    public void onSendButtonClicked(String msg) {
        mAdapter.addItem(new ChatBean(null, msg));
        keyboardLayout.clearInputContent();
    }

    @Override
    public void onInputTextChanged(final String text) {

    }

    @Override
    public void onRecordingAction(ChatKeyboardLayout.RecordingAction action) {
        switch (action) {
            case START:
                mVoicePath = AudioLib.getInstance().generatePath(this);
                AudioLib.getInstance().start(mVoicePath, new AudioListener());
                rlRecordArea.show(1);
                break;
            case RESTORE:
                rlRecordArea.show(1);
                break;
            case WILLCANCEL:
                rlRecordArea.show(0);
                break;
            case CANCELED:
                AudioLib.getInstance().cancel();
                rlRecordArea.hide();
                break;
            case COMPLETE:
                if (AudioLib.getInstance().complete() < 0) {
                    Toast.makeText(this, "time is too short", Toast.LENGTH_SHORT).show();
                }
                rlRecordArea.hide();
                break;
        }
    }

    private class AudioListener implements AudioLib.OnAudioListener {
        @Override
        public void onDbChange(double db) {
            int level = 0;
            Log.e("pengtao", "onDbChange db = " + db);
            if (db > 40) {
                level = ((int) db - 40) / 7;
            }
            Log.e("pengtao", "onDbChange level = " + level);
            rlRecordArea.setVoiceLevel(level);
        }
    }

    @Override
    public void onUserDefEmoticonClicked(String tag, String uri) {
        mAdapter.addItem(new ChatBean(tag, null));
    }

    @Override
    public void onKeyboardHeightChanged(final int height) {
        Log.e("pengtao", "height = " + height);
    }
}
