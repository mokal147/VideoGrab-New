package com.mokal147.videograb;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
    EditText url, start, end;
    RadioGroup mode;
    TextView status;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(20,100,65));
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(1);
        root.setPadding(24,24,24,24);
        root.setBackgroundColor(Color.rgb(242,248,244));
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(root);
        setContentView(scroll);

        TextView title = new TextView(this);
        title.setText("VideoGrab");
        title.setTextSize(30);
        title.setTextColor(Color.rgb(20,100,65));
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        url = field(root,"رابط الفيديو أو الموسيقى","https://");
        TextView label = new TextView(this);
        label.setText("نوع التنزيل");
        label.setTextSize(17);
        root.addView(label);

        mode = new RadioGroup(this);
        mode.setOrientation(0);
        RadioButton video = new RadioButton(this);
        video.setText("فيديو");
        video.setId(1);
        video.setChecked(true);
        RadioButton audio = new RadioButton(this);
        audio.setText("موسيقى MP3");
        audio.setId(2);
        mode.addView(video);
        mode.addView(audio);
        root.addView(mode);

        start = field(root,"وقت البداية (مثال 00:01:20)","فارغ = من البداية");
        end = field(root,"وقت النهاية (مثال 00:02:30)","فارغ = حتى النهاية");

        Button go = new Button(this);
        go.setText("بدء التنزيل عبر Termux");
        root.addView(go);
        status = new TextView(this);
        status.setTextSize(16);
        status.setPadding(0,20,0,0);
        root.addView(status);

        go.setOnClickListener(v -> runDownload());
    }

    EditText field(LinearLayout root, String label, String hint) {
        TextView t = new TextView(this);
        t.setText(label);
        t.setTextSize(16);
        root.addView(t);
        EditText e = new EditText(this);
        e.setSingleLine(true);
        e.setHint(hint);
        e.setTextDirection(View.TEXT_DIRECTION_LTR);
        root.addView(e);
        return e;
    }

    void runDownload() {
        String link = url.getText().toString().trim();
        String a = start.getText().toString().trim();
        String b = end.getText().toString().trim();
        if (!(link.startsWith("https://") || link.startsWith("http://"))) {
            status.setText("أدخل رابطًا صحيحًا يبدأ بـ http أو https.");
            return;
        }
        if (a.isEmpty() != b.isEmpty()) {
            status.setText("أدخل وقت البداية والنهاية معًا، أو اتركهما فارغين.");
            return;
        }

        String script = System.getenv("HOME");
        String[] args = new String[] {
            link, mode.getCheckedRadioButtonId() == 2 ? "audio" : "video", a, b
        };
        try {
            Intent i = new Intent("com.termux.RUN_COMMAND");
            i.setClassName("com.termux", "com.termux.app.RunCommandService");
            i.putExtra("com.termux.RUN_COMMAND_PATH",
                "/data/data/com.termux/files/home/VideoGrabNew/download_media.sh");
            i.putExtra("com.termux.RUN_COMMAND_ARGUMENTS", args);
            i.putExtra("com.termux.RUN_COMMAND_WORKDIR",
                "/data/data/com.termux/files/home/VideoGrabNew");
            i.putExtra("com.termux.RUN_COMMAND_BACKGROUND", true);
            startService(i);
            status.setText("تم إرسال طلب التنزيل إلى Termux. تابع التنفيذ هناك.");
        } catch (Exception e) {
            status.setText("تعذر تشغيل Termux: " + e.getMessage()
                + "\nتأكد من تثبيت Termux والسماح للتطبيقات الخارجية.");
        }
    }
}
