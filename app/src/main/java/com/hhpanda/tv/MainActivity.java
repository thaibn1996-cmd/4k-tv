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

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectSpatialNavigation(view);
            }
        });

        webView.loadUrl("https://hhpanda.st/");
    }

    // HỆ THỐNG ĐIỀU HƯỚNG TỌA ĐỘ KHÔNG GIAN CHO ANDROID TV
    private void injectSpatialNavigation(WebView view) {
        String script = "javascript:(function() {" +
            "if (window.hasSpatialNav) return;" +
            "window.hasSpatialNav = true;" +
            "" +
            "let currentIndex = 0;" +
            "let focusableElements = [];" +
            "" +
            "function updateElements() {" +
            "    // Gom tất cả các liên kết poster, menu, tab lịch chiếu và nút bấm có thật trên trang" +
            "    const raw = document.querySelectorAll('a.halim-trending-link, a.halim-thumb, header a, .navbar a, .halim-schedule-block a, .halim-schedule-block-mobile a, .page-numbers, button');" +
            "    focusableElements = Array.from(raw).filter(el => {" +
            "        const rect = el.getBoundingClientRect();" +
            "        return rect.width > 0 && rect.height > 0 && window.getComputedStyle(el).visibility !== 'hidden';" +
            "    });" +
            "    if (focusableElements.length > 0 && currentIndex >= focusableElements.length) {" +
            "        currentIndex = 0;" +
            "    }" +
            "}" +
            "" +
            "function renderFocus() {" +
            "    updateElements();" +
            "    focusableElements.forEach((el, idx) => {" +
            "        if (idx === currentIndex) {" +
            "            el.style.outline = '4px solid #00e5ff';" +
            "            el.style.transform = 'scale(1.06)';" +
            "            el.style.boxShadow = '0 0 25px rgba(0, 229, 255, 0.9)';" +
            "            el.style.zIndex = '999999';" +
            "            el.style.transition = 'all 0.15s ease';" +
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
            "setTimeout(renderFocus, 1000);" +
            "" +
            "// Thuật toán tìm phần tử gần nhất dựa trên khoảng cách tọa độ màn hình (X, Y)" +
            "function moveFocus(dir) {" +
            "    updateElements();" +
            "    if (focusableElements.length === 0) return;" +
            "" +
            "    const currentEl = focusableElements[currentIndex] || focusableElements[0];" +
            "    const curRect = currentEl.getBoundingClientRect();" +
            "    let bestIndex = currentIndex;" +
            "    let minScore = Infinity;" +
            "" +
            "    focusableElements.forEach((el, idx) => {" +
            "        if (idx === currentIndex) return;" +
            "        const rect = el.getBoundingClientRect();" +
            "        const dx = rect.left + rect.width/2 - (curRect.left + curRect.width/2);" +
            "        const dy = rect.top + rect.height/2 - (curRect.top + curRect.height/2);" +
            "" +
            "        let isValidDirection = false;" +
            "        let score = Infinity;" +
            "" +
            "        if (dir === 'right' && dx > 0) {" +
            "            score = Math.abs(dy) * 2 + dx;" +
            "            isValidDirection = true;" +
            "        } else if (dir === 'left' && dx < 0) {" +
            "            score = Math.abs(dy) * 2 + Math.abs(dx);" +
            "            isValidDirection = true;" +
            "        } else if (dir === 'down' && dy > 0) {" +
            "            score = Math.abs(dx) * 2 + dy;" +
            "            isValidDirection = true;" +
            "        } else if (dir === 'up' && dy < 0) {" +
            "            score = Math.abs(dx) * 2 + Math.abs(dy);" +
            "            isValidDirection = true;" +
            "        }" +
            "" +
            "        if (isValidDirection && score < minScore) {" +
            "            minScore = score;" +
            "            bestIndex = idx;" +
            "        }" +
            "    });" +
            "" +
            "    if (bestIndex !== currentIndex) {" +
            "        currentIndex = bestIndex;" +
            "        renderFocus();" +
            "    }" +
            "}" +
            "" +
            "document.addEventListener('keydown', function(e) {" +
            "    if (e.key === 'ArrowRight') { e.preventDefault(); moveFocus('right'); }" +
            "    else if (e.key === 'ArrowLeft') { e.preventDefault(); moveFocus('left'); }" +
            "    else if (e.key === 'ArrowDown') { e.preventDefault(); moveFocus('down'); }" +
            "    else if (e.key === 'ArrowUp') { e.preventDefault(); moveFocus('up'); }" +
            "    else if (e.key === 'Enter' || e.keyCode === 13 || e.keyCode === 23) {" +
            "        e.preventDefault();" +
            "        if (focusableElements[currentIndex]) {" +
            "            focusableElements[currentIndex].click();" +
            "            setTimeout(renderFocus, 2000);" +
            "        }" +
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
