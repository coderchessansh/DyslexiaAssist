package com.coderchessansh.dyslexiaassist;

import android.app.*;
import android.os.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;
import android.view.*;
import android.widget.*;
import android.content.*;
import java.util.*;

public class MainActivity extends Activity {
  LinearLayout root, content;
  TextToSpeech tts;
  float scale=1f, speechRate=0.9f;
  boolean dark=false, highContrast=false;
  int purple=Color.rgb(108,99,255), mint=Color.rgb(53,211,153);
  int bg(){return dark?Color.rgb(20,20,28):Color.rgb(247,247,251);}
  int fg(){return dark?Color.WHITE:Color.rgb(35,35,50);}
  int sub(){return dark?Color.rgb(205,205,220):Color.DKGRAY;}
  int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
  TextView tv(String s,float size){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(fg());v.setPadding(dp(8),dp(6),dp(8),dp(6));return v;}
  Button btn(String s){Button b=new Button(this);b.setText(s);b.setTextSize(15);b.setAllCaps(false);b.setTextColor(dark?Color.WHITE:Color.rgb(35,35,50));return b;}
  void title(TextView v){v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);}
  @Override public void onCreate(Bundle b){super.onCreate(b);loadPrefs();tts=new TextToSpeech(this,st->{if(st==TextToSpeech.SUCCESS)tts.setLanguage(Locale.US);});showHome();}
  void loadPrefs(){android.content.SharedPreferences p=getSharedPreferences("prefs",0);scale=p.getFloat("scale",1f);dark=p.getBoolean("dark",false);highContrast=p.getBoolean("contrast",false);}
  void savePrefs(){getSharedPreferences("prefs",0).edit().putFloat("scale",scale).putBoolean("dark",dark).putBoolean("contrast",highContrast).apply();}
  void base(){
    root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(bg());
    LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.CENTER_VERTICAL);bar.setPadding(dp(14),dp(8),dp(14),dp(6));
    ImageView logo=new ImageView(this);logo.setImageResource(R.drawable.ic_logo);bar.addView(logo,new LinearLayout.LayoutParams(dp(44),dp(44)));
    TextView title=tv("DyslexiaAssist",21);title(title);bar.addView(title,new LinearLayout.LayoutParams(0,dp(48),1));
    Button home=btn("⌂");home.setOnClickListener(v->showHome());bar.addView(home,new LinearLayout.LayoutParams(dp(52),dp(48)));
    root.addView(bar);content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);content.setPadding(dp(18),dp(4),dp(18),dp(18));
    ScrollView scroll=new ScrollView(this);scroll.addView(content);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);
  }
  TextView heading(String s){TextView h=tv(s,27);title(h);return h;}
  void cardButton(String text,String desc,View.OnClickListener l){
    Button b=btn(text+"\n"+desc);b.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);b.setPadding(dp(16),dp(12),dp(12),dp(12));content.addView(b,new LinearLayout.LayoutParams(-1,dp(82)));b.setOnClickListener(l);
  }
  void showHome(){
    base();
    content.addView(heading("Read with less stress."));
    TextView intro=tv("Friendly tools for reading, listening and practicing.",16);intro.setTextColor(sub());content.addView(intro);
    Space sp=new Space(this);content.addView(sp,new LinearLayout.LayoutParams(1,dp(8)));
    cardButton("Easy Reader","Paste text, change the size and listen.",v->reader());
    cardButton("Word Practice","Practice useful words with pronunciation.",v->practice());
    cardButton("Progress","See your practice stats and achievements.",v->progress());
    cardButton("Reading Settings","Text size, contrast, dark mode and speech speed.",v->settings());
    TextView tip=tv("Tip: Try shorter sentences first, then build up.",15);tip.setTextColor(sub());tip.setPadding(dp(8),dp(18),dp(8),dp(8));content.addView(tip);
    TextView note=tv("Learning support tool — not a diagnostic or medical device.",13);note.setTextColor(sub());content.addView(note);
  }
  void reader(){
    base();content.addView(heading("Easy Reader"));
    TextView hint=tv("Type or paste a passage below. You can listen to it at a comfortable speed.",15);hint.setTextColor(sub());content.addView(hint);
    EditText input=new EditText(this);input.setHint("Start typing or paste text here…");input.setGravity(Gravity.TOP);input.setMinHeight(dp(190));input.setTextSize(20*scale);input.setPadding(dp(14),dp(14),dp(14),dp(14));input.setTextColor(fg());input.setHintTextColor(sub());content.addView(input,new LinearLayout.LayoutParams(-1,dp(220)));
    LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);
    Button speak=btn("▶ Read");Button stop=btn("■ Stop");Button minus=btn("A−");Button plus=btn("A+");
    row.addView(speak,new LinearLayout.LayoutParams(0,dp(52),2));row.addView(stop,new LinearLayout.LayoutParams(0,dp(52),2));row.addView(minus,new LinearLayout.LayoutParams(0,dp(52),1));row.addView(plus,new LinearLayout.LayoutParams(0,dp(52),1));content.addView(row);
    Button clear=btn("Clear text");content.addView(clear);TextView preview=tv("Preview\n\n"+(input.getText().length()==0?"Your reading preview will appear here.":input.getText().toString()),20*scale);preview.setTextColor(highContrast?Color.BLACK:fg());preview.setBackgroundColor(highContrast?Color.WHITE:(dark?Color.rgb(35,35,45):Color.WHITE));preview.setPadding(dp(18),dp(18),dp(18),dp(18));content.addView(preview,new LinearLayout.LayoutParams(-1,dp(230)));
    input.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int b,int c){preview.setText("Preview\n\n"+(s.length()==0?"Your reading preview will appear here.":s.toString()));}public void afterTextChanged(android.text.Editable e){}});
    speak.setOnClickListener(v->{if(tts!=null){tts.setSpeechRate(speechRate);tts.speak(input.getText().toString(),TextToSpeech.QUEUE_FLUSH,null,"reader");}});
    stop.setOnClickListener(v->{if(tts!=null)tts.stop();});
    minus.setOnClickListener(v->{scale=Math.max(.8f,scale-.1f);input.setTextSize(20*scale);preview.setTextSize(20*scale);savePrefs();});
    plus.setOnClickListener(v->{scale=Math.min(1.6f,scale+.1f);input.setTextSize(20*scale);preview.setTextSize(20*scale);savePrefs();});
    clear.setOnClickListener(v->input.setText(""));
  }
  void practice(){
    base();content.addView(heading("Word Practice"));
    TextView info=tv("Tap a word to hear it. Try saying it yourself first.",15);info.setTextColor(sub());content.addView(info);
    String[] words={"apple","window","school","friend","planet","because","beautiful","together","remember","different"};
    for(String w:words){Button x=btn("🔊  "+w);content.addView(x,new LinearLayout.LayoutParams(-1,dp(52)));x.setOnClickListener(v->{tts.setSpeechRate(speechRate);tts.speak(w,TextToSpeech.QUEUE_FLUSH,null,"word");addPractice();});}
  }
  void addPractice(){getSharedPreferences("prefs",0).edit().putInt("words",getSharedPreferences("prefs",0).getInt("words",0)+1).apply();}
  void progress(){
    base();content.addView(heading("Your Progress"));
    int words=getSharedPreferences("prefs",0).getInt("words",0);
    int sessions=getSharedPreferences("prefs",0).getInt("sessions",0);
    content.addView(tv("Practice dashboard",16));
    TextView stats=tv("Words practiced: "+words+"\n\nReading sessions: "+sessions+"\n\nLevel: "+(1+words/10)+"\n\nXP: "+(words*10),21);stats.setPadding(dp(18),dp(20),dp(18),dp(20));content.addView(stats);
    String badge=words>=10?"Achievement unlocked: 10 Words":"Next achievement: practice "+Math.max(1,10-words)+" more word(s).";
    TextView b=tv(badge,17);b.setTextColor(purple);title(b);content.addView(b);
  }
  void settings(){
    base();content.addView(heading("Reading Settings"));
    TextView label=tv("Text size: "+Math.round(scale*100)+"%",16);content.addView(label);
    SeekBar seek=new SeekBar(this);seek.setMax(80);seek.setProgress((int)((scale-.8f)*100));content.addView(seek);
    seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int p,boolean f){scale=.8f+p/100f;label.setText("Text size: "+Math.round(scale*100)+"%");savePrefs();}public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}});
    TextView speed=tv("Speech speed: "+String.format(Locale.US,"%.1fx",speechRate),16);content.addView(speed);
    SeekBar rate=new SeekBar(this);rate.setMax(10);rate.setProgress(3);content.addView(rate);
    rate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int p,boolean f){speechRate=.7f+p*.1f;speed.setText("Speech speed: "+String.format(Locale.US,"%.1fx",speechRate));}public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}});
    Button contrast=btn(highContrast?"✓ High Contrast: ON":"High Contrast: OFF");content.addView(contrast);contrast.setOnClickListener(v->{highContrast=!highContrast;savePrefs();settings();});
    Button theme=btn(dark?"☀ Light Mode":"☾ Dark Mode");content.addView(theme);theme.setOnClickListener(v->{dark=!dark;savePrefs();settings();});
    TextView preview=tv("Reading preview\n\nThe quick brown fox jumps over the lazy dog.",20*scale);preview.setPadding(dp(18),dp(18),dp(18),dp(18));preview.setBackgroundColor(highContrast?Color.WHITE:(dark?Color.rgb(35,35,45):Color.WHITE));preview.setTextColor(highContrast?Color.BLACK:fg());content.addView(preview);
  }
  @Override protected void onDestroy(){if(tts!=null)tts.shutdown();super.onDestroy();}
}