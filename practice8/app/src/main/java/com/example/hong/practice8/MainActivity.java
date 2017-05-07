package com.example.hong.practice8;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    EditText editText;
    Button btn;
    Animation anim;
    LinearLayout L1;
    ListView listView;
    ArrayList<String> data = new ArrayList<String>();
    ArrayAdapter<String> adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn1);
        webView = (WebView) findViewById(R.id.webview);
        editText = (EditText) findViewById(R.id.edittext);
        L1 = (LinearLayout) findViewById(R.id.linearlayout);
        listView = (ListView) findViewById(R.id.listview);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line
                , data);
        listView.setAdapter(adapter);
        anim = AnimationUtils.loadAnimation(this, R.anim.translate_top);
        webView.loadUrl("http://www.naver.com");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavaScriptMethods(), "MyApp");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,
                                                    WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                editText.setText(url);
            }


        });

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                L1.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,
                                                    WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                editText.setText(url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.setMessage("Loading...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int
                    newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 100) dialog.dismiss();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(view.getContext());
                dlg.setTitle("삭제확인");
                dlg.setIcon(R.mipmap.ic_launcher);
                dlg.setMessage("선택한 즐겨찾기 목록을 삭제하실건가요?");
                dlg.setNegativeButton("취소", null);
                dlg.setPositiveButton("삭제",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                data.remove(position);
                                Toast.makeText(getApplicationContext(),"삭제되었습니다", Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                            }
                        });
                dlg.show();

                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                L1.setVisibility(View.VISIBLE);
                listView.setVisibility(View.INVISIBLE);
                webView.setVisibility(View.VISIBLE);
                int pointer = data.get(position).indexOf(" ");
                String url = data.get(position).substring(pointer + 1);
                if (url.contains("http")) {
                    webView.loadUrl(url);
                } else {
                    webView.loadUrl("http://" + url);
                }
            }
        });
    }

    public void onClick(View v) {
        if (v == btn) {
            webView.loadUrl(editText.getText().toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                L1.setVisibility(View.VISIBLE);
                listView.setVisibility(View.INVISIBLE);
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(("file:///android_asset/www/urladd.html"));
                L1.setAnimation(anim);
                anim.start();
                break;
            case R.id.list:
                listView.setVisibility(View.VISIBLE);
                webView.setVisibility(View.INVISIBLE);
                L1.setVisibility(View.GONE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    Handler handler = new Handler();

    class JavaScriptMethods {

        @JavascriptInterface//웹페이지에서 javascript로 사용할 것이다.
        public void displayToast() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    L1.setVisibility(View.VISIBLE);
                }
            });
        }

        @JavascriptInterface
        public void addfavoritelist(final String name, final String url) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (compare(url)) {
                        data.add("<" + name + ">" + " " + url);
                        adapter.notifyDataSetChanged();
                        String msg = name + " 이(가) 등록되었습니다.";
                        webView.loadUrl("javascript:setMsg('" + msg + "')");
                    } else {
                        webView.loadUrl("javascript:displayMsg()");
                    }

                }
            });
        }
    }

    boolean compare(String url) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).contains(url)) {
                return false;
            }
        }
        return true;
    }
}

