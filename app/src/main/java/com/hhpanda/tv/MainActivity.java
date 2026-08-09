package com.hhpanda.tv;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Nhúng mã nguồn quản lý Focus và Highlight kiểu Android TV khi trang web tải xong
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectTvFocusEngine(view);
            }
        });

        webView.loadUrl("https://hhpanda.st/");
    }

    // HỆ THỐNG ĐIỀU HƯỚNG FOCUS & HIGHLIGHT KIỂU ANDROID TV
    private void injectTvFocusEngine(WebView view) {
        String script = "javascript:(function() {" +
            "if (window.hasTvFocusEngine) return;" +
            "window.hasTvFocusEngine = true;" +
            "" +
            "let currentIndex = 0;" +
            "let focusableElements = [];" +
            "" +
            "function updateFocusables() {" +
            "    const raw = document.querySelectorAll('a, button, input, [tabindex=\"0\"]');" +
            "    focusableElements = Array.from(raw).filter(el => {" +
            "        const rect = el.getBoundingClientRect();" +
            "        return rect.width > 0 && rect.height > 0 && window.getComputedStyle(el).visibility !== 'hidden';" +
            "    });" +
            "    if (focusableElements.length > 0 && currentIndex >= focusableElements.length) {" +
            "        currentIndex = 0;" +
            "    }" +
            "}" +
            "" +
            "function applyFocus() {" +
            "    updateFocusables();" +
            "    focusableElements.forEach((el, idx) => {" +
            "        if (idx === currentIndex) {" +
            "            // Hiệu ứng phát sáng, phóng to và đổi màu viền giống app TV chuẩn" +
            "            el.style.outline = '4px solid #00e5ff';" +
            "            el.style.transform = 'scale(1.07);';" +
            "            el.style.boxShadow = '0 0 25px rgba(0, 229, 255, 0.9)';" +
            "            el.style.zIndex = '999999';" +
            "            el.style.transition = 'all 0.2s ease';" +
            "            el.scrollIntoView({ behavior: 'smooth', block: 'center', inline: 'center' });" +
            "        } else {" +
            "            el.style.outline = '';" +
            "            el.style.transform = '';" +
            "            el.style.boxShadow = '';" +
            "            el.style.zIndex = '';" +
            "        }" +
            "    });" +
            "}" +
            "" +
            "// Khởi tạo focus sau khi trang load xong" +
            "setTimeout(applyFocus, 800);" +
            "" +
            "// Lắng nghe sự kiện phím bấm trên Remote TV" +
            "document.addEventListener('keydown', function(e) {" +
            "    updateFocusables();" +
            "    if (focusableElements.length === 0) return;" +
            "" +
            "    let moved = false;" +
            "    const columns = 4; // Giả lập bố cục lưới khoảng 4 cột phim" +
            "" +
            "    if (e.key === 'ArrowRight') {" +
            "        currentIndex = Math.min(currentIndex + 1, focusableElements.length - 1);" +
            "        moved = true;" +
            "    } else if (e.key === 'ArrowLeft') {" +
            "        currentIndex = Math.max(currentIndex - 1, 0);" +
            "        moved = true;" +
            "    } else if (e.key === 'ArrowDown') {" +
            "        currentIndex = Math.min(currentIndex + columns, focusableElements.length - 1);" +
            "        moved = true;" +
            "    } else if (e.key === 'ArrowUp') {" +
            "        currentIndex = Math.max(currentIndex - columns, 0);" +
            "        moved = true;" +
            "    } else if (e.key === 'Enter' || e.keyCode === 13 || e.keyCode === 23) {" +
            "        e.preventDefault();" +
            "        if (focusableElements[currentIndex]) {" +
            "            focusableElements[currentIndex].click();" +
            "            // Đợi trang mới load xong thì reset lại focus" +
            "            setTimeout(applyFocus, 1500);" +
            "        }" +
            "    }" +
            "" +
            "    if (moved) {" +
            "        e.preventDefault();" +
            "        applyFocus();" +
            "    }" +
            "}, true);" +
            "})();";
        view.evaluateJavascript(script, null);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
